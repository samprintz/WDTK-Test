package de.sampri.wd2xlisa;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;

/**
 * Currently not used. Might be introduced again.
 */
public interface IndexGenerator extends EntityDocumentProcessor {

	void processItemDocumentById(String itemId);

	IndexElement retrieveResult(ItemDocument itemDocument);

	void writeToIndex(IndexElement indexElement);

}
