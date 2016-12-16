package de.sampri.wd2xlisa.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//import java.util.HashMap;
//import java.util.List;

public class EntityBlock implements Block {

	private String entity;

	private long sitelinksCount;

	// private long surfaceFormsCount;

	private double probability;

	// HashMap<String, List<String>> surfaceForms;

	public EntityBlock() {
	}

	public EntityBlock(String entity, double probability) {
		this.entity = entity;
		this.probability = probability;
	}

	public String getEntity() {
		return this.entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public long getSitelinkCount() {
		return this.sitelinksCount;
	}

	public void setSitelinksCount(int sitelinksCount) {
		this.sitelinksCount = sitelinksCount;
	}

	// public long getSurfaceFormsCount() {
	// return this.surfaceFormsCount;
	// }
	//
	// public void setSurfaceFormsCount(int surfaceFormsCount) {
	// this.surfaceFormsCount = surfaceFormsCount;
	// }

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
