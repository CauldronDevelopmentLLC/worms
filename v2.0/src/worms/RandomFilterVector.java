package worms;

/**
 * Title:        PNEditor
 * Description:  An editor for XML Process Networks.
 * Copyright:    Copyleft 2001
 * Company:      UvA
 * @author Joe Coffland
 * @version 1.0
 */

import java.awt.geom.Point2D;
import java.util.*;

public class RandomFilterVector extends FilterVector {
	int maxFilters;
	Vector active = new Vector();

	public RandomFilterVector(int maxFilters) {
		this.maxFilters = maxFilters;
		randomize();
	}

	public int filter(Point2D pt, int color) {
		Enumeration enumeration = active.elements();
		while (enumeration.hasMoreElements())
		 color = ((Filter)enumeration.nextElement()).filter(pt, color);

		return color;
	}

	public void step() {
		Enumeration enumeration = active.elements();
		while (enumeration.hasMoreElements())
		 ((Filter)enumeration.nextElement()).step();
	}

	public void randomize() {
		int numFilters = (int)(maxFilters * Math.random()) + 1;
		if (numFilters > filters.size()) numFilters = filters.size();

		filters.addAll(active);
		active.clear();
		while (active.size() < numFilters) {
			int f = (int)(Math.random() * filters.size());
			Filter filter = (Filter)filters.get(f);
			active.add(filter);
			filters.remove(f);
			filter.randomize();
		}
	}
}