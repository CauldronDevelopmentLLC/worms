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

public interface Filter {
	public int filter(Point2D pt, int color);
	public void step();
	public void randomize();
}