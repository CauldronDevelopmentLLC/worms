package worms;

/**
 * Title:        PNEditor
 * Description:  An editor for XML Process Networks.
 * Copyright:    Copyleft 2001
 * Company:      UvA
 * @author Joe Coffland
 * @version 1.0
 */

public interface ColorSourceCollection {
	public int size();
	public ColorSource getRandCS();
	public ColorSource[] getRandCS(int count);
	public ColorSource getCS(int i);
}