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

import java.awt.geom.Point2D;
import java.awt.Color;
import java.awt.Graphics;

public class RandWormArtist implements Artist {
  ColorSource cs;

  int size;
  int x = -1;
  int y = -1;

  public RandWormArtist(int size) {
    this.size = size;
  }

  public RandWormArtist(int size, int x, int y) {
    this.size = size;
    this.x = x;
    this.y = y;
  }

  public void setColorSource(ColorSource cs) {
    this.cs = cs;
  }

  public void draw(BufferedImage image, Filter filter) {
    if (x == -1) x = (int)(Math.random() * image.getWidth());
    if (y == -1) y = (int)(Math.random() * image.getHeight());

    //int sqSize = (int)Math.pow(2, Math.random() * 4);
    int sqSize = 10;

    int rgb = -1;
    int i;
    for (i = 0; i < size; i++) {

      int step = 2;
      int dir = (int)(Math.random() * 8);
      switch (dir) {
        case 0: x -= step; y += step; break;
        case 1:            y += step; break;
        case 2: x += step; y += step; break;
        case 3: x -= step;            break;
        case 4: x += step;            break;
        case 5: x -= step; y -= step; break;
        case 6:            y -= step; break;
        case 7: x += step; y -= step; break;
      }

      if (x < 0) x = image.getWidth() - 1;
      if (x >= image.getWidth()) x = 0;
      if (y < 0) y = image.getHeight() - 1;
      if (y >= image.getHeight()) y = 0;

      Point2D pt = new Point2D.Double(x, y);
      if (rgb == -1) rgb = cs.getRGB(pt);
      if (filter != null) rgb = filter.filter(pt, rgb);

      try {
        //Graphics gc = image.getGraphics();
        //gc.setColor(new Color(rgb));
        //gc.fillRect((int)pt.getX(), (int)pt.getY(), sqSize, sqSize);
        image.setRGB((int)pt.getX(), (int)pt.getY(), rgb);
      } catch (ArrayIndexOutOfBoundsException e) {
        System.err.println("ArrayIndexOutOfBoundsException " + (int)pt.getX() + " " + (int)pt.getY() + " " + rgb);
      }
    }
  }

  public void randomize() {}
}