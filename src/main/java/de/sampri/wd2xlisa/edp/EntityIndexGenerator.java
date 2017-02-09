package de.sampri.wd2xlisa.edp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

import de.sampri.wd2xlisa.Helper;
import de.sampri.wd2xlisa.model.EntityBlock;
import de.sampri.wd2xlisa.model.Index;

/**
 * Class for creating an {@link Index} of {@link EntityBlock}s, containing
 * blocks for all entities of a Wikidata dump.
 */
public class EntityIndexGenerator
		implements EntityDocumentProcessor /* implements IndexGenerator */ {

	Logger logger;

	/**
	 * Incremented with each processed entity. Contains the number of processed
	 * entities after the dump was processed.
	 */
	int itemCount = 0;

	/**
	 * Incremented with each processed entity, that was added to the index.
	 */
	int itemIndexedCount = 0;

	/**
	 * Filled by the {@link SitelinksCounter} with Number of distinct site links
	 * in the dump.
	 */
	int distinctSitelinks;

	/**
	 * All entity blocks are stored in this index.
	 */
	Index<EntityBlock> index = new Index<EntityBlock>();

	public EntityIndexGenerator(Logger logger, int distinctSitelinks) {
		this.logger = logger;
		this.distinctSitelinks = distinctSitelinks;
	}

	public void processPropertyDocument(PropertyDocument propertyDocument) {
	}

	public void processItemDocument(ItemDocument itemDocument) {
		String label = itemDocument.findLabel("en");

		if (label != null) {
			EntityBlock entity = new EntityBlock();
			entity.setEntity(itemDocument.getItemId().getId());
			entity.setLabel(label);

			// Surface forms
			// TODO Werden hier erstmal nicht mehr gebraucht
			// indexEntity.surfaceForms = retrieveSurfaceForms(itemDocument);

			// Statistics
			int sitelinksCount = itemDocument.getSiteLinks().size();
			entity.setSitelinksCount(sitelinksCount);
			entity.setProbability((double) sitelinksCount / distinctSitelinks);

			index.add(entity);
			this.itemIndexedCount++;
		}

		this.itemCount++;
		if (this.itemCount % Helper.LOGGING_DEPTH == 0) {
			logStatus();
		}
	}

	public void logStatus() {
		logger.info("Processed " + itemCount + " entities, added " + itemIndexedCount + " to index.");
	}

	/**
	 * Given an item document it retrieves all surface forms of the item for all
	 * available languages grouped by language.
	 * 
	 * @param itemDocument
	 *            The item which surface forms should be retrieved.
	 * @return All surface forms of the item grouped by language.
	 */
	public HashMap<String, List<String>> retrieveSurfaceForms(ItemDocument itemDocument) {
		HashMap<String, List<String>> surfaceForms = new HashMap<String, List<String>>();
		// TODO Als Statisik abspeichern
		// int surfaceFormsCount = 0;

		// Get for all available languages
		Set<String> languages = itemDocument.getLabels().keySet();
		for (String language : languages) {
			List<String> surfaceFormsForLanguage = new ArrayList<String>();

			// Label
			String label = itemDocument.findLabel(language);
			if (label != null) {
				surfaceFormsForLanguage.add(label);
				// surfaceFormsCount++;
			}

			// Aliases
			List<MonolingualTextValue> aliases = itemDocument.getAliases().get(language);
			if (aliases != null) {
				for (MonolingualTextValue alias : aliases) {
					surfaceFormsForLanguage.add(alias.getText());
					// surfaceFormsCount++;
				}
			}

			surfaceForms.put(language, surfaceFormsForLanguage);
		}

		return surfaceForms;
	}

	public Index<EntityBlock> getIndex() {
		return index;
	}

}
