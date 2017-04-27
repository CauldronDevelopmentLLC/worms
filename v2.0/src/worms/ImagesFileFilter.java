package worms;

import java.io.FilenameFilter;
import java.io.File;

/**
 * Title:        PNEditor
 * Description:  An editor for XML Process Networks.
 * Copyright:    Copyleft 2001
 * Company:      UvA
 * @author Joe Coffland
 * @version 1.0
 */

public class ImagesFileFilter implements FilenameFilter {

	public ImagesFileFilter() {
	}

	public boolean accept(File file, String pathname) {
		if (pathname.endsWith(".jpg")) return true;
		if (pathname.endsWith(".jpeg")) return true;
		if (pathname.endsWith(".JPG")) return true;
		if (file.isDirectory()) return true;
		return false;
	}
}