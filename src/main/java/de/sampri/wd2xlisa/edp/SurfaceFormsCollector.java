package de.sampri.wd2xlisa.edp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

import de.sampri.wd2xlisa.Helper;

/**
 * Class for retrieving all distinct surface forms in a Wikidata dump.
 */
public class SurfaceFormsCollector implements EntityDocumentProcessor {

	Logger logger;

	/**
	 * Statistics about the dump.
	 */
	public class SurfaceFormStatistics {
		long countEntities = 0;
		long countItems = 0;
		long countProperties = 0;
		long countLabels = 0;
		long countDistinctLabels = 0;
		long countAliases = 0;
		long countDistinctAliases = 0;
		long countSurfaceForms = 0;
		long countDistinctSurfaceForms = 0;
	}

	/**
	 * List of all distinct surface forms with the frequency they appear.
	 */
	// HashMap<String, Integer> surfaceForms = new HashMap<String, Integer>();
	// DB db = DBMaker.memoryDB("results/file.db").make();
	DB db = DBMaker.memoryDB().make();
	ConcurrentMap<String, Integer> surfaceForms = db.hashMap("map", Serializer.STRING, Serializer.INTEGER).create(); // .counterEnable()

	SurfaceFormStatistics stat = new SurfaceFormStatistics();

	public SurfaceFormsCollector(Logger logger) {
		this.logger = logger;
	}

	public void processItemDocument(ItemDocument itemDocument) {
		stat.countEntities++;
		stat.countItems++;

//		Map<String, MonolingualTextValue> labels = itemDocument.getLabels();
//		Map<String, List<MonolingualTextValue>> aliases = itemDocument.getAliases();
		String language = "en";
		String label = itemDocument.findLabel(language);
		List<MonolingualTextValue> aliasesLangSpec = itemDocument.getAliases().get(language);

		// Label
//		for (MonolingualTextValue Mtvlabel : labels.values()) {
//			String label = Mtvlabel.getText();
		if (label != null) {
			stat.countLabels++;
			stat.countSurfaceForms++;
			if (surfaceForms.containsKey(label)) {
				surfaceForms.put(label, surfaceForms.get(label) + 1);
			} else {
				surfaceForms.put(label, 1);
				stat.countDistinctLabels++;
				stat.countDistinctSurfaceForms++;
			}
		}
//		}

		// Aliases
//		for (List<MonolingualTextValue> aliasesLangSpec : aliases.values()) {
		if (aliasesLangSpec != null) {
			for (MonolingualTextValue MtvAlias : aliasesLangSpec) {
				String alias = MtvAlias.getText();
				stat.countSurfaceForms++;
				stat.countAliases++;
				if (surfaceForms.containsKey(alias)) {
					surfaceForms.put(alias, surfaceForms.get(alias) + 1);
				} else {
					surfaceForms.put(alias, 1);
					stat.countDistinctAliases++;
					stat.countDistinctSurfaceForms++;
				}
			}
		}
//		}

		if (stat.countEntities % Helper.LOGGING_DEPTH == 0) {
			logStatus();
			// printStatus();
		}
	}

	public void processPropertyDocument(PropertyDocument propertyDocument) {
		stat.countEntities++;
		stat.countProperties++;
	}

	public void logStatus() {
		logger.info("Processed " + stat.countEntities + " entities (" + stat.countItems + " Q, " + stat.countProperties
				+ " P) and counted " + stat.countDistinctSurfaceForms + " distinct surface forms.");

		logger.debug("Of the " + stat.countSurfaceForms + " surface forms (" + stat.countDistinctSurfaceForms
				+ " distinct), " + stat.countLabels + " were labels (" + stat.countDistinctLabels + " distinct) and "
				+ stat.countAliases + " aliases (" + stat.countDistinctAliases + " distinct).");
	}

	public void printStatus() {
		System.out.println("---");
		System.out.println("Entities: " + stat.countEntities);
		System.out.println(" * Items: " + stat.countItems);
		System.out.println(" * Properties: " + stat.countProperties);
		System.out.println("Surface Forms: " + stat.countSurfaceForms);
		System.out
				.println(" * Distinct (Count/Hashmap): " + stat.countDistinctSurfaceForms + "/" + surfaceForms.size());
		System.out.println("Labels: " + stat.countLabels);
		System.out.println(" * Distinct: " + stat.countDistinctLabels);
		System.out.println("Aliases: " + stat.countAliases);
		System.out.println(" * Distinct: " + stat.countDistinctAliases);
		System.out.println("HashMap Size: " + surfaceForms.size());
	}

	/**
	 * Print list of all distinct surface forms in the dump. Use with care (only
	 * for testing), the list might be huge.
	 */
	public void printList() {
		System.out.println(surfaceForms.toString().replaceAll(",", ",\n"));
	}

	/**
	 * Print a sorted list of all distinct surface forms in the dump. Use with
	 * care (only for testing), the list might be huge.
	 */
	public void printSortedList() {
		Collection<String> unsorted = surfaceForms.keySet();
		List<String> sorted = SitelinksCounter.asSortedList(unsorted);
		System.out.println(sorted.toString().replaceAll(",", ",\n"));
		// System.out.println(sorted.toString());
	}

	/**
	 * Generates text file from the surface form list and writes it to
	 * {@code filepath}.
	 * 
	 * @param filepath
	 *            The results will be written here.
	 */
	public void writeToFile(String filepath) {
		logger.info("Write surface forms to file (" + filepath + ")...");

		String eol = System.getProperty("line.separator");
		int count = 0;

		try {
			Writer writer = new FileWriter(filepath);

			for (Map.Entry<String, Integer> entry : surfaceForms.entrySet()) {
				writer.append(entry.getKey()).append(',').append(entry.getValue().toString()).append(eol);
				count++;
				if (count % Helper.LOGGING_DEPTH == 0) {
					logger.info("Written " + count + " surface forms to file.");
				}
			}

			writer.close();
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}

		// db.close();

		logger.info("All surface forms written to file (" + filepath + ").");
	}

	/**
	 * Returns the the list of all distinct surface forms with the frequency
	 * they appear.
	 * 
	 * @return the the list of all distinct surface forms with the frequency
	 *         they appear.
	 */
	public ConcurrentMap<String, Integer> getResult() {
		return this.surfaceForms;
	}

}
