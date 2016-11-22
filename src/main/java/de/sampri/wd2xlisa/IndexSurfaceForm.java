package de.sampri.wd2xlisa;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IndexSurfaceForm extends IndexElement {

	String surfaceForm;

	public HashMap<String, List<String>> entities;

	// TODO Stastics?

	public IndexSurfaceForm() {
		this.entities = new HashMap<String, List<String>>();
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
