package de.sampri.wd2xlisa.edp;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;

import de.sampri.wd2xlisa.model.Block;
import de.sampri.wd2xlisa.model.Index;

/**
 * Currently not used. Might be introduced again.
 */
public interface IndexGenerator extends EntityDocumentProcessor {

	Index<Block> getIndex(ItemDocument itemDocument);

}
