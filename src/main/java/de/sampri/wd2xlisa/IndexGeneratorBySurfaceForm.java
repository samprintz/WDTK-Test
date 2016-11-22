package de.sampri.wd2xlisa;

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

public class IndexGeneratorBySurfaceForm implements IndexGenerator {

	public void processItemDocument(ItemDocument itemDocument) {
		IndexSurfaceForm indexSurfaceForm = retrieveResult(itemDocument);
		writeToIndex(indexSurfaceForm);
	}

	public void processPropertyDocument(PropertyDocument propertyDocument) {
	}

	public void processItemDocumentById(String itemId) {
	}

	public IndexSurfaceForm retrieveResult(ItemDocument itemDocument) {
		IndexSurfaceForm indexSurfaceForm = new IndexSurfaceForm();

		// TODO Zuerst brauche ich eine Liste aller SF, dann kann ich über die
		// Entities iterieren und für jede dabei vorkommende SF die Entität zum
		// Index hinzufügen.

		return indexSurfaceForm;
	}

	public void writeToIndex(IndexElement indexElement) {
		// TODO Auto-generated method stub

	}

}
