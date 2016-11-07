package test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.EntityTimerProcessor;
import org.wikidata.wdtk.dumpfiles.MwLocalDumpFile;
import org.wikidata.wdtk.examples.ExampleHelpers;

public class TestProcessor implements EntityDocumentProcessor {

	private final static String DUMP_FILE = "B:/20161107-wikidata_dump/dumpfiles/wikidatawiki/json-20161031/20161031-head-100.json.gz";
	// private final static String DUMP_FILE = "C:/Daten/Eclipse/wdtk-parent/wdtk-examples/dumpfiles/wikidatawiki/json-20161031/20161031.json.gz";
	// private final static String DUMP_FILE = "./src/main/resources/sample-dump-20150815.json.gz";

	int itemCount = 0;

	public static void main(String[] args) throws IOException {
		ExampleHelpers.configureLogging();
		
		TestProcessor processor = new TestProcessor();
		
		DumpProcessingController dumpProcessingController = new DumpProcessingController(
				"wikidatawiki");
		dumpProcessingController.setOfflineMode(true);
		dumpProcessingController.registerEntityDocumentProcessor(processor, null, true);

		// Also add a timer that reports some basic progress information:
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(
				entityTimerProcessor, null, true);

		// Select local file and set meta-data:
		MwLocalDumpFile mwDumpFile = new MwLocalDumpFile(DUMP_FILE, DumpContentType.JSON,
				"20161031", "wikidatawiki");
		dumpProcessingController.processDump(mwDumpFile);

		entityTimerProcessor.close();
	}
	
	public TestProcessor() {
		
	}

	public void processItemDocument(ItemDocument itemDocument) {
		this.itemCount++;
		
		//TODO Nach GND filtern?
		
		String id = itemDocument.getItemId().getId();
		String labelDe = itemDocument.findLabel("de");
		
 
		List<MonolingualTextValue> aliasesDe = itemDocument.getAliases().get("de");
		
		System.out.println(id + " " + labelDe);
		
		if (aliasesDe != null) {
			for (MonolingualTextValue alias : aliasesDe) {
				System.out.println(alias.getText());
			}
		}
		
		
		
		
		// für alle Sprachen!!
		
		// Print progress every 100,000 items:
		if (this.itemCount % 1000 == 0) {
			printStatus();
		}
		
		
		//TODO Wie kann ich hier Text ausgeben? Dann implementieren, dass hier die Labels zu jeder entity geprintet werden
		
		
	}

	public void processPropertyDocument(PropertyDocument propertyDocument) {

	}

	/**
	 * Prints the current status, time and entity count.
	 */
	public void printStatus() {
		System.out.println("Processed " + this.itemCount
				+ " items.");
	}
}
