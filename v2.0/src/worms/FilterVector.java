package worms;

import java.awt.geom.Point2D;

/**
 * Title:        PNEditor
 * Description:  An editor for XML Process Networks.
 * Copyright:    Copyleft 2001
 * Company:      UvA
 * @author Joe Coffland
 * @version 1.0
 */

import java.util.*;

public class FilterVector implements Filter {
	Vector filters = new Vector();

	public FilterVector() {
	}

	public void add(Filter filter) {filters.add(filter);}
	public void clear(Filter filter) {filters.clear();}

	public int filter(Point2D pt, int color) {
		Enumeration enumeration = filters.elements();
		while (enumeration.hasMoreElements())
			color = ((Filter)enumeration.nextElement()).filter(pt, color);

		return color;
	}

	public void step() {
		Enumeration enumeration = filters.elements();
		while (enumeration.hasMoreElements())
			((Filter)enumeration.nextElement()).step();
	}

	public void randomize() {
		Enumeration enumeration = filters.elements();
		while (enumeration.hasMoreElements())
			((Filter)enumeration.nextElement()).randomize();
	}
}