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

public class NullFilter implements Filter {
	public NullFilter() {}
	public int filter(Point2D pt, int color) {return color;}
	public void step() {}
	public void randomize() {}
}