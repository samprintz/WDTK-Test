package de.sampri.wd2xlisa.model;

public class SenseBlock implements Block {

	private String entity;

	private String surfaceForm;

	private double probability;

	public SenseBlock() {
	}

	public SenseBlock(String entity, String surfaceForm, double probability) {
		this.entity = entity;
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

}
