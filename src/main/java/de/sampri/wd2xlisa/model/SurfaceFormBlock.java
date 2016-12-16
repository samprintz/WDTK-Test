package de.sampri.wd2xlisa.model;

public class SurfaceFormBlock implements Block {

	private String surfaceForm;

	private double probability;

	public SurfaceFormBlock() {
	}

	public SurfaceFormBlock(String surfaceForm, Integer probability) {
		this.surfaceForm = surfaceForm;
		this.probability = probability;
	}

	public String getText() {
		return surfaceForm;
	}

	public void setText(String surfaceForm) {
		this.surfaceForm = surfaceForm;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

}
