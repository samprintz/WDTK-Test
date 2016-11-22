package de.sampri.wd2xlisa;

import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.EntityTimerProcessor;
import org.wikidata.wdtk.dumpfiles.MwLocalDumpFile;
import org.wikidata.wdtk.examples.ExampleHelpers;

public class Main {

	/**
	 * The dump which entites should be processed..
	 */
	private final static String DUMP_FILE = "B:/20161107-wikidata_dump/dumpfiles/wikidatawiki/json-20161031/20161031-head-1000.json.gz";
	// private final static String DUMP_FILE =
	// "C:/Daten/Eclipse/wdtk-parent/wdtk-examples/dumpfiles/wikidatawiki/json-20161031/20161031.json.gz";
	// private final static String DUMP_FILE =
	// "./src/main/resources/sample-dump-20150815.json.gz";

	/**
	 * Incremented with each processed entity. Contains the number of processed
	 * entities after the dump was processed.
	 */
	int itemCount = 0;

	public static void main(String[] args) {
		ExampleHelpers.configureLogging();

		// Select dump file
		MwLocalDumpFile mwDumpFile = new MwLocalDumpFile(DUMP_FILE, DumpContentType.JSON, "20161031", "wikidatawiki");

		int distinctSitelinks = runSitelinksCounter(mwDumpFile);

		runSurfaceFormsCounter(mwDumpFile);

		runIndexGeneratorByEntity(mwDumpFile, distinctSitelinks);

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
		DumpProcessingController dumpProcessingControllerCountSF = new DumpProcessingController("wikidatawiki");
		dumpProcessingControllerCountSF.setOfflineMode(true);

		// Instantiate SurfaceForms Counter
		SurfaceFormsCounter surfaceFormsCounter = new SurfaceFormsCounter();
		dumpProcessingControllerCountSF.registerEntityDocumentProcessor(surfaceFormsCounter, null, true);

		dumpProcessingControllerCountSF.processDump(mwDumpFile);

		surfaceFormsCounter.printStatus();
	}

	private static void runIndexGeneratorByEntity(MwLocalDumpFile mwDumpFile, int distinctSiteLinks) {
		// Instantiate Dump Processor Controller for Index Generator
		DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
		dumpProcessingController.setOfflineMode(true);

		// Instantiale Index Generator and Timer Processor
		IndexGeneratorByEntity indexGeneratorByEntity = new IndexGeneratorByEntity();
		dumpProcessingController.registerEntityDocumentProcessor(indexGeneratorByEntity, null, true);
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(entityTimerProcessor, null, true);

		indexGeneratorByEntity.open();
		indexGeneratorByEntity.distinctSitelinks = distinctSiteLinks;
		dumpProcessingController.processDump(mwDumpFile);

		// indexGeneratorByEntity.processItemDocumentById("Q1726");

		entityTimerProcessor.close();
	}

}
