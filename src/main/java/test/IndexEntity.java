package test;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IndexEntity {

	// TODO properties private machen (war für json serialization) + getter &
	// setter

	public String id;

	public HashMap<String, String> labels;

	public HashMap<String, List<String>> aliases;

	public HashMap<String, Integer> statistics;

	public IndexEntity() {
		this.labels = new HashMap<String, String>();
		this.aliases = new HashMap<String, List<String>>();
		this.statistics = new HashMap<String, Integer>();
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
