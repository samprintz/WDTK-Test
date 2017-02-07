package de.sampri.wd2xlisa.model;

public class EntityDbpediaMapping implements Block {

	private String entity;

	private String dbpediaUrl;

	public EntityDbpediaMapping() {
	}

	public EntityDbpediaMapping(String entity, String dbpediaUrl) {
		this.entity = entity;
		this.dbpediaUrl = dbpediaUrl;
	}

	public String getEntity() {
		return entity;
	}

	public String getDbpediaUrl() {
		return dbpediaUrl;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public void setDbpediaUrl(String dbpediaUrl) {
		this.dbpediaUrl = dbpediaUrl;
	}

	public String toString() {
		return "{" + entity + ": " + dbpediaUrl + "}";
	}

}
