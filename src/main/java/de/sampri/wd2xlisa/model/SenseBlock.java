package de.sampri.wd2xlisa.model;

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

	public String getEntity() {
		return entity;
	}

	public void setId(String entity) {
		this.entity = entity;
	}

	public String getSurfaceForm() {
		return surfaceForm;
	}

	public void setSurfaceForm(String surfaceForm) {
		this.surfaceForm = surfaceForm;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String toString() {
		return "{" + this.entity + ", " + this.surfaceForm + "(" + this.language + "), " + this.probability + "}";
	}

}
