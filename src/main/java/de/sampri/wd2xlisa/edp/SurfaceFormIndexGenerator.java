package de.sampri.wd2xlisa.edp;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import de.sampri.wd2xlisa.model.Index;
import de.sampri.wd2xlisa.model.SurfaceFormBlock;

/**
 * Class for creating an {@link Index} of {@link SurfaceFormBlock}s, containing
 * blocks for all surface forms appearing in a Wikidata dump.
 */
public class SurfaceFormIndexGenerator {

	/**
	 * All surface form blocks are stored in this index.
	 */
	private Index<SurfaceFormBlock> index = new Index<SurfaceFormBlock>();

	public void generateIndex(ConcurrentMap<String, Integer> distinctSurfaceForms) {
		for (Map.Entry<String, Integer> sf : distinctSurfaceForms.entrySet()) {
			// SurfaceFormBlock block = new SurfaceFormBlock(sf.getKey(),
			// sf.getValue());
			SurfaceFormBlock block = new SurfaceFormBlock(sf.getKey(), 1);
			index.add(block);
		}
	}

	public Index<SurfaceFormBlock> getIndex() {
		return index;
	}

}
