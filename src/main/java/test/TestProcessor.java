package test;

import java.io.File;
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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestProcessor implements EntityDocumentProcessor {

	private final static String DUMP_FILE = "B:/20161107-wikidata_dump/dumpfiles/wikidatawiki/json-20161031/20161031-head-100.json.gz";
	// private final static String DUMP_FILE = "C:/Daten/Eclipse/wdtk-parent/wdtk-examples/dumpfiles/wikidatawiki/json-20161031/20161031.json.gz";
	// private final static String DUMP_FILE = "./src/main/resources/sample-dump-20150815.json.gz";

	private static final String JSON_OUTPUT_FILE = "results/index_data.json";
	
	private static File outputFile;

	int itemCount = 0;

	public static void main(String[] args) throws IOException {
		ExampleHelpers.configureLogging();
		
		outputFile = new File(JSON_OUTPUT_FILE);
		
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
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		

		try {
			objectMapper.writeValue(outputFile, indexEntity); // TODO am Ende steht nur das letzte Objekt in der Datei, scheint immer wieder überschrieben zu werden
		} catch (JsonGenerationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String jsonInString;
		try {
			jsonInString = objectMapper.writeValueAsString(indexEntity);
			System.out.println(jsonInString);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
