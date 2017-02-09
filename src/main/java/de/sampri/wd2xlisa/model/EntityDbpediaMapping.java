package de.sampri.wd2xlisa.model;

/**
 * Represents an index block for an mapping between an entity ID and an DBpedia
 * URL.
 */
public class EntityDbpediaMapping implements Block {

	private String entity;

	private String dbpediaUrl;

	public EntityDbpediaMapping() {
	}

	public EntityDbpediaMapping(String entity, String dbpediaUrl) {
		this.entity = entity;
		this.dbpediaUrl = dbpediaUrl;
	}

	/**
	 * Returns the Wikidata ID of the entity (Q-Code, e.g. Q1040)
	 * 
	 * @return the Wikidata ID of the entity (Q-Code, e.g. Q1040)
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * Sets the Wikidata ID of the entity (Q-Code, e.g. Q1040)
	 * 
	 * @param entity
	 *            the Wikidata ID of the entity (Q-Code, e.g. Q1040)
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * Returns the DBpedia URL of the entity.
	 * 
	 * @return the DBpedia URL of the entity.
	 */
	public String getDbpediaUrl() {
		return dbpediaUrl;
	}

	/**
	 * Sets the DBpedia URL of the entity.
	 * 
	 * @param dbpediaUrl
	 *            the DBpedia URL of the entity.
	 */
	public void setDbpediaUrl(String dbpediaUrl) {
		this.dbpediaUrl = dbpediaUrl;
	}

	public String toString() {
		return "{" + entity + ": " + dbpediaUrl + "}";
	}

}
