package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

	/**
	 * The dump which entites should be processed..
	 */
	private final static String DUMP_FILE = "B:/20161107-wikidata_dump/dumpfiles/wikidatawiki/json-20161031/20161031-head-1000.json.gz";
	// private final static String DUMP_FILE =
	// "C:/Daten/Eclipse/wdtk-parent/wdtk-examples/dumpfiles/wikidatawiki/json-20161031/20161031.json.gz";
	// private final static String DUMP_FILE =
	// "./src/main/resources/sample-dump-20150815.json.gz";

	/**
	 * The result, the generated Index JSON file, will be saved here.
	 */
	private static final String JSON_OUTPUT_FILE = "results/index_data.json";

	private static final List<String> LANGUAGES = Arrays.asList("de", "en", "es", "zh");

	/**
	 * Incremented with each processed entity. Contains the number of processed
	 * entities after the dump was processed.
	 */
	int itemCount = 0;

	/**
	 * Filled by the {@link SitelinksCounter} with Number of distinct site links
	 * in the dump.
	 */
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
		List<String> surfaceForms = new ArrayList<String>();

		// Label
		String label = itemDocument.findLabel(language);
		if (label != null) {
			surfaceForms.add(label);
		}

		// Aliases
		List<MonolingualTextValue> aliases = itemDocument.getAliases().get(language);
		if (aliases != null) {
			for (MonolingualTextValue alias : aliases) {
				surfaceForms.add(alias.getText());
			}
		}

		indexEntity.surfaceForms.put(language, surfaceForms);

		// Statistics
		double sitelinksAbs = itemDocument.getSiteLinks().size();
		double sitelinksRel = sitelinksAbs / distinctSitelinks;
		indexEntity.statistics.put("sitelinksAbs", sitelinksAbs);
		indexEntity.statistics.put("sitelinksRel", sitelinksRel);

		// Write in JSON file
		try {
			jsonGen.writeObject(indexEntity);
			jsonGen.writeRaw('\n');
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// Print object in JSON style
		System.out.println(indexEntity.toString());

		// Update and print progress
		this.itemCount++;
		if (this.itemCount % 1000 == 0) {
			printStatus();
		}
	}

	public void processPropertyDocument(PropertyDocument propertyDocument) {
	}

	/**
	 * Prints the current status, time and entity count.
	 */
	public void printStatus() {
		System.out.println("Processed " + this.itemCount + " items.");
	}
}
