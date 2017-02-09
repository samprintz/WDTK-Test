package de.sampri.wd2xlisa;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
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

import de.sampri.wd2xlisa.edp.DbpediaMappingsGenerator;
import de.sampri.wd2xlisa.edp.EntityIndexGenerator;
import de.sampri.wd2xlisa.edp.SenseIndexGenerator;
import de.sampri.wd2xlisa.edp.SurfaceFormIndexGenerator;
import de.sampri.wd2xlisa.edp.LongestSurfaceFormFinder;
import de.sampri.wd2xlisa.edp.SitelinksCounter;
import de.sampri.wd2xlisa.edp.SurfaceFormsCollectorByLang;
import de.sampri.wd2xlisa.edp.SurfaceFormsCollector;
import de.sampri.wd2xlisa.model.EntityBlock;
import de.sampri.wd2xlisa.model.EntityDbpediaMapping;
import de.sampri.wd2xlisa.model.Index;
import de.sampri.wd2xlisa.model.SenseBlock;
import de.sampri.wd2xlisa.model.SurfaceFormBlock;

public class Main {

	/**
	 * The logger. Logs to console and file which path is specified in
	 * {@link #LOG_PATH} and {@link #LOG_FILE}.}
	 */
	static final Logger logger = Logger.getRootLogger();

	/**
	 * The dump which entites should be processed.
	 */
	// private final static String DEFAULT_DUMP_FILE =
	// "src/main/resources/20161031.json.gz";
	private final static String DEFAULT_DUMP_FILE = "B:/dumps/20161107-wikidata_dump/dumpfiles/wikidatawiki/json-20161031/20161031-head-10000.json.gz";

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
	 * Postfix of the JSON file for of the Mappings between Wikidata entities
	 * and their corresponding DBpedia URL.
	 */
	private static final String DBPEDIA_MAPPING_FILE = "-entity-dbpedia-mapping.json";

	/**
	 * The logs will be saved here.
	 */
	private static final String LOG_PATH = "logs/";

	/**
	 * Postfix of the log file.
	 */
	private static final String LOG_FILE = "-log.log";

	/**
	 * Contains the dump file
	 */
	private static MwLocalDumpFile mwDumpFile = null;

	public static void main(String[] args) {
		String modeInput;
		Helper.Mode mode = null;

		String dumpFileInput;

		configureLogging();

		if (args.length != 2) {
			Scanner scanner = new Scanner(System.in);

			System.out.println(
					"Please input path to dump file, or press Enter for default path (" + DEFAULT_DUMP_FILE + "):");
			dumpFileInput = scanner.nextLine();

			if (dumpFileInput.length() == 0) {
				dumpFileInput = DEFAULT_DUMP_FILE;
			}

			System.out.println("Please select mode (entity, sfform, sense, dbpedia, longest_sfform):");
			modeInput = scanner.nextLine();

			scanner.close();
		} else {
			modeInput = args[0];
			dumpFileInput = args[1];
		}

		if (modeInput.equalsIgnoreCase("entity")) {
			mode = Helper.Mode.ENTITY_INDEX;
		} else if (modeInput.equalsIgnoreCase("sfform")) {
			mode = Helper.Mode.SFFORM_INDEX;
		} else if (modeInput.equalsIgnoreCase("sense")) {
			mode = Helper.Mode.SENSE_INDEX;
		} else if (modeInput.equalsIgnoreCase("longest_sfform")) {
			mode = Helper.Mode.LONGEST_SFFORM;
		} else if (modeInput.equalsIgnoreCase("dbpedia")) {
			mode = Helper.Mode.DBPEDIA_MAPPING;
		} else {
			System.out.println(
					"Invalid mode: " + modeInput + ". Must be entity, sfform, sense, dbpedia or longest_sfform.");
			System.exit(0);
		}

		// Dump file
		mwDumpFile = new MwLocalDumpFile(dumpFileInput, DumpContentType.JSON, "20161031", "wikidatawiki");

		if (mwDumpFile == null || mwDumpFile.isAvailable() == false) {
			System.out.println("Dump file (" + dumpFileInput + ") not found.");
			System.exit(0);
		}

		logger.info("Create " + modeInput + " index from dump file " + dumpFileInput + ".");

		// Create directories
		File dir = new File(OUTPUT_PATH);
		dir.mkdirs();
		dir = new File(LOG_PATH);
		dir.mkdirs();

		switch (mode) {
		case ENTITY_INDEX:
			logger.info("=== Preprocessing ===");
			// Count Sitelinks
			int distinctSitelinks = getDistinctSitelinks();
			logger.info("");
			logger.info("=== Processing ===");
			// Create Entity Index
			runEntityIndexGenerator(distinctSitelinks);
			break;

		case SFFORM_INDEX:
			logger.info("=== Preprocessing ===");
			// Get all distinct Surface Forms
			ConcurrentMap<String, Integer> distinctSurfaceForms = getDistinctSurfaceForms();
			logger.info("");
			logger.info("=== Processing ===");
			// Create Surface Form Index
			runSurfaceFormIndexGenerator(distinctSurfaceForms);
			break;

		case SENSE_INDEX:
			logger.info("=== Preprocessing ===");
			// Get for each language all distinct Surface Forms
			HashMap<String, ConcurrentMap<String, Integer>> distinctSurfaceFormsByLang = getDistinctSurfaceFormsByLang();
			logger.info("");
			logger.info("=== Processing ===");
			// Create Sense Index
			runSenseIndexGenerator(distinctSurfaceFormsByLang);
			break;

		case LONGEST_SFFORM:
			logger.info("=== Processing ===");
			// Find the surface form containing most words
			runLongestSurfaceFormFinder();
			break;

		case DBPEDIA_MAPPING:
			logger.info("=== Processing ===");
			// Create DBpedia Mapping
			runDbpediaMappingGenerator();
			break;
		default:
			break;
		}

		logger.info("");
		logger.info("Done.");

	}

