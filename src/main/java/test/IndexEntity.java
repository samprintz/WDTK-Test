package test;

import java.util.HashMap;
import java.util.List;

public class IndexEntity {

	// TODO properties private machen (war für json serialization) + getter & setter
	
	public String id;
	
	public HashMap<String, String> labels;
	
	public HashMap<String, List<String>> aliases;
	
	public IndexEntity() {
		this.labels = new HashMap<String, String>();
		this.aliases = new HashMap<String, List<String>>();
	}
	
	public String toString() {
		// TODO
		return id;
	}

}
