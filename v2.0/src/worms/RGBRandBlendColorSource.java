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

public class RGBRandBlendColorSource implements ColorSource {
	int rgb;
	int dist;
	int maxChange;

	public RGBRandBlendColorSource(int rgb, int maxChange, int dist) {
		this.dist = dist;
		this.rgb = rgb;
		this.maxChange = maxChange;
		if (rgb == -1) randomize();
	}

	public int getRGB(Point2D pt) {
		return rgb;
	}

	public void step() {
		int color =  (int)(Math.random() * 3);
		double change = maxChange - (int)Math.pow(Math.random() * (int)Math.pow(maxChange, dist), 1.0/dist) + 1;
                //double change = (double)maxChange * Math.random();
		if ((int)(Math.random() * 2) == 0) change = -change;

		switch (color) {
		case 0: change *= 256;
		case 1: change *= 256;
		case 2:
		}

		rgb += change;
	}

	public void randomize() {
		rgb = (int)(Math.random() * (256 * 256 * 256));
	}
}