package worms;

/**
 * Title:        PNEditor
 * Description:  An editor for XML Process Networks.
 * Copyright:    Copyleft 2001
 * Company:      UvA
 * @author Joe Coffland
 * @version 1.0
 */

import java.awt.image.BufferedImage;

public interface Artist {
	public void setColorSource(ColorSource cs);
	public void draw(BufferedImage image, Filter filter);
	public void randomize();
}