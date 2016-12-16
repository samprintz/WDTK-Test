package de.sampri.wd2xlisa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

import de.sampri.wd2xlisa.model.Index;
import de.sampri.wd2xlisa.model.SenseBlock;

public class IndexGeneratorBySense implements EntityDocumentProcessor {

	Logger logger;

	Index<SenseBlock> index = new Index<SenseBlock>();

	public IndexGeneratorBySense(Logger logger) {
		this.logger = logger;
	}

	public void processPropertyDocument(PropertyDocument propertyDocument) {
	}

	public void processItemDocument(ItemDocument itemDocument) {
		List<String> surfaceForms = getSurfaceForms(itemDocument);

		for (String surfaceForm : surfaceForms) {
			SenseBlock block = new SenseBlock();
			block.setId(itemDocument.getItemId().getId());
			block.setSurfaceForm(surfaceForm);
			block.setProbability(0);
			index.add(block);
		}
	}

	private List<String> getSurfaceForms(ItemDocument itemDocument) {
		List<String> surfaceForms = new ArrayList<String>();

		Map<String, MonolingualTextValue> labels = itemDocument.getLabels();
		Map<String, List<MonolingualTextValue>> aliases = itemDocument.getAliases();

		for (MonolingualTextValue Mtvlabel : labels.values()) {
			String label = Mtvlabel.getText();
			if (!surfaceForms.contains(label)) {
				surfaceForms.add(label);
			}
		}

		for (List<MonolingualTextValue> aliasesLangSpec : aliases.values()) {
			for (MonolingualTextValue MtvAlias : aliasesLangSpec) {
				String alias = MtvAlias.getText();
				if (surfaceForms.contains(alias)) {
					surfaceForms.add(alias);
				}
			}
		}

		return surfaceForms;
	}

	public void generateIndex() {
		// already done during processing the dump
	}

	public Index<SenseBlock> getIndex() {
		return index;
	}

}
