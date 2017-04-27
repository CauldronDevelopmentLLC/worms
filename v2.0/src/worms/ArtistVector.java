package worms;

import java.awt.image.BufferedImage;

/**
 * Title:        PNEditor
 * Description:  An editor for XML Process Networks.
 * Copyright:    Copyleft 2001
 * Company:      UvA
 * @author Joe Coffland
 * @version 1.0
 */

import java.util.*;

public class ArtistVector implements Artist {
	ColorSource cs;
	Vector artists = new Vector();

	public ArtistVector() {
	}

	public void addArtist(Artist artist) {
		artists.add(artist);
	}

	public void clear() {
		artists.clear();
	}

	public void setColorSource(ColorSource cs) {
		this.cs = cs;
		Enumeration enumeration = artists.elements();
		while (enumeration.hasMoreElements())
			((Artist)enumeration.nextElement()).setColorSource(cs);
	}

	public void draw(BufferedImage image, Filter filter) {
		Enumeration enumeration = artists.elements();
		while (enumeration.hasMoreElements())
			((Artist)enumeration.nextElement()).draw(image, filter);
	}

	public void randomize() {
		Enumeration enumeration = artists.elements();
		while (enumeration.hasMoreElements())
			((Artist)enumeration.nextElement()).randomize();
	}
}