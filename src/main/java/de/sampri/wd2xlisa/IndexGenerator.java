package de.sampri.wd2xlisa;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentDumpProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;

public interface IndexGenerator extends EntityDocumentDumpProcessor {

	void processItemDocumentById(String itemId);

	IndexElement retrieveResult(ItemDocument itemDocument);

	void writeToIndex(IndexElement indexElement);

}
