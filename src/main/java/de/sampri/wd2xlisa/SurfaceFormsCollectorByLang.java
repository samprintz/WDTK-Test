package de.sampri.wd2xlisa;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

/**
 * Class for retrieving all distinct surface forms in a Wikidata dump, grouped
 * in Maps by language.
 */
public class SurfaceFormsCollectorByLang implements EntityDocumentProcessor {

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
		long countLanguages = 0;
	}

	/**
	 * Map of all distinct surface forms with the frequency they appear, grouped
	 * in Maps by language.
	 */
	HashMap<String, ConcurrentMap<String, Integer>> allSurfaceForms = new HashMap<String, ConcurrentMap<String, Integer>>();

	SurfaceFormStatistics stat = new SurfaceFormStatistics();

	public SurfaceFormsCollectorByLang(Logger logger) {
		this.logger = logger;
	}

	public void processItemDocument(ItemDocument itemDocument) {
		stat.countEntities++;
		stat.countItems++;

		// Labels
		Set<String> labelLanguages = itemDocument.getLabels().keySet();
		for (String language : labelLanguages) {
			String label = itemDocument.findLabel(language);
			if (label != null) {
				stat.countLabels++;
				stat.countSurfaceForms++;
				ConcurrentMap<String, Integer> langSurfaceForms = allSurfaceForms.get(language);
				if (langSurfaceForms != null) {
					Integer langSurfaceFormsCount = langSurfaceForms.get(label);
					if (langSurfaceFormsCount == null) {
						langSurfaceForms.put(label, 1);
						stat.countDistinctLabels++;
						stat.countDistinctSurfaceForms++;
					} else {
						langSurfaceForms.put(label, langSurfaceFormsCount + 1);
					}
				} else {
					DB db = DBMaker.memoryDB().make();
					langSurfaceForms = db.hashMap("map", Serializer.STRING, Serializer.INTEGER).create();
					allSurfaceForms.put(language, langSurfaceForms);
					stat.countLanguages++;
					stat.countDistinctLabels++;
					stat.countDistinctSurfaceForms++;
				}
			}
		}

		// Aliases
		Set<String> aliasesLanguages = itemDocument.getAliases().keySet();
		for (String language : aliasesLanguages) {
			List<MonolingualTextValue> aliases = itemDocument.getAliases().get(language);
			for (MonolingualTextValue mtv : aliases) {
				if (mtv != null) {
					stat.countAliases++;
					stat.countSurfaceForms++;
					String alias = mtv.getText();
					ConcurrentMap<String, Integer> langSurfaceForms = allSurfaceForms.get(language);
					if (langSurfaceForms != null) {
						Integer langSurfaceFormsCount = langSurfaceForms.get(alias);
						if (langSurfaceFormsCount == null) {
							langSurfaceForms.put(alias, 1);
							stat.countDistinctAliases++;
							stat.countDistinctSurfaceForms++;
						} else {
							langSurfaceForms.put(alias, langSurfaceFormsCount + 1);
						}
					} else {
						DB db = DBMaker.memoryDB().make();
						langSurfaceForms = db.hashMap("map", Serializer.STRING, Serializer.INTEGER).create();
						allSurfaceForms.put(language, langSurfaceForms);
						stat.countLanguages++;
						stat.countDistinctAliases++;
						stat.countDistinctSurfaceForms++;
					}
				}
			}
		}

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
				+ " P) and counted " + stat.countDistinctSurfaceForms + " distinct surface forms for "
				+ stat.countLanguages + " languages.");

		logger.debug("Of the " + stat.countSurfaceForms + " surface forms (" + stat.countDistinctSurfaceForms
				+ " distinct), " + stat.countLabels + " were labels (" + stat.countDistinctLabels + " distinct) and "
				+ stat.countAliases + " aliases (" + stat.countDistinctAliases + " distinct).");
	}

	public void printList() {
		System.out.println(allSurfaceForms.toString().replaceAll(",", ",\n"));
	}

	public void printSortedList() {
		Collection<String> unsorted = allSurfaceForms.keySet();
		List<String> sorted = SitelinksCounter.asSortedList(unsorted);
		System.out.println(sorted.toString().replaceAll(",", ",\n"));
		// System.out.println(sorted.toString());
	}

	/**
	 * Returns the the map with all distinct surface forms with the frequency
	 * they appear, grouped by language.
	 * 
	 * @return the the map with all distinct surface forms with the frequency
	 *         they appear, grouped by language.
	 */
	public HashMap<String, ConcurrentMap<String, Integer>> getResult() {
		return this.allSurfaceForms;
	}

}
