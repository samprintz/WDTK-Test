package de.sampri.wd2xlisa.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import de.sampri.wd2xlisa.Helper;

public class Index<T> {

	// private final Class<T> type;

	List<T> blocks = new ArrayList<T>();

	// public Index(Class<T> type) {
	// this.type = type;
	// }

	// public Class<T> getType() {
	// return type;
	// }

	public void add(T block) {
		blocks.add(block);
	}

	public void writeToFile(String filepath, Logger logger) {
		logger.info("Write index to file (" + filepath + ")...");

		JsonGenerator jsonGenerator = Helper.getJsonGenerator(filepath, logger);
		jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());

		// TODO
//		int count = 0;

		try {
			jsonGenerator.writeObject(blocks);
			
//			jsonGenerator.writeStartArray();
//
//			for (T entity : blocks) {
//				jsonGenerator.writeObject(entity);
//
//				count++;
//				if (count % Helper.loggingDepth == 0) {
//					logger.info("Written " + count + " entities to file.");
//				}
//			}
//
//			jsonGenerator.writeEndArray();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		logger.info("All blocks written to index (" + filepath + ").");
	}

}
