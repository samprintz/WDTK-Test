package de.sampri.wd2xlisa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Helper {

	/**
	 * While processing items, this is the number of items after each the status
	 * is logged.
	 */
	public static final int LOGGING_DEPTH = 1000000;

	/**
	 * Returns current time stamp. Please use as prefix for log and result
	 * output.
	 * 
	 * @return current time stamp.
	 */
	static String getTimeStamp() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}

	/**
	 * Types of indizes, that can be created.
	 */
	public enum Mode {
		ENTITY_INDEX, SFFORM_INDEX, SENSE_INDEX,
	}

	/**
	 * Returns JSON generator for writing index results to file.
	 * 
	 * @param filepath
	 *            the results will be written here.
	 * @param logger
	 * @return JSON generator for writing index results to file.
	 */
	public static JsonGenerator getJsonGenerator(String filepath, Logger logger) {
		JsonGenerator jsonGenerator = null;
		try {
			FileOutputStream file = new FileOutputStream(new File(filepath));
			try {
				JsonFactory jsonFactory = new JsonFactory();
				jsonGenerator = jsonFactory.createGenerator(file, JsonEncoding.UTF8);
				jsonGenerator.setCodec(new ObjectMapper());
				jsonGenerator.setPrettyPrinter(new MinimalPrettyPrinter(""));
			} catch (IOException e) {
				logger.error("Result could not be written into JSON file.");
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			logger.error("JSON file for result was not found.");
			e.printStackTrace();
		}
		return jsonGenerator;
	}

}