	public static void configureLogging() {
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

	/**
	 * Runs the {@link SitelinksCounter}.
	 * 
	 * @return list of all distinct sitelinks in the dump.
	 */
	public static int getDistinctSitelinks() {
		logger.info("> Start counting of distinct sitelinks...");
		long startTime = System.nanoTime();

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

		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		logger.info("Finished counting of distinct sitelinks (" + duration + " ms).");

		return sitelinksCounter.getResult();
	}

	/**
	 * Runs the {@link SurfaceFormsCollector}.
	 * 
	 * @return the the list of all distinct surface forms with the frequency
	 *         they appear.
	 */
	public static ConcurrentMap<String, Integer> getDistinctSurfaceForms() {
		logger.info("> Start collecting distinct surface forms...");
		long startTime = System.nanoTime();

		// Instantiate Dump Processor Controller for SurfaceForms Counter
		DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
		dumpProcessingController.setOfflineMode(true);

		// Instantiate SurfaceForms Counter
		SurfaceFormsCollector surfaceFormsCounter = new SurfaceFormsCollector(logger);
		dumpProcessingController.registerEntityDocumentProcessor(surfaceFormsCounter, null, true);
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(entityTimerProcessor, null, true);

		dumpProcessingController.processDump(mwDumpFile);

		entityTimerProcessor.close();

		surfaceFormsCounter.logStatus();

		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		logger.info("Finished collecting of distinct surface forms (" + duration + " ms).");

		return surfaceFormsCounter.getResult();
	}

	/**
	 * Runs the {@link SurfaceFormsCollectorByLang}.
	 * 
	 * @return the the map with all distinct surface forms with the frequency
	 *         they appear, grouped by language.
	 */
	public static HashMap<String, ConcurrentMap<String, Integer>> getDistinctSurfaceFormsByLang() {
		logger.info("> Start collecting distinct surface forms for each language...");
		long startTime = System.nanoTime();

		// Instantiate Dump Processor Controller for SurfaceForms Counter
		DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
		dumpProcessingController.setOfflineMode(true);

		// Instantiate SurfaceForms Counter
		SurfaceFormsCollectorByLang surfaceFormsCollectorByLang = new SurfaceFormsCollectorByLang(logger);
		dumpProcessingController.registerEntityDocumentProcessor(surfaceFormsCollectorByLang, null, true);
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(entityTimerProcessor, null, true);

		dumpProcessingController.processDump(mwDumpFile);

		entityTimerProcessor.close();

		surfaceFormsCollectorByLang.logStatus();

		// surfaceFormsCollectorByLang.printSortedList();

		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		logger.info("Finished collecting of distinct surface forms for each language (" + duration + " ms).");

		return surfaceFormsCollectorByLang.getResult();
	}

	/**
	 * Runs the {@link EntityIndexGenerator} and writes the result to
	 * {@link #ENTITY_INDEX_FILE}.
	 * 
	 * @param distinctSitelinks
	 *            Number of distinct sitelinks.
	 */
	public static void runEntityIndexGenerator(int distinctSitelinks) {
		logger.info("> Start creation of entity index...");
		long startTime = System.nanoTime();

		// Instantiate Dump Processor Controller for Index Generator
		DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
		dumpProcessingController.setOfflineMode(true);

		// Instantiale Index Generator and Timer Processor
		EntityIndexGenerator indexGenerator = new EntityIndexGenerator(logger, distinctSitelinks);
		dumpProcessingController.registerEntityDocumentProcessor(indexGenerator, null, true);
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(entityTimerProcessor, null, true);

		dumpProcessingController.processDump(mwDumpFile);

		entityTimerProcessor.close();

		Index<EntityBlock> index = indexGenerator.getIndex();

		// writeToFile
		String filepath = OUTPUT_PATH + Helper.getTimeStamp() + ENTITY_INDEX_FILE;
		index.writeToFile(filepath, logger);

		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		logger.info("Finished creation of entity index (" + duration + " ms). File at " + filepath);
	}

	/**
	 * Runs the {@link SurfaceFormIndexGenerator} and writes the result to
	 * {@link #SFFORM_INDEX_FILE}.
	 * 
	 * @param distinctSurfaceForms
	 *            Map of distinct surface forms with their frequency in the
	 *            dump.
	 */
	public static void runSurfaceFormIndexGenerator(ConcurrentMap<String, Integer> distinctSurfaceForms) {
		logger.info("> Start creation of surface form index...");
		long startTime = System.nanoTime();

		SurfaceFormIndexGenerator indexGenerator = new SurfaceFormIndexGenerator();
		indexGenerator.generateIndex(distinctSurfaceForms);
		Index<SurfaceFormBlock> index = indexGenerator.getIndex();

		String filepath = OUTPUT_PATH + Helper.getTimeStamp() + SFFORM_INDEX_FILE;
		index.writeToFile(filepath, logger);

		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		logger.info("Finished creation of surface form index (" + duration + " ms). File at " + filepath);
	}

	/**
	 * Runs the {@link SenseIndexGenerator} and writes the result to
	 * {@link #SENSE_INDEX_FILE}.
	 * 
	 * @param distinctSurfaceFormsByLang
	 *            Map of distinct surface forms with their frequency in the
	 *            dump, grouped by language.
	 */
	public static void runSenseIndexGenerator(
			HashMap<String, ConcurrentMap<String, Integer>> distinctSurfaceFormsByLang) {

		logger.info("> Start creation of sense index...");
		long startTime = System.nanoTime();

		// Instantiate Dump Processor Controller for Index Generator
		DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
		dumpProcessingController.setOfflineMode(true);

		// Instantiale Index Generator and Timer Processor
		SenseIndexGenerator indexGenerator = new SenseIndexGenerator(logger, distinctSurfaceFormsByLang);
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

		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		logger.info("Finished creation of sense index (" + duration + " ms). File at " + filepath);
	}

	/**
	 * Runs the {@link LongestSurfaceFormFinder} and prints the result to the
	 * logs.
	 */
	public static void runLongestSurfaceFormFinder() {
		logger.info("> Start finding of the surface form contain most words...");
		long startTime = System.nanoTime();

		// Instantiate Dump Processor Controller for Sitelinks Counter
		DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
		dumpProcessingController.setOfflineMode(true);

		// Instantiate Sitelinks Counter
		LongestSurfaceFormFinder longSfformFinder = new LongestSurfaceFormFinder();
		dumpProcessingController.registerEntityDocumentProcessor(longSfformFinder, null, true);
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(entityTimerProcessor, null, true);

		dumpProcessingController.processDump(mwDumpFile);

		entityTimerProcessor.close();

		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		logger.info("Finished finding of the surface form contain most words (" + duration + " ms).");

		longSfformFinder.printStatistics();
		logger.info(longSfformFinder.getResult());
	}

	/**
	 * Runs the {@link DbpediaMappingGenerator} and writes the result to
	 * {@link #DBPEDIA_MAPPING_FILE}.
	 */
	public static void runDbpediaMappingGenerator() {
		logger.info("> Start creation of DBpedia mappings...");
		long startTime = System.nanoTime();

		// Instantiate Dump Processor Controller for Sitelinks Counter
		DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
		dumpProcessingController.setOfflineMode(true);

		// Instantiate Sitelinks Counter
		DbpediaMappingsGenerator dbpediaMappingsGenerator = new DbpediaMappingsGenerator(logger);
		dumpProcessingController.registerEntityDocumentProcessor(dbpediaMappingsGenerator, null, true);
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(entityTimerProcessor, null, true);

		dumpProcessingController.processDump(mwDumpFile);

		entityTimerProcessor.close();

		Index<EntityDbpediaMapping> index = dbpediaMappingsGenerator.getIndex();
		dbpediaMappingsGenerator.printStatistics();

		// writeToFile
		String filepath = OUTPUT_PATH + Helper.getTimeStamp() + DBPEDIA_MAPPING_FILE;
		index.writeToFile(filepath, logger);

		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		logger.info("Finished creation of DBpedia mappings (" + duration + " ms). File at " + filepath);
	}

}
