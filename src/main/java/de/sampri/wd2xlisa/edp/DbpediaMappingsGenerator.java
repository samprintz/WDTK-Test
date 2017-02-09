package de.sampri.wd2xlisa.edp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;

import de.sampri.wd2xlisa.Helper;
import de.sampri.wd2xlisa.model.EntityDbpediaMapping;
import de.sampri.wd2xlisa.model.Index;

/**
 * Class for creating an {@link Index} of {@link EntityDbpediaMapping}s,
 * containing mappings for all entities of a Wikidata dump.
 */
public class DbpediaMappingsGenerator implements EntityDocumentProcessor {

	Logger logger;

	/**
	 * Contains sites information for retrieving the site URLs
	 */
	// Sites sites;

	/**
	 * Base URL for DBpedia resources
	 */
	private final String DBPEDIA_URL = "http://dbpedia.org/resource/";

	/**
	 * All DBpedia mappings are stored in this index.
	 */
	Index<EntityDbpediaMapping> index = new Index<EntityDbpediaMapping>();

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
	 * Incremented with each processed entity, that was added to the index and
	 * has an DBpedia URL.
	 */
	int itemMappedCount = 0;

	public DbpediaMappingsGenerator(Logger logger) {
		this.logger = logger;
		// DumpProcessingController dumpProcessingController = new
		// DumpProcessingController("wikidatawiki");
		// dumpProcessingController.setOfflineMode(ExampleHelpers.OFFLINE_MODE);
		// try {
		// this.sites = dumpProcessingController.getSitesInformation();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	public void processPropertyDocument(PropertyDocument propertyDocument) {
	}

	public void processItemDocument(ItemDocument itemDocument) {
		String entityName = itemDocument.findLabel("en");
		if (entityName != null) {
			EntityDbpediaMapping entityDbpediaMapping = new EntityDbpediaMapping();
			String entityId = itemDocument.getEntityId().getId();
			entityDbpediaMapping.setEntity(entityId);
			SiteLink sitelink = itemDocument.getSiteLinks().get("enwiki");
			String pageUrl;
			if (sitelink != null) {
				// pageUrl = sites.getPageUrl(sitelink.getSiteKey(),
				// sitelink.getPageTitle());
				pageUrl = getDbpediaUrl(sitelink.getPageTitle());
				this.itemMappedCount++;
			} else {
				pageUrl = "NA";
			}
			entityDbpediaMapping.setDbpediaUrl(pageUrl);

			index.add(entityDbpediaMapping);
			this.itemIndexedCount++;
		}

		this.itemCount++;
		if (this.itemCount % Helper.LOGGING_DEPTH == 0) {
			logStatus();
		}
	}

	private void logStatus() {
		logger.info("Processed " + itemCount + " items, added " + itemIndexedCount + " to index ("
				+ (itemIndexedCount - itemMappedCount) + " of them had no DBpedia URL).");
	}

	public void printStatistics() {
		logStatus();
	}

	private String getDbpediaUrl(String pageTitle) {
		try {
			String encodedTitle;
			encodedTitle = URLEncoder.encode(pageTitle.replace(" ", "_"), "utf-8");
			// Keep special title symbols unescaped:
			encodedTitle = encodedTitle.replace("%3A", ":").replace("%2F", "/");
			return DBPEDIA_URL + encodedTitle;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Your JRE does not support UTF-8 encoding. Srsly?!", e);
		}
	}

	public Index<EntityDbpediaMapping> getIndex() {
		return index;
	}

}
