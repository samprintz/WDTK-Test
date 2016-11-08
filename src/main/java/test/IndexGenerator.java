package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
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

public class IndexGenerator implements EntityDocumentProcessor {

	private final static String DUMP_FILE = "B:/20161107-wikidata_dump/dumpfiles/wikidatawiki/json-20161031/20161031-head-1000.json.gz";
	// private final static String DUMP_FILE =
	// "C:/Daten/Eclipse/wdtk-parent/wdtk-examples/dumpfiles/wikidatawiki/json-20161031/20161031.json.gz";
	// private final static String DUMP_FILE =
	// "./src/main/resources/sample-dump-20150815.json.gz";

	private static final String JSON_OUTPUT_FILE = "results/index_data.json";

	int itemCount = 0;
	static int distinctSitelinks;

	private static JsonGenerator jsonGen;

	public static void main(String[] args) throws IOException {
		ExampleHelpers.configureLogging();

		// Prepare JSON output
		JsonFactory jsonFactory = new JsonFactory();
		FileOutputStream file = new FileOutputStream(new File(JSON_OUTPUT_FILE));
		jsonGen = jsonFactory.createGenerator(file, JsonEncoding.UTF8);
		jsonGen.setCodec(new ObjectMapper());
		jsonGen.setPrettyPrinter(new MinimalPrettyPrinter(""));

		// Select dump file
		MwLocalDumpFile mwDumpFile = new MwLocalDumpFile(DUMP_FILE, DumpContentType.JSON, "20161031", "wikidatawiki");

		// Instantiate Dump Processor Controller for Sitelinks Counter
		DumpProcessingController dumpProcessingControllerCount = new DumpProcessingController("wikidatawiki");
		dumpProcessingControllerCount.setOfflineMode(true);

		// Instantiate Sitelinks Counter
		SitelinksCounter sitelinksCounter = new SitelinksCounter();
		dumpProcessingControllerCount.registerEntityDocumentProcessor(sitelinksCounter, null, true);
		dumpProcessingControllerCount.processDump(mwDumpFile);
		distinctSitelinks = sitelinksCounter.getResult();
		// sitelinksCounter.printList();

		// Instantiate Dump Processor Controller for Index Generator
		DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
		dumpProcessingController.setOfflineMode(true);

		// Instantiale Index Generator and Timer Processor
		IndexGenerator indexGenerator = new IndexGenerator();
		dumpProcessingController.registerEntityDocumentProcessor(indexGenerator, null, true);
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(entityTimerProcessor, null, true);
		dumpProcessingController.processDump(mwDumpFile);
		entityTimerProcessor.close();

	}

	public IndexGenerator() {

	}

	public void processItemDocument(ItemDocument itemDocument) {
		// TODO für alle Sprachen!!
		String language = "de";
		IndexEntity indexEntity = new IndexEntity();

		// ID
		indexEntity.id = itemDocument.getItemId().getId();

		// Surface forms
		// Label
		String label = itemDocument.findLabel(language);
		if (label != null) {
			indexEntity.labels.put(language, label);
		}

		// Aliases
		List<MonolingualTextValue> aliasesDe = itemDocument.getAliases().get(language);
		if (aliasesDe != null) {
			List<String> aliases = new ArrayList<String>();
			for (MonolingualTextValue alias : aliasesDe) {
				aliases.add(alias.getText());
			}
			indexEntity.aliases.put(language, aliases);
		}

		// Statistics
		double sitelinksAbs = itemDocument.getSiteLinks().size();
		double sitelinksRel = sitelinksAbs/distinctSitelinks;
		indexEntity.statistics.put("sitelinksAbs", sitelinksAbs);
		indexEntity.statistics.put("sitelinksRel", sitelinksRel);

		// Write in JSON file
		try {
			jsonGen.writeObject(indexEntity);
			jsonGen.writeRaw('\n');
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Print object in JSON style
		System.out.println(indexEntity.toString());

		// Update and print progress
		this.itemCount++;
		if (this.itemCount % 1000 == 0) {
			printStatus();
		}
	}

	public void processPropertyDocument(PropertyDocument propertyDocument) {}

	/**
	 * Prints the current status, time and entity count.
	 */
	public void printStatus() {
		System.out.println("Processed " + this.itemCount + " items.");
	}
}
