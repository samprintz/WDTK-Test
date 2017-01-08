package de.sampri.wd2xlisa.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//import java.util.HashMap;
//import java.util.List;

/**
 * Represents an index block for an entity of the Wikidata knowledge base.
 */
public class EntityBlock implements Block {

	private String entity;

	private long sitelinksCount;

	// private long surfaceFormsCount;

	private double probability;

	// /**
	// * Contains one list of surface forms for each language, the entity own
	// surface forms in.
	// */
	// HashMap<String, List<String>> surfaceForms;

	public EntityBlock() {
	}

	public EntityBlock(String entity, double probability) {
		this.entity = entity;
		this.probability = probability;
	}

	/**
	 * Returns the Wikidata ID of the entity (Q-Code, e.g. Q1040).
	 * 
	 * @return the Wikidata ID of the entity (Q-Code, e.g. Q1040).
	 */
	public String getEntity() {
		return this.entity;
	}

	/**
	 * Sets the Wikidata ID of the entity (Q-Code, e.g. Q1040).
	 * 
	 * @param entity
	 *            the Wikidata ID of the entity (Q-Code, e.g. Q1040).
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * Returns the number of sitelinks of the entity.
	 * 
	 * @return the number of sitelinks of the entity.
	 */
	public long getSitelinkCount() {
		return this.sitelinksCount;
	}

	/**
	 * Sets the number of sitelinks of the entity.
	 * 
	 * @param sitelinksCount
	 *            the number of sitelinks of the entity.
	 */
	public void setSitelinksCount(int sitelinksCount) {
		this.sitelinksCount = sitelinksCount;
	}

	// /**
	// * Returns the sum of number of labels and number of aliases of the
	// entity.
	// * @return the sum of number of labels and number of aliases of the
	// entity.
	// */
	// public long getSurfaceFormsCount() {
	// return this.surfaceFormsCount;
	// }

	// /**
	// * Sets the sum of number of labels and number of aliases of the entity.
	// * @param the sum of number of labels and number of aliases of the entity.
	// */
	// public void setSurfaceFormsCount(int surfaceFormsCount) {
	// this.surfaceFormsCount = surfaceFormsCount;
	// }

	/**
	 * Returns the importance of an entity. Cf. {@code res#probability} in paper
	 * xLiD-Lexica.
	 * 
	 * @return the importance of an entity. Cf. {@code res#probability} in paper
	 *         xLiD-Lexica.
	 */
	public double getProbability() {
		return this.probability;
	}

	/**
	 * Sets the importance of an entity. Cf. {@code res#probability} in paper
	 * xLiD-Lexica.
	 * 
	 * @param probability
	 *            the importance of an entity. Cf. {@code res#probability} in
	 *            paper xLiD-Lexica.
	 */
	public void setProbability(double probability) {
		this.probability = probability;
	}

	public String toString() {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "Konnte nicht serialisiert werden.";
		}
	}

}
