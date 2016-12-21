package de.sampri.wd2xlisa;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

import de.sampri.wd2xlisa.model.Index;
import de.sampri.wd2xlisa.model.SenseBlock;

public class IndexGeneratorBySense implements EntityDocumentProcessor {

	Logger logger;

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

	Index<SenseBlock> index = new Index<SenseBlock>();

	Statistics stat = new Statistics();

	public IndexGeneratorBySense(Logger logger) {
		this.logger = logger;
	}

	public void processPropertyDocument(PropertyDocument propertyDocument) {
		stat.countEntities++;
		stat.countProperties++;
	}

	public void processItemDocument(ItemDocument itemDocument) {
		stat.countEntities++;
		stat.countItems++;

		// Get for all available languages
		Set<String> languages = itemDocument.getLabels().keySet();
		for (String language : languages) {

			// Label
			String label = itemDocument.findLabel(language);
			if (label != null) {
				SenseBlock block = new SenseBlock(itemDocument.getItemId().getId(), label, language, 0);
				index.add(block);
				stat.countSurfaceForms++;
				stat.countLabels++;
			}

			// Aliases
			List<MonolingualTextValue> aliases = itemDocument.getAliases().get(language);
			if (aliases != null) {
				for (MonolingualTextValue alias : aliases) {
					SenseBlock block = new SenseBlock(itemDocument.getItemId().getId(), alias.getText(), language, 0);
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
