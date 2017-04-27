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

public class WormsArtist extends ArtistVector {
	int minCountPow = 4;
    //int minSizePow = 10;
    int maxCountPow = 12;
    int minSizePow = 1;
	int maxSizePow = 14;
	ColorSourceCollection csc = null;

	public WormsArtist(int count, int size) {
		init(count, size);
	}

	private void init(int count, int size) {
		int i;
		for (i = 0; i < count; i++) {
			Artist worm = new RandWormArtist(size);
			addArtist(worm);
		}
		assignColorSources();
	}

	private void assignColorSources() {
		Enumeration enumeration = artists.elements();

		if (csc != null) {
			//int numCS = 1 + ((int)(RandomDist.exp(2) * csc.size()) % 4);
			int numCS = (int)Math.floor(Math.pow(2, 3 * Math.random()));
			ColorSource[] css = csc.getRandCS(numCS);

			int csNum = 0;
			while (enumeration.hasMoreElements()) {
				((Artist)enumeration.nextElement()).setColorSource(css[csNum]);
				csNum = (csNum + 1) % css.length;
			}

		} else {
			while (enumeration.hasMoreElements())
				((Artist)enumeration.nextElement()).setColorSource(cs);
		}
	}

	public void setColorSource(ColorSourceCollection csc) {
		this.csc = csc;
		assignColorSources();
	}

	public void randomize() {
		//clear();
		init((int)Math.pow(2, (minCountPow + Math.random() *
                               (double)(maxCountPow - minCountPow))),
             (int)Math.pow(2, (minSizePow + Math.random() *
                               (double)(maxSizePow - minSizePow))));
	}
}
