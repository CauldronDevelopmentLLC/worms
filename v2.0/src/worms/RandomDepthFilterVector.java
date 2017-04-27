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
import java.util.Enumeration;

public class RandomDepthFilterVector extends FilterVector {
	int maxDepth;

	public RandomDepthFilterVector() {
	}

	public int filter(Point2D pt, int color) {
		Enumeration enumeration = filters.elements();
		int i = 0;
		int depth = (int)(Math.random() * filters.size());
		while (enumeration.hasMoreElements() && i++ < depth)
			color = ((Filter)enumeration.nextElement()).filter(pt, color);

		return color;
	}
}