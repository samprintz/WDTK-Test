package de.sampri.wd2xlisa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.EntityTimerProcessor;
import org.wikidata.wdtk.dumpfiles.MwLocalDumpFile;
import org.wikidata.wdtk.examples.ExampleHelpers;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IndexGeneratorAlt implements IndexGenerator {

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
	private static final String OUTPUT_PATH = "results/";
	private static final String OUTPUT_FILE = "-index.json";

	/**
	 * Incremented with each processed entity. Contains the number of processed
	 * entities after the dump was processed.
	 */
	int itemCount = 0;

	/**
	 * Filled by the {@link SitelinksCounter} with Number of distinct site links
	 * in the dump.
	 */
	private static int distinctSitelinks;

	private static JsonGenerator jsonGen;

	public static void main(String[] args) throws IOException {
		ExampleHelpers.configureLogging();

		// Prepare JSON output
		JsonFactory jsonFactory = new JsonFactory();
		String filepath = OUTPUT_PATH + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + OUTPUT_FILE;
		FileOutputStream file = new FileOutputStream(new File(filepath));
		jsonGen = jsonFactory.createGenerator(file, JsonEncoding.UTF8);
		jsonGen.setCodec(new ObjectMapper());
		jsonGen.setPrettyPrinter(new MinimalPrettyPrinter(""));

		// TODO Ist noch kein JSON array, schließende Klammer wird irgendwie
		// nicht geschrieben (s.u.)
		// jsonGen.writeRaw("[");

		// Select dump file
		MwLocalDumpFile mwDumpFile = new MwLocalDumpFile(DUMP_FILE, DumpContentType.JSON, "20161031", "wikidatawiki");

		// Instantiate Dump Processor Controller for Sitelinks Counter
		DumpProcessingController dumpProcessingControllerCountSL = new DumpProcessingController("wikidatawiki");
		dumpProcessingControllerCountSL.setOfflineMode(true);
		// Instantiate Sitelinks Counter
		SitelinksCounter sitelinksCounter = new SitelinksCounter();
		dumpProcessingControllerCountSL.registerEntityDocumentProcessor(sitelinksCounter, null, true);
		dumpProcessingControllerCountSL.processDump(mwDumpFile);
		distinctSitelinks = sitelinksCounter.getResult();
		// sitelinksCounter.printList();

		// Instantiate Dump Processor Controller for SurfaceForms Counter
		DumpProcessingController dumpProcessingControllerCountSF = new DumpProcessingController("wikidatawiki");
		dumpProcessingControllerCountSF.setOfflineMode(true);
		// Instantiate SurfaceForms Counter
		SurfaceFormsCounter surfaceFormsCounter = new SurfaceFormsCounter(null);
		dumpProcessingControllerCountSF.registerEntityDocumentProcessor(surfaceFormsCounter, null, true);
		dumpProcessingControllerCountSF.processDump(mwDumpFile);
		surfaceFormsCounter.printStatus();

		// Instantiate Dump Processor Controller for Index Generator
		DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
		dumpProcessingController.setOfflineMode(true);

		// Instantiale Index Generator and Timer Processor
		IndexGeneratorAlt indexGenerator = new IndexGeneratorAlt();
		dumpProcessingController.registerEntityDocumentProcessor(indexGenerator, null, true);
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(entityTimerProcessor, null, true);
		dumpProcessingController.processDump(mwDumpFile);

		// TODO
		// jsonGen.writeRaw("]");

		indexGenerator.processItemDocumentById("Q1726");

		entityTimerProcessor.close();

	}

	public IndexGeneratorAlt() {
	}

	public void processItemDocument(ItemDocument itemDocument) {
		IndexEntity indexEntity = retrieveResult(itemDocument);
		writeToIndex(indexEntity);

		// TODO
		// System.out.println(indexEntity.toString());

		// Update and print progress
		this.itemCount++;
		if (this.itemCount % 1000 == 0) {
			printStatus();
		}
	}

	public void processPropertyDocument(PropertyDocument propertyDocument) {
	}

	/**
	 * Writes data to JSON Index file.
	 * 
	 * @param indexEntity
	 */
	private void writeToIndex(IndexEntity indexEntity) {
		try {
			jsonGen.writeObject(indexEntity);
			jsonGen.writeRaw(",\n");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @param itemDocument
	 * @return
	 */
	public IndexEntity retrieveResult(ItemDocument itemDocument) {
		IndexEntity indexEntity = new IndexEntity();

		// ID
		indexEntity.id = itemDocument.getItemId().getId();

		// Surface forms
		indexEntity.surfaceForms = retrieveSurfaceForms(itemDocument);

		// Statistics
		indexEntity.statistics = retrieveStatistics(itemDocument);

		return indexEntity;
	}

	/**
	 * Given an item document it calculates the statistics of the item.
	 * 
	 * @param itemDocument
	 *            The item which statistics should be calculated.
	 * @return The statistics for the item.
	 */
	private HashMap<String, Double> retrieveStatistics(ItemDocument itemDocument) {
		HashMap<String, Double> statistics = new HashMap<String, Double>();

		double sitelinksAbs = itemDocument.getSiteLinks().size();
		double sitelinksRel = sitelinksAbs / distinctSitelinks;
		statistics.put("sitelinksAbs", sitelinksAbs);
		statistics.put("sitelinksRel", sitelinksRel);

		return statistics;
	}

	/**
	 * Given an item document it retrieves all surface forms of the item for all
	 * available languages grouped by language.
	 * 
	 * @param itemDocument
	 *            The item which surface forms should be retrieved.
	 * @return All surface forms of the item grouped by language.
	 */
	private HashMap<String, List<String>> retrieveSurfaceForms(ItemDocument itemDocument) {
		HashMap<String, List<String>> surfaceForms = new HashMap<String, List<String>>();
		// TODO Als Statisik abspeichern
		// int surfaceFormsCount = 0;

		// Get for all available languages
		Set<String> languages = itemDocument.getLabels().keySet();
		for (String language : languages) {
			List<String> surfaceFormsForLanguage = new ArrayList<String>();

			// Label
			String label = itemDocument.findLabel(language);
			if (label != null) {
				surfaceFormsForLanguage.add(label);
				// surfaceFormsCount++;
			}

			// Aliases
			List<MonolingualTextValue> aliases = itemDocument.getAliases().get(language);
			if (aliases != null) {
				for (MonolingualTextValue alias : aliases) {
					surfaceFormsForLanguage.add(alias.getText());
					// surfaceFormsCount++;
				}
			}

			surfaceForms.put(language, surfaceFormsForLanguage);
		}

		// TODO
		// System.out.println(surfaceFormsCount);

		return surfaceForms;
	}

	public void processItemDocumentById(String itemId) {
		WikibaseDataFetcher wbdf = WikibaseDataFetcher.getWikidataDataFetcher();
		try {
			EntityDocument entityDocument = wbdf.getEntityDocument(itemId);
			if (entityDocument instanceof ItemDocument) {
				IndexEntity indexEntity = retrieveResult((ItemDocument) entityDocument);
				writeToIndex(indexEntity);
				// TODO
				// System.out.println(indexEntity.toString());
			}
		} catch (MediaWikiApiErrorException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints the current status, time and entity count.
	 */
	public void printStatus() {
		System.out.println("Processed " + this.itemCount + " items.");
	}

	public void writeToIndex(IndexElement indexElement) {
		// TODO Auto-generated method stub

	}

	public void open() {
		// TODO Auto-generated method stub
		
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}
}
