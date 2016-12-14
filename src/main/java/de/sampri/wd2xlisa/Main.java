package de.sampri.wd2xlisa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
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

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

	static final Logger logger = Logger.getRootLogger();

	/**
	 * The dump which entites should be processed.
	 */
	// TODO als Parameter?
	private final static String DUMP_FILE = "B:/20161107-wikidata_dump/dumpfiles/wikidatawiki/json-20161031/20161031-head-10000.json.gz";
	// private final static String DUMP_FILE =
	// "src/main/resources/20161031.json.gz";
	// private final static String DUMP_FILE =
	// "src/main/resources/20161031-head-1000.json.gz";

	// Select dump file
	private final static MwLocalDumpFile mwDumpFile = new MwLocalDumpFile(DUMP_FILE, DumpContentType.JSON, "20161031",
			"wikidatawiki");

	/**
	 * Incremented with each processed entity. Contains the number of processed
	 * entities after the dump was processed.
	 */
	int itemCount = 0;

	/**
	 * The result, the generated Index JSON file, will be saved here.
	 */
	private static final String OUTPUT_PATH = "results/";
	private static final String INDEX_FILE = "-index.json";
	private static final String SFFORM_FILE = "-sfforms.txt";
	private static final String LOG_PATH = "logs/";
	private static final String LOG_FILE = "-log.log";

	public static void main(String[] args) {
		// Create directories
		File dir = new File(OUTPUT_PATH);
		dir.mkdirs();
		dir = new File(LOG_PATH);
		dir.mkdirs();

		configureLogging();

		// Get JSON Generator
		JsonGenerator jsonGenerator = getJsonGenerator();

		// Count Sitelinks
		int distinctSitelinks = runSitelinksCounter();

		// Get all Surface Forms
		ConcurrentMap<String, Integer> distinctSurfaceForms = runSurfaceFormsCounter();

		// Create Entity Index
		runEntityIndexGenerator(jsonGenerator, distinctSitelinks);

		// Create Surface Form Index
		runSurfaceFormIndexGenerator(distinctSurfaceForms);

		// Create Sense Index
		runSenseIndexGenerator(mwDumpFile);

	}

	private static void configureLogging() {
		PatternLayout layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p - %m%n"); // %c{1}:%L

		ConsoleAppender consoleAppender = new ConsoleAppender(layout);
		consoleAppender.setThreshold(Level.INFO); // ALL, DEBUG, INFO, WARN,
													// ERROR, FATAL, OFF
		consoleAppender.activateOptions();
		logger.addAppender(consoleAppender);

		try {
			FileAppender fileAppender = new FileAppender(layout, LOG_PATH + getTimeStamp() + LOG_FILE, false);
			logger.addAppender(fileAppender);
		} catch (IOException e) {
			System.out.println("File logger could not be initialized: " + e);
			e.printStackTrace();
		}
	}

	static String getTimeStamp() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}

	private static JsonGenerator getJsonGenerator() {
		JsonFactory jsonFactory = new JsonFactory();
		JsonGenerator jsonGenerator = null;

		String filepath = OUTPUT_PATH + getTimeStamp() + INDEX_FILE;
		FileOutputStream file;

		try {
			file = new FileOutputStream(new File(filepath));
			try {
				jsonGenerator = jsonFactory.createGenerator(file, JsonEncoding.UTF8);
				jsonGenerator.setCodec(new ObjectMapper());
				jsonGenerator.setPrettyPrinter(new MinimalPrettyPrinter(""));
			} catch (IOException e) {
				logger.error("Result could not be written into JSON file.");
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			logger.error("JSON file for result was not found.");
			e.printStackTrace();
		}
		return jsonGenerator;

		// TODO Ist noch kein JSON array, schließende Klammer wird irgendwie
		// nicht geschrieben (s.u.)
		// jsonGen.writeRaw("[");
	}

	private static int runSitelinksCounter() {
		logger.info("Start counting of distinct sitelinks.");

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

	private static ConcurrentMap<String, Integer> runSurfaceFormsCounter() {
		logger.info("Start creation of surface forms index.");

		// Instantiate Dump Processor Controller for SurfaceForms Counter
		DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
		dumpProcessingController.setOfflineMode(true);

		// Instantiate SurfaceForms Counter
		SurfaceFormsCounter surfaceFormsCounter = new SurfaceFormsCounter(logger);
		dumpProcessingController.registerEntityDocumentProcessor(surfaceFormsCounter, null, true);
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(entityTimerProcessor, null, true);

		dumpProcessingController.processDump(mwDumpFile);

		surfaceFormsCounter.logStatus();

		String filepath = OUTPUT_PATH + getTimeStamp() + SFFORM_FILE;

		surfaceFormsCounter.writeToFile(filepath);

		logger.info("Finished creation of surface form list. File at " + filepath);

		return surfaceFormsCounter.getResult();
	}

	private static void runEntityIndexGenerator(JsonGenerator jsonGenerator,
			int distinctSitelinks) {
		logger.info("Start creation of entity index.");

		// Instantiate Dump Processor Controller for Index Generator
		DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
		dumpProcessingController.setOfflineMode(true);

		// Instantiale Index Generator and Timer Processor
		IndexGeneratorByEntity indexGeneratorByEntity = new IndexGeneratorByEntity(logger, jsonGenerator,
				distinctSitelinks);
		dumpProcessingController.registerEntityDocumentProcessor(indexGeneratorByEntity, null, true);
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(entityTimerProcessor, null, true);

		dumpProcessingController.processDump(mwDumpFile);

		entityTimerProcessor.close();

		// indexGeneratorByEntity.logStatus();

		String filepath = OUTPUT_PATH + getTimeStamp() + INDEX_FILE;

		indexGeneratorByEntity.writeToFile(filepath);

		logger.info("Finished creation of entity index. File at " + filepath);

		// indexGeneratorByEntity.processItemDocumentById("Q1726");
	}

	private static void runSurfaceFormIndexGenerator(ConcurrentMap<String,Integer> distinctSurfaceForms) {
//		distinctSurfaceForms
		for (Map.Entry<String, Integer> sf : distinctSurfaceForms.entrySet()) {
			
		}
	}

	private static void runSenseIndexGenerator(MwLocalDumpFile mwDumpFile) {
		// TODO Auto-generated method stub

	}

}
