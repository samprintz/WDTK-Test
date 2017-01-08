package de.sampri.wd2xlisa.model;

/**
 * Represents an index block for a surface form of the Wikidata knowledge base.
 */
public class SurfaceFormBlock implements Block {

	private String surfaceForm;

	private double probability;

	public SurfaceFormBlock() {
	}

	public SurfaceFormBlock(String surfaceForm, Integer probability) {
		this.surfaceForm = surfaceForm;
		this.probability = probability;
	}

	/**
	 * Returns the text of the surface form.
	 * 
	 * @return the text of the surface form.
	 */
	public String getText() {
		return surfaceForm;
	}

	/**
	 * Sets the text of the surface form.
	 * 
	 * @param surfaceForm
	 *            the text of the surface form.
	 */
	public void setText(String surfaceForm) {
		this.surfaceForm = surfaceForm;
	}

	/**
	 * Returns the importance of a surface form. Cf. {@code sf#probability} in
	 * paper xLiD-Lexica.
	 * 
	 * @return the importance of a surface form. Cf. {@code sf#probability} in
	 *         paper xLiD-Lexica.
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * Sets the importance of a surface form. Cf. {@code sf#probability} in
	 * paper xLiD-Lexica.
	 * 
	 * @param probability
	 *            the importance of a surface form. Cf. {@code sf#probability}
	 *            in paper xLiD-Lexica.
	 */
	public void setProbability(double probability) {
		this.probability = probability;
	}

}
