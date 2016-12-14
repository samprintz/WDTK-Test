package de.sampri.wd2xlisa.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//import java.util.HashMap;
//import java.util.List;

public class EntityBlock implements Block {

	String id;
	long sitelinksCount;
	long surfaceFormsCount;
	double probability;
	// HashMap<String, List<String>> surfaceForms;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getSitelinkCount() {
		return this.sitelinksCount;
	}

	public void setSitelinksCount(int sitelinksCount) {
		this.sitelinksCount = sitelinksCount;
	}

	public long getSurfaceFormsCount() {
		return this.surfaceFormsCount;
	}

	public void setSurfaceFormsCount(int surfaceFormsCount) {
		this.surfaceFormsCount = surfaceFormsCount;
	}

	public double getProbability() {
		return this.probability;
	}

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
