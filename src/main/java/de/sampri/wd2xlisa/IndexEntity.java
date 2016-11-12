package de.sampri.wd2xlisa;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IndexEntity {

	// TODO properties private machen (war für json serialization) + getter &
	// setter

	public String id;

	public HashMap<String, List<String>> surfaceForms;

	public HashMap<String, Double> statistics;

	public IndexEntity() {
		this.surfaceForms = new HashMap<String, List<String>>();
		this.statistics = new HashMap<String, Double>();
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
