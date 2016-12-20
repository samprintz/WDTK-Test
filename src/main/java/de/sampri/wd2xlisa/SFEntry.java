package de.sampri.wd2xlisa;

public class SFEntry {
	private String text;
	private String language;

	public SFEntry(String text, String language) {
		this.text = text;
		this.language = language;
	}

	public String getText() {
		return text;
	}

	public String getLanguage() {
		return language;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setLanguag(String language) {
		this.language = language;
	}
}
