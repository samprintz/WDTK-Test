package de.sampri.wd2xlisa.model;

/**
 * Represents an index block for a sense (i.e. a the meaning of a surface form =
 * an entity meant by the surface form).
 */
public class SenseBlock implements Block {

	private String entity;

	private String surfaceForm;

	private String language;

	private double probability;

	public SenseBlock() {
	}

	public SenseBlock(String entity, String surfaceForm, String language, double probability) {
		this.entity = entity;
		this.language = language;
		this.surfaceForm = surfaceForm;
		this.probability = probability;
	}

	/**
	 * Returns the Wikidata ID of the entity (Q-Code, e.g. Q1040).
	 * 
	 * @return the Wikidata ID of the entity (Q-Code, e.g. Q1040).
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * Sets the Wikidata ID of the entity (Q-Code, e.g. Q1040).
	 * 
	 * @param entity
	 *            the Wikidata ID of the entity (Q-Code, e.g. Q1040)
	 */
	public void setId(String entity) {
		this.entity = entity;
	}

	/**
	 * Returns the text of the surface form.
	 * 
	 * @return the text of the surface form.
	 */
	public String getSurfaceForm() {
		return surfaceForm;
	}

	/**
	 * Sets the text of the surface form.
	 * 
	 * @param surfaceForm
	 *            the text of the surface form.
	 */
	public void setSurfaceForm(String surfaceForm) {
		this.surfaceForm = surfaceForm;
	}

	/**
	 * Returns the cohesion of the surface form to the entity. Cf.
	 * {@code res#priorProbability} in paper xLiD-Lexica.
	 * 
	 * @return the cohesion of the surface form to the entity. Cf.
	 *         {@code res#priorProbability} in paper xLiD-Lexica.
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * Sets the cohesion of the surface form to the entity. Cf.
	 * {@code res#priorProbability} in paper xLiD-Lexica.
	 * 
	 * @param probability
	 *            the cohesion of the surface form to the entity. Cf.
	 *            {@code res#priorProbability} in paper xLiD-Lexica.
	 */
	public void setProbability(double probability) {
		this.probability = probability;
	}

	/**
	 * Returns the language of the surface form.
	 * 
	 * @return the language of the surface form.
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the language of the surface form.
	 * 
	 * @param language
	 *            the language of the surface form.
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	public String toString() {
		return "{" + this.entity + ", " + this.surfaceForm + "(" + this.language + "), " + this.probability + "}";
	}

}
