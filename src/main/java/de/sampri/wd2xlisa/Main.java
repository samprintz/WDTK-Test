package de.sampri.wd2xlisa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.EntityTimerProcessor;
import org.wikidata.wdtk.dumpfiles.MwLocalDumpFile;
import org.wikidata.wdtk.examples.ExampleHelpers;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

	/**
	 * The dump which entites should be processed..
	 */
	// private final static String DUMP_FILE =
	// "B:/20161107-wikidata_dump/dumpfiles/wikidatawiki/json-20161031/20161031-head-1000.json.gz";
	private final static String DUMP_FILE = "B:/20161107-wikidata_dump/dumpfiles/wikidatawiki/json-20161031/20161031.json.gz";
	// private final static String DUMP_FILE =
	// "src/main/resources/20161031-head-1000.json.gz";

	/**
	 * Incremented with each processed entity. Contains the number of processed
	 * entities after the dump was processed.
	 */
	int itemCount = 0;

	/**
	 * The result, the generated Index JSON file, will be saved here.
	 */
	private static final String OUTPUT_PATH = "results/";
	private static final String OUTPUT_FILE = "-index.json";

	public static void main(String[] args) {
		ExampleHelpers.configureLogging();

		// Select dump file
		MwLocalDumpFile mwDumpFile = new MwLocalDumpFile(DUMP_FILE, DumpContentType.JSON, "20161031", "wikidatawiki");

		// Get JSON Generator
		// JsonGenerator jsonGenerator = getJsonGenerator();

		// int distinctSitelinks = runSitelinksCounter(mwDumpFile);

		runSurfaceFormsCounter(mwDumpFile);

		// runIndexGeneratorByEntity(mwDumpFile, jsonGenerator,
		// distinctSitelinks);

	}

	private static JsonGenerator getJsonGenerator() {
		File dir = new File(OUTPUT_PATH);
		dir.mkdirs();

		JsonFactory jsonFactory = new JsonFactory();
		JsonGenerator jsonGenerator = null;

		String filepath = OUTPUT_PATH + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + OUTPUT_FILE;
		FileOutputStream file;

		try {
			file = new FileOutputStream(new File(filepath));
			try {
				jsonGenerator = jsonFactory.createGenerator(file, JsonEncoding.UTF8);
				jsonGenerator.setCodec(new ObjectMapper());
				jsonGenerator.setPrettyPrinter(new MinimalPrettyPrinter(""));
			} catch (IOException e) {
				System.out.println("Result could not be written into JSON file.");
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			System.out.println("JSON file for result was not found.");
			e.printStackTrace();
		}
		return jsonGenerator;

		// TODO Ist noch kein JSON array, schließende Klammer wird irgendwie
		// nicht geschrieben (s.u.)
		// jsonGen.writeRaw("[");
	}

	private static int runSitelinksCounter(MwLocalDumpFile mwDumpFile) {
		// Instantiate Dump Processor Controller for Sitelinks Counter
		DumpProcessingController dumpProcessingControllerCountSL = new DumpProcessingController("wikidatawiki");
		dumpProcessingControllerCountSL.setOfflineMode(true);

		// Instantiate Sitelinks Counter
		SitelinksCounter sitelinksCounter = new SitelinksCounter();
		dumpProcessingControllerCountSL.registerEntityDocumentProcessor(sitelinksCounter, null, true);

		dumpProcessingControllerCountSL.processDump(mwDumpFile);

		return sitelinksCounter.getResult();
	}

	private static void runSurfaceFormsCounter(MwLocalDumpFile mwDumpFile) {
		// Instantiate Dump Processor Controller for SurfaceForms Counter
		DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
		dumpProcessingController.setOfflineMode(true);

		// Instantiate SurfaceForms Counter
		SurfaceFormsCounter surfaceFormsCounter = new SurfaceFormsCounter();
		dumpProcessingController.registerEntityDocumentProcessor(surfaceFormsCounter, null, true);
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(entityTimerProcessor, null, true);

		dumpProcessingController.processDump(mwDumpFile);

		surfaceFormsCounter.printStatus();
		surfaceFormsCounter.writeToFile();
		System.out.println("Done.");
	}

	private static void runIndexGeneratorByEntity(MwLocalDumpFile mwDumpFile, JsonGenerator jsonGenerator,
			int distinctSiteLinks) {
		// Instantiate Dump Processor Controller for Index Generator
		DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
		dumpProcessingController.setOfflineMode(true);

		// Instantiale Index Generator and Timer Processor
		IndexGeneratorByEntity indexGeneratorByEntity = new IndexGeneratorByEntity(jsonGenerator);
		dumpProcessingController.registerEntityDocumentProcessor(indexGeneratorByEntity, null, true);
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(entityTimerProcessor, null, true);

		indexGeneratorByEntity.distinctSitelinks = distinctSiteLinks;
		dumpProcessingController.processDump(mwDumpFile);

		// indexGeneratorByEntity.processItemDocumentById("Q1726");

		entityTimerProcessor.close();
	}

}
