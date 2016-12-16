package de.sampri.wd2xlisa;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import de.sampri.wd2xlisa.model.Index;
import de.sampri.wd2xlisa.model.SurfaceFormBlock;

public class IndexGeneratorBySurfaceForm {

	private Index<SurfaceFormBlock> index = new Index<SurfaceFormBlock>();

	public void generateIndex(ConcurrentMap<String, Integer> distinctSurfaceForms) {
		for (Map.Entry<String, Integer> sf : distinctSurfaceForms.entrySet()) {
//			SurfaceFormBlock block = new SurfaceFormBlock(sf.getKey(), sf.getValue());
			SurfaceFormBlock block = new SurfaceFormBlock(sf.getKey(), 0);
			index.add(block);
		}
	}

	public Index<SurfaceFormBlock> getIndex() {
		return index;
	}

}
