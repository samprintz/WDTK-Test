package de.sampri.wd2xlisa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

public class SurfaceFormsCounter implements EntityDocumentProcessor {

	private static final String OUTPUT_PATH = "results/";
	private static final String OUTPUT_FILE = "-sfforms.txt";

	class SurfaceFormStatistics {
		long countEntities = 0;
		long countItems = 0;
		long countProperties = 0;
		long countLabels = 0;
		long countDistinctLabels = 0;
		long countAliases = 0;
		long countDistinctAliases = 0;
		long countSurfaceForms = 0;
		long countDistinctSurfaceForms = 0;
	}

	// HashMap<String, Integer> surfaceForms = new HashMap<String, Integer>();

	// DB db = DBMaker.memoryDB("results/file.db").make();
	DB db = DBMaker.memoryDB().make();
	ConcurrentMap<String, Integer> surfaceForms = db.hashMap("map", Serializer.STRING, Serializer.INTEGER)
			.counterEnable().create();

	SurfaceFormStatistics stat = new SurfaceFormStatistics();

	public void processItemDocument(ItemDocument itemDocument) {
		stat.countEntities++;
		stat.countItems++;

		Map<String, MonolingualTextValue> labels = itemDocument.getLabels();
		Map<String, List<MonolingualTextValue>> aliases = itemDocument.getAliases();

		for (MonolingualTextValue Mtvlabel : labels.values()) {
			String label = Mtvlabel.getText();
			stat.countLabels++;
			stat.countSurfaceForms++;
			if (surfaceForms.containsKey(label)) {
				surfaceForms.put(label, surfaceForms.get(label) + 1);
			} else {
				surfaceForms.put(label, 1);
				stat.countDistinctLabels++;
				stat.countDistinctSurfaceForms++;
			}
		}

		for (List<MonolingualTextValue> aliasesLangSpec : aliases.values()) {
			for (MonolingualTextValue MtvAlias : aliasesLangSpec) {
				String alias = MtvAlias.getText();
				stat.countSurfaceForms++;
				stat.countAliases++;
				if (surfaceForms.containsKey(alias)) {
					surfaceForms.put(alias, surfaceForms.get(alias) + 1);
				} else {
					surfaceForms.put(alias, 1);
					stat.countDistinctAliases++;
					stat.countDistinctSurfaceForms++;
				}
			}
		}

		if (stat.countEntities % 100000 == 0) {
			printStatus();
		}

		if (stat.countEntities % 10000000 == 0) {
			printStatus();
		}
	}

	public void processPropertyDocument(PropertyDocument propertyDocument) {
		stat.countEntities++;
		stat.countProperties++;
	}

	public void printStatus() {
		System.out.println("---");
		System.out.println("Entities: " + stat.countEntities);
		System.out.println(" * Items: " + stat.countItems);
		System.out.println(" * Properties: " + stat.countProperties);
		System.out.println("Surface Forms: " + stat.countSurfaceForms);
		System.out
				.println(" * Distinct (Count/Hashmap): " + stat.countDistinctSurfaceForms + "/" + surfaceForms.size());
		System.out.println("Labels: " + stat.countLabels);
		System.out.println(" * Distinct: " + stat.countDistinctLabels);
		System.out.println("Aliases: " + stat.countAliases);
		System.out.println(" * Distinct: " + stat.countDistinctAliases);
		System.out.println("HashMap Size: " + surfaceForms.size());
	}

	public void printList() {
		System.out.println(surfaceForms.toString().replaceAll(",", ",\n"));
	}

	public void printSortedList() {
		Collection<String> unsorted = surfaceForms.keySet();
		List<String> sorted = SitelinksCounter.asSortedList(unsorted);
		System.out.println(sorted.toString().replaceAll(",", ",\n"));
		// System.out.println(sorted.toString());
	}

	public void writeToFile() {
		File dir = new File(OUTPUT_PATH);
		dir.mkdirs();
		String filepath = OUTPUT_PATH + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + OUTPUT_FILE;
		Properties properties = new Properties();
		for (Map.Entry<String, Integer> entry : surfaceForms.entrySet()) {
			properties.put(entry.getKey(), entry.getValue().toString());
		}
		try {
			properties.store(new FileOutputStream(filepath), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		db.close();
	}

	// public void writeToFile() {
	// String filepath = OUTPUT_PATH + new
	// SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + OUTPUT_FILE;
	// try {
	// FileOutputStream fileOut = new FileOutputStream(filepath);
	// ObjectOutputStream out = new ObjectOutputStream(fileOut);
	// out.writeObject(surfaceForms);
	// out.close();
	// fileOut.close();
	// System.out.println("Serialized data is saved in " + filepath + ".");
	// } catch (IOException i) {
	// i.printStackTrace();
	// }
	// }

}
