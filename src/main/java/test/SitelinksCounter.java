package test;

import java.util.HashMap;
import java.util.Map;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;

public class SitelinksCounter implements EntityDocumentProcessor {
	
	public Map<String, SiteLink> siteLinks;
	
	public SitelinksCounter() {
		this.siteLinks = new HashMap<String, SiteLink>();
	}

	public void processItemDocument(ItemDocument itemDocument) {
		siteLinks.putAll(itemDocument.getSiteLinks());
	}

	public void processPropertyDocument(PropertyDocument propertyDocument) {
		// TODO Auto-generated method stub

	}
	
	public int getResult() {
		return this.siteLinks.size();
	}
	
	public void printList() {
		System.out.println(siteLinks.toString().replaceAll(",", "\n"));
	}

}
