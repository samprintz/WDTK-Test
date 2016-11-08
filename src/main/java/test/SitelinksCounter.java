package test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;

public class SitelinksCounter implements EntityDocumentProcessor {

	private Map<String, SiteLink> sitelinks;

	public SitelinksCounter() {
		this.sitelinks = new HashMap<String, SiteLink>();
	}

	public void processItemDocument(ItemDocument itemDocument) {
		sitelinks.putAll(itemDocument.getSiteLinks());
	}

	public void processPropertyDocument(PropertyDocument propertyDocument) {}

	public int getResult() {
		return this.sitelinks.size();
	}

	public void printList() {
		Collection<String> unsorted = sitelinks.keySet();
		List<String> sorted = SitelinksCounter.asSortedList(unsorted);
		// System.out.println(sorted.toString().replaceAll(",", ",\n"));
		System.out.println(sorted.toString());
	}

	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		java.util.Collections.sort(list);
		return list;
	}

}
