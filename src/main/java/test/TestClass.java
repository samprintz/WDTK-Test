package test;

import java.io.IOException;

import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.EntityTimerProcessor;
import org.wikidata.wdtk.dumpfiles.MwLocalDumpFile;
import org.wikidata.wdtk.examples.ExampleHelpers;
import org.wikidata.wdtk.examples.LocalDumpFileExample;

public class TestClass {

	private final static String DUMP_FILE = "./src/main/resources/sample-dump-20150815.json.gz";

	public static void main(String[] args) throws IOException {
		ExampleHelpers.configureLogging();
		LocalDumpFileExample.printDocumentation();

		DumpProcessingController dumpProcessingController = new DumpProcessingController(
				"wikidatawiki");
		// Note that the project name "wikidatawiki" is only for online access;
		// not relevant here.

		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(
				entityTimerProcessor, null, true);

		// Select local file (meta-data will be guessed):
		System.out.println();
		System.out
				.println("Processing a local dump file giving only its location");
		System.out
				.println("(meta-data like the date is guessed from the file name):");
		MwLocalDumpFile mwDumpFile = new MwLocalDumpFile(DUMP_FILE);
		dumpProcessingController.processDump(mwDumpFile);

		// Select local file and set meta-data:
		System.out.println();
		System.out
				.println("Processing a local dump file with all meta-data set:");
		mwDumpFile = new MwLocalDumpFile(DUMP_FILE, DumpContentType.JSON,
				"20150815", "wikidatawiki");
		dumpProcessingController.processDump(mwDumpFile);

		entityTimerProcessor.close();
	}
}
