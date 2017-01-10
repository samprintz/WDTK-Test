package de.sampri.wd2xlisa;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

import de.sampri.wd2xlisa.model.Index;
import de.sampri.wd2xlisa.model.SenseBlock;

/**
 * Class for creating an {@link Index} of {@link SenseBlock}s, containing blocks
 * for all senses of surface forms appearing in a Wikidata dump.
 */
public class IndexGeneratorBySense implements EntityDocumentProcessor {

	Logger logger;

	private HashMap<String, ConcurrentMap<String, Integer>> distinctSurfaceFormsByLang;

	/**
	 * Statistics about the dump.
	 */
	public class Statistics {
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
	 * All sense blocks are stored in this index.
	 */
	private Index<SenseBlock> index = new Index<SenseBlock>();

	Statistics stat = new Statistics();

	public IndexGeneratorBySense(Logger logger,
			HashMap<String, ConcurrentMap<String, Integer>> distinctSurfaceFormsByLang) {
		this.logger = logger;
		this.distinctSurfaceFormsByLang = distinctSurfaceFormsByLang;
	}

	public void processPropertyDocument(PropertyDocument propertyDocument) {
		stat.countEntities++;
		stat.countProperties++;
	}

	public void processItemDocument(ItemDocument itemDocument) {
		stat.countEntities++;
		stat.countItems++;

		// Label
		Set<String> labelLanguages = itemDocument.getLabels().keySet();
		for (String language : labelLanguages) {
			String label = itemDocument.findLabel(language);
			if (label != null) {
				int n = distinctSurfaceFormsByLang.get(language).get(label);
				SenseBlock block = new SenseBlock(itemDocument.getItemId().getId(), label, language, 1.0 / n);
				index.add(block);
				stat.countSurfaceForms++;
				stat.countLabels++;
			}
		}

		// Aliases
		Set<String> aliasesLanguages = itemDocument.getAliases().keySet();
		for (String language : aliasesLanguages) {
			List<MonolingualTextValue> aliases = itemDocument.getAliases().get(language);
			for (MonolingualTextValue mtv : aliases) {
				if (mtv != null) {
					String alias = mtv.getText();
					int n = distinctSurfaceFormsByLang.get(language).get(alias);
					SenseBlock block = new SenseBlock(itemDocument.getItemId().getId(), alias, language, 1.0 / n);
					index.add(block);
					stat.countSurfaceForms++;
					stat.countAliases++;
				}
			}
		}

		if (stat.countEntities % Helper.LOGGING_DEPTH == 0) {
			logStatus();
		}
	}

	public void logStatus() {
		logger.info("Processed " + stat.countEntities + " entities (" + stat.countItems + " Q, " + stat.countProperties
				+ " P) and counted " + stat.countDistinctSurfaceForms + " distinct surface forms.");

		logger.debug("Of the " + stat.countSurfaceForms + " surface forms (" + stat.countDistinctSurfaceForms
				+ " distinct), " + stat.countLabels + " were labels (" + stat.countDistinctLabels + " distinct) and "
				+ stat.countAliases + " aliases (" + stat.countDistinctAliases + " distinct).");
	}

	public void generateIndex() {
		// already done during processing the dump
	}

	public Index<SenseBlock> getIndex() {
		return index;
	}

}
