package worms;

import java.awt.image.BufferedImageOp;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.geom.Point2D;
import java.awt.RenderingHints;

/**
 * Title:        PNEditor
 * Description:  An editor for XML Process Networks.
 * Copyright:    Copyleft 2001
 * Company:      UvA
 * @author Joe Coffland
 * @version 1.0
 */

public class ShuffleImageOp implements BufferedImageOp {
	int blocks;
	int blockWidth;
	int blockHeight;

	public ShuffleImageOp(int blocks, int width, int height) {
		setOps(blocks, width, height);
	}

	public void setOps(int blocks, int width, int height) {
		this.blocks = blocks;
		blockWidth = width / blocks;
		blockHeight = height / blocks;
	}

	public BufferedImage filter(BufferedImage src, BufferedImage dest) {
		if (dest == null) dest = createCompatibleDestImage(src, src.getColorModel());

		int x;
		int y;
		for (x = 0; x < src.getWidth(); x++)
			for (y = 0; y < src.getHeight(); y++) {
				Point2D pt = getPoint2D(new Point2D.Double(x, y), null);

				//System.out.println("" + (int)pt.getX() + " " + (int)pt.getY() + " " + src.getRGB(x, y));
				dest.setRGB((int)pt.getX(), (int)pt.getY(), src.getRGB(x, y));
			}

		return dest;
	}

	public Rectangle2D getBounds2D(BufferedImage src) {
		return new Rectangle2D.Double(0, 0, src.getWidth(), src.getHeight());
	}
	public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {
		return new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
	}

	public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
		int x = (int)srcPt.getX();
		int y = (int)srcPt.getY();

		int newX = x / blockWidth;
		int newY = y / blockHeight;

		if (newX == 0) newX++;
		if (newY == 0) newY++;
		if (newX > blocks) newX--;
		if (newY > blocks) newY--;

		if ((newX + newY) % 2 == 0) {
			newX = x;
			newY = y;
		} else {
			newX = blocks - newX;
			newY = blocks - newY;

			newX = newX * blockWidth + x % blockWidth;
			newY = newY * blockHeight + y % blockHeight;
		}

		if (dstPt == null) dstPt = new Point2D.Double();
		dstPt.setLocation(newX, newY);

		return dstPt;
	}

	public RenderingHints getRenderingHints() {
		return null;
	}
}