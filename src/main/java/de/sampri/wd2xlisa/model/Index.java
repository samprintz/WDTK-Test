package de.sampri.wd2xlisa.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerator;

import de.sampri.wd2xlisa.Helper;

/**
 * Provides a list for collecting the surface form, entity or sense blocks while
 * processing the dump, and a method to export this list as JSON.
 * 
 * @param <T>
 *            Class of the blocks that are collected, {@link SurfaceFormBlock},
 *            {@link EntityBlock} or , {@link SenseBlock}.
 */
public class Index<T> {

	/**
	 * List for collecting the surface form, entity or sense blocks while
	 * processing the dump.
	 */
	List<T> blocks = new ArrayList<T>();

	public void add(T block) {
		blocks.add(block);
	}

	/**
	 * Generates an JSON file from the index and writes it to {@code filepath}.
	 * 
	 * @param filepath
	 *            The results will be written here.
	 */
	public void writeToFile(String filepath, Logger logger) {
		logger.info("Write index to file (" + filepath + ")...");

		JsonGenerator jsonGenerator = Helper.getJsonGenerator(filepath, logger);
		// jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());

		int count = 0;

		try {
			for (T entity : blocks) {
				jsonGenerator.writeObject(entity);
				jsonGenerator.writeRaw("\n");

				count++;
				if (count % Helper.LOGGING_DEPTH == 0) {
					logger.info("Written " + count + " blocks to file.");
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		logger.info("All blocks written to index (" + filepath + ").");
	}

	public String toString() {
		return this.blocks.toString();
	}

}
