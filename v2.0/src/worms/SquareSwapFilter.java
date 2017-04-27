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

import java.awt.geom.Point2D;

public class SquareSwapFilter implements Filter {
	int blocks;
	int blockWidth;
	int blockHeight;
	int w;
	int h;

	public SquareSwapFilter(int blocks, int w, int h) {
		this.w = w;
		this.h = h;
		init(blocks);
	}

	private void init(int blocks) {
		this.blocks = blocks;
		blockWidth = w / blocks;
		blockHeight = h / blocks;
	}

	public int filter(Point2D pt, int color) {
		int newX = (int)Math.floor(pt.getX() / blockWidth);
		int newY = (int)Math.floor(pt.getY() / blockHeight);

		if (newX == blocks) newX--;
		if (newY == blocks) newY--;

		if ((newX + newY) % 2 == 0) {
			newX = (int)pt.getX();
			newY = (int)pt.getY();
		} else {
			//System.err.println("" + blocks + " " + newX + " " + newY);
			newX = blocks - newX - 1;
			newY = blocks - newY - 1;

			newX = newX * blockWidth + (int)pt.getX() % blockWidth;
			newY = newY * blockHeight + (int)pt.getY() % blockHeight;

			if (newX < 0) newX += w;
			if (newY < 0) newY += h;
		}

		pt.setLocation(newX, newY);

		return color;
	}

	public void step() {
	}

	public void randomize() {
		init(1+(int)Math.pow(2, RandomDist.exp(2) * 8));
	}
}