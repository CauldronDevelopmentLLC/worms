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

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class ImageColorSource implements ColorSource {
	int[] imageBuf;
	int w;
	int h;

	public ImageColorSource(String filename, int w, int h) {
		this.w = w;
		this.h = h;
		Image image = Toolkit.getDefaultToolkit().createImage(filename);
		Image scaledImage = image.getScaledInstance(w, h, Image.SCALE_DEFAULT);

		imageBuf = new int [w * h];
		PixelGrabber pg = new PixelGrabber(scaledImage, 0, 0, w, h, imageBuf, 0, w);

		try {
			pg.grabPixels();
		} catch (Exception e) {
			e.printStackTrace();
		}

		scaledImage.flush();
		image.flush();
	}

	public int getRGB(Point2D pt) {
		return imageBuf[(int)pt.getX() + (int)pt.getY() * w];
	}

	public void step() {}

	public void randomize() {}
}