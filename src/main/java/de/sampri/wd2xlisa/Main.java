package de.sampri.wd2xlisa;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.EntityTimerProcessor;
import org.wikidata.wdtk.dumpfiles.MwLocalDumpFile;

import de.sampri.wd2xlisa.model.EntityBlock;
import de.sampri.wd2xlisa.model.Index;
import de.sampri.wd2xlisa.model.SenseBlock;
import de.sampri.wd2xlisa.model.SurfaceFormBlock;

public class Main {

	/**
	 * The logger. Logs to console and file which path is specified in
	 * {@link #LOG_PATH} and {@link #LOG_FILE}.}
	 */
	static final Logger logger = Logger.getRootLogger();

	// TODO als Parameter?
	/**
	 * The dump which entites should be processed.
	 */
	// private final static String DUMP_FILE =
	// "src/main/resources/20161031.json.gz";
	private final static String DUMP_FILE = "B:/20161107-wikidata_dump/dumpfiles/wikidatawiki/json-20161031/20161031-head-10000.json.gz";

	/**
	 * Contains the dump file
	 */
	private final static MwLocalDumpFile mwDumpFile = new MwLocalDumpFile(DUMP_FILE, DumpContentType.JSON, "20161031",
			"wikidatawiki");

	/**
	 * The results will be saved here.
	 */
	private static final String OUTPUT_PATH = "results/";

	/**
	 * Postfix of the JSON file for of the entity index.
	 */
	private static final String ENTITY_INDEX_FILE = "-entity-index.json";

	/**
	 * Postfix of the JSON file for of the surface form index.
	 */
	private static final String SFFORM_INDEX_FILE = "-sfform-index.json";

	/**
	 * Postfix of the JSON file for of the sense index.
	 */
	private static final String SENSE_INDEX_FILE = "-sense-index.json";

	/**
	 * The logs will be saved here.
	 */
	private static final String LOG_PATH = "logs/";

	/**
	 * Postfix of the log file.
	 */
	private static final String LOG_FILE = "-log.log";

	public static void main(String[] args) {
		// Create directories
		File dir = new File(OUTPUT_PATH);
		dir.mkdirs();
		dir = new File(LOG_PATH);
		dir.mkdirs();

		configureLogging();

		logger.info("=== Preprocessing ===");

		// Count Sitelinks
		int distinctSitelinks = getDistinctSitelinks();

		// Get all Surface Forms
		ConcurrentMap<String, Integer> distinctSurfaceForms = getDistinctSurfaceForms();

		logger.info("");
		logger.info("=== Processing ===");

		// Create Entity Index
		runEntityIndexGenerator(distinctSitelinks);

		// Create Surface Form Index
		runSurfaceFormIndexGenerator(distinctSurfaceForms);

		// Create Sense Index
		runSenseIndexGenerator(distinctSurfaceForms);

		logger.info("");
		logger.info("Done.");

	}

	private static void configureLogging() {
		PatternLayout layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p - %m%n"); // %c{1}:%L

		ConsoleAppender consoleAppender = new ConsoleAppender(layout);
		consoleAppender.setThreshold(Level.INFO); // ALL, DEBUG, INFO, WARN,
													// ERROR, FATAL, OFF
		consoleAppender.activateOptions();
		logger.addAppender(consoleAppender);

		try {
			FileAppender fileAppender = new FileAppender(layout, LOG_PATH + Helper.getTimeStamp() + LOG_FILE, false);
			logger.addAppender(fileAppender);
		} catch (IOException e) {
			System.out.println("File logger could not be initialized: " + e);
			e.printStackTrace();
		}
	}

	private static int getDistinctSitelinks() {
		logger.info("> Start counting of distinct sitelinks...");

		// Instantiate Dump Processor Controller for Sitelinks Counter
		DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
		dumpProcessingController.setOfflineMode(true);

		// Instantiate Sitelinks Counter
		SitelinksCounter sitelinksCounter = new SitelinksCounter();
		dumpProcessingController.registerEntityDocumentProcessor(sitelinksCounter, null, true);
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(entityTimerProcessor, null, true);

		dumpProcessingController.processDump(mwDumpFile);

		entityTimerProcessor.close();

		logger.info("Finished counting of distinct sitelinks.");

		return sitelinksCounter.getResult();
	}

