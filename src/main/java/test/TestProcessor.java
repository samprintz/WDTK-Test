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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestProcessor implements EntityDocumentProcessor {

	private final static String DUMP_FILE = "B:/20161107-wikidata_dump/dumpfiles/wikidatawiki/json-20161031/20161031-head-100.json.gz";
	// private final static String DUMP_FILE =
	// "C:/Daten/Eclipse/wdtk-parent/wdtk-examples/dumpfiles/wikidatawiki/json-20161031/20161031.json.gz";
	// private final static String DUMP_FILE =
	// "./src/main/resources/sample-dump-20150815.json.gz";

	private static final String JSON_OUTPUT_FILE = "results/index_data.json";

	int itemCount = 0;

	private static JsonGenerator jsonGen;

	public static void main(String[] args) throws IOException {
		ExampleHelpers.configureLogging();

		// Prepare JSON output
		JsonFactory jsonFactory = new JsonFactory();
		FileOutputStream file = new FileOutputStream(new File(JSON_OUTPUT_FILE));
		jsonGen = jsonFactory.createGenerator(file, JsonEncoding.UTF8);
		jsonGen.setCodec(new ObjectMapper());
		jsonGen.setPrettyPrinter(new MinimalPrettyPrinter(""));

		TestProcessor processor = new TestProcessor();

		DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
		dumpProcessingController.setOfflineMode(true);
		dumpProcessingController.registerEntityDocumentProcessor(processor, null, true);

		// Also add a timer that reports some basic progress information:
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(entityTimerProcessor, null, true);

		// Select local file and set meta-data:
		MwLocalDumpFile mwDumpFile = new MwLocalDumpFile(DUMP_FILE, DumpContentType.JSON, "20161031", "wikidatawiki");
		dumpProcessingController.processDump(mwDumpFile);

		entityTimerProcessor.close();
	}

	public TestProcessor() {

	}

	public void processItemDocument(ItemDocument itemDocument) {
		String language = "de";
		IndexEntity indexEntity = new IndexEntity();

		indexEntity.id = itemDocument.getItemId().getId();

		String label = itemDocument.findLabel(language);
		if (label != null) {
			indexEntity.labels.put(language, label);
		}

		List<MonolingualTextValue> aliasesDe = itemDocument.getAliases().get(language);

		if (aliasesDe != null) {
			List<String> aliases = new ArrayList<String>();
			for (MonolingualTextValue alias : aliasesDe) {
				aliases.add(alias.getText());
			}
			indexEntity.aliases.put(language, aliases);
		}

		try {
			jsonGen.writeObject(indexEntity);
			jsonGen.writeRaw('\n');
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println(indexEntity.toString());

		// TODO für alle Sprachen!!

		this.itemCount++;
		// Print progress every 1,000 items:
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
