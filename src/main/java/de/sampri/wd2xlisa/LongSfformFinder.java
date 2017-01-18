package de.sampri.wd2xlisa;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

/**
 * Class for finding the surface form containing the most words in the Wikidata
 * dump and getting some statistics about the lengths of all surface forms.
 */
public class LongSfformFinder implements EntityDocumentProcessor {

	private static Logger logger = Logger.getLogger(Main.class);

	/**
	 * Only for the English surface forms
	 */
	private final String language = "en";

	/**
	 * The surface form containing the most words.
	 */
	private String sfform;
	/**
	 * The entity having the surface form as label or alias.
	 */
	private String entity;
	/**
	 * The number of words of the surface form.
	 */
	private int length;

	/**
	 * Stores statistics about how many surface forms of which length exist.
	 */
	private HashMap<Integer, Integer> stats = new HashMap<Integer, Integer>();

	public void processItemDocument(ItemDocument itemDocument) {
		String label = itemDocument.findLabel(language);
		if (label != null) {
			testIfLonger(label, itemDocument);
		}

		List<MonolingualTextValue> aliases = itemDocument.getAliases().get(language);
		if (aliases != null) {
			for (MonolingualTextValue mtvAlias : aliases) {
				String alias = mtvAlias.getText();
				testIfLonger(alias, itemDocument);
			}
		}
	}

	/**
	 * Tests, if the current surface form is the longest until now, and stores
	 * this information, if so. Increases the statistics.
	 * 
	 * @param sf
	 *            the current surface form
	 * @param itemDocument
	 *            the current itemDocument
	 */
	private void testIfLonger(String sf, ItemDocument itemDocument) {
		String[] splitted = sf.split(" ");
		int length = splitted.length;

		// longer?
		if (length > this.length) {
			this.length = length;
			this.sfform = sf;
			this.entity = itemDocument.getEntityId().getId();
			logger.info(
					"Found longer surface form at entity '" + entity + "' with length " + length + ": " + sfform + "");
		}

		// statistics
		if (stats.containsKey(length)) {
			stats.put(length, stats.get(length) + 1);
		} else {
			stats.put(length, 1);
		}
	}

	public void processPropertyDocument(PropertyDocument propertyDocument) {
	}

	public String getResult() {
		return "Longest surface form is '" + sfform + "' with " + length + " words at entity " + entity + ".";
	}

	public void printStatistics() {
		for (Entry<Integer, Integer> entry : stats.entrySet()) {
			logger.info("Length " + entry.getKey() + ": " + entry.getValue() + " items");
		}
	}

}