	private static ConcurrentMap<String, Integer> getDistinctSurfaceForms() {
		logger.info("> Start collecting distinct surface forms...");

		// Instantiate Dump Processor Controller for SurfaceForms Counter
		DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
		dumpProcessingController.setOfflineMode(true);

		// Instantiate SurfaceForms Counter
		SurfaceFormsCounter surfaceFormsCounter = new SurfaceFormsCounter(logger);
		dumpProcessingController.registerEntityDocumentProcessor(surfaceFormsCounter, null, true);
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(entityTimerProcessor, null, true);

		dumpProcessingController.processDump(mwDumpFile);

		entityTimerProcessor.close();

		surfaceFormsCounter.logStatus();

		// String filepath = OUTPUT_PATH + Helper.getTimeStamp() + SFFORM_FILE;
		// surfaceFormsCounter.writeToFile(filepath);

		logger.info("Finished collecting of distinct surface forms.");

		return surfaceFormsCounter.getResult();
	}

	private static void runEntityIndexGenerator(int distinctSitelinks) {
		logger.info("> Start creation of entity index...");

		// Instantiate Dump Processor Controller for Index Generator
		DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
		dumpProcessingController.setOfflineMode(true);

		// Instantiale Index Generator and Timer Processor
		IndexGeneratorByEntity indexGenerator = new IndexGeneratorByEntity(logger, distinctSitelinks);
		dumpProcessingController.registerEntityDocumentProcessor(indexGenerator, null, true);
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(entityTimerProcessor, null, true);

		dumpProcessingController.processDump(mwDumpFile);

		entityTimerProcessor.close();

		// indexGenerator.generateIndex();
		Index<EntityBlock> index = indexGenerator.getIndex();

		// writeToFile
		String filepath = OUTPUT_PATH + Helper.getTimeStamp() + ENTITY_INDEX_FILE;
		index.writeToFile(filepath, logger);
		// indexGeneratorByEntity.writeToFile(filepath);

		logger.info("Finished creation of entity index. File at " + filepath);

		// indexGeneratorByEntity.processItemDocumentById("Q1726");
	}

	private static void runSurfaceFormIndexGenerator(ConcurrentMap<String, Integer> distinctSurfaceForms) {
		logger.info("> Start creation of surface form index...");

		IndexGeneratorBySurfaceForm indexGenerator = new IndexGeneratorBySurfaceForm();
		indexGenerator.generateIndex(distinctSurfaceForms);
		Index<SurfaceFormBlock> index = indexGenerator.getIndex();

		String filepath = OUTPUT_PATH + Helper.getTimeStamp() + SFFORM_INDEX_FILE;
		index.writeToFile(filepath, logger);

		logger.info("Finished creation of surface form index. File at " + filepath);
	}

	private static void runSenseIndexGenerator(ConcurrentMap<String, Integer> distinctSurfaceForms) {

		logger.info("> Start creation of sense index...");

		// Instantiate Dump Processor Controller for Index Generator
		DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
		dumpProcessingController.setOfflineMode(true);

		// Instantiale Index Generator and Timer Processor
		IndexGeneratorBySense indexGenerator = new IndexGeneratorBySense(logger);
		dumpProcessingController.registerEntityDocumentProcessor(indexGenerator, null, true);
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(entityTimerProcessor, null, true);

		dumpProcessingController.processDump(mwDumpFile);

		entityTimerProcessor.close();

		// indexGenerator.generateIndex();
		Index<SenseBlock> index = indexGenerator.getIndex();

		// writeToFile
		String filepath = OUTPUT_PATH + Helper.getTimeStamp() + SENSE_INDEX_FILE;
		index.writeToFile(filepath, logger);
		// indexGeneratorByEntity.writeToFile(filepath);

		logger.info("Finished creation of sense index. File at " + filepath);
	}

}
