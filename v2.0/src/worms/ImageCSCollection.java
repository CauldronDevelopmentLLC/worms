package worms;

import java.util.Collection;
import java.util.Iterator;

/**
 * Title:        PNEditor
 * Description:  An editor for XML Process Networks.
 * Copyright:    Copyleft 2001
 * Company:      UvA
 * @author Joe Coffland
 * @version 1.0
 */

import java.util.Vector;
import java.io.*;

public class ImageCSCollection implements ColorSourceCollection {
	Vector files;
	int w;
	int h;

	public ImageCSCollection(int w, int h) {
		this.w = w;
		this.h = h;
		files = new Vector();
	}

	public ImageCSCollection(int w, int h, Vector files) {
		this.w = w;
		this.h = h;
		this.files = files;
		if (this.files == null) this.files = new Vector();
	}

	public boolean add(String fileStr) {
		return add(fileStr, false);
	}

	public boolean add(String fileStr, boolean recursive) {
		File file = new File(fileStr);
		if (!file.exists()) return false;

		if (file.isDirectory()) {
			File dir = new File(fileStr);
			File[] dirFiles = dir.listFiles(new ImagesFileFilter());

			int i;
			for (i = 0; i < dirFiles.length; i++) {
				if (dirFiles[i].isDirectory() && recursive) {
					add(dirFiles[i].getPath(), true);
					continue;
				}

				add(dirFiles[i].getPath(), recursive);
			}
		} else {
			System.out.println("Adding " + fileStr);
			files.add(fileStr);
		}

		return true;
	}

	public int size() {return files.size();}

	public ColorSource[] getRandCS(int count) {
		ColorSource[] css = new ColorSource[count];
		int i;
		for (i = 0; i < count; i++) css[i] = getRandCS();

		return css;
	}

	public ColorSource getRandCS() {
		return getCS((int)(Math.random() * size()));
	}

	public ColorSource getCS(int i) {
		return new ImageColorSource((String)files.get(i), w, h);
	}
}