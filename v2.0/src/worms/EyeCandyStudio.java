package worms;

/**
 * Title:        PNEditor
 * Description:  An editor for XML Process Networks.
 * Copyright:    Copyleft 2001
 * Company:      UvA
 * @author Joe Coffland
 * @version 1.0
 */

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class EyeCandyStudio extends JFrame implements ActionListener {
  BufferedImage image;

  Artist artist;
  ColorSource cs;
  Filter filter;
  int delay;
  boolean drawing = false;

  Timer updateTimer;

  public EyeCandyStudio(Artist artist, ColorSource cs, Filter filter,
                        int delay) {
    this.artist = artist;
    this.filter = filter;
    this.delay = delay;
    this.cs = cs;
    randomize();

    // Init graphics
    setUndecorated(true);

    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gd = ge.getDefaultScreenDevice();
    gd.setFullScreenWindow(this);

    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    setResizable(false);

    setSize(gd.getDisplayMode().getWidth(),
            gd.getDisplayMode().getHeight());
    validate();
    show();


    (updateTimer = new Timer(1000/30, this)).start();
  }

  public void setSize(int width, int height) {
    super.setSize(width, height);
    synchronized (this) {
      image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }
  }

  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      updateTimer.stop();
      System.exit(0);
    }
  }

  public void randomize() {
    artist.randomize();
    if (filter != null) filter.randomize();
    if (cs != null) cs.randomize();
  }

  public void paint(Graphics g) {
    Graphics2D g2 = (Graphics2D)g;

    if (image != null)
      g2.drawImage(image, 0, 0, this);
  }

  public void clear() {
    updateTimer.stop();
    int x, y;
    for (x = 0; x < image.getWidth(); x++)
      for (y = 0; y < image.getHeight(); y++)
        image.setRGB(x, y, 0);

    updateTimer.start();
  }

  public void reset() {
    synchronized (this) {
      clear();
      randomize();
    }
  }

  public void actionPerformed(ActionEvent e) {
    repaint();

    if (drawing) return;

    synchronized (this) {
      drawing = true;

      artist.draw(image, filter);

      if (cs != null) cs.step();
      filter.step();

      drawing = false;
    }
  }

  static public ColorSource[] loadCSImages(String dirStr, int w, int h) {
    ColorSource[] css;
    File dir = new File(dirStr);
    File[] files = dir.listFiles(new ImagesFileFilter());

    int len = files.length;
    if (len > 25 ) len = 25;
    css = new ColorSource[len];

    int i;
    for (i = 0; i < len; i++) {
      css[i] = new ImageColorSource(files[i].getPath(), w, h);
      System.out.println("loaded " + files[i].getPath());
    }

    return css;
  }

  static public void main(String args[]) {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gd = ge.getDefaultScreenDevice();

    int w = gd.getDisplayMode().getWidth();
    int h = gd.getDisplayMode().getHeight();

    WormsArtist artist = new WormsArtist( -1, -1);
    ImageCSCollection csc = new ImageCSCollection(w, h);
    csc.add("/media/Pics/Backgrounds/", true);
    //csc.add("/media/Pics/Backgrounds/Peru_Machu_Picchu_Sunset.jpg", true);
    //csc.add("/media/Pics/sorted/C3030/", true);
    //csc.add("images/", true);
    csc.add("/mnt/bucket/media/Clips/Trade_Center/pics", true);
    int j;
    for (j = 0; j < args.length; j++) csc.add(args[j], true);
    artist.setColorSource(csc);

    ColorSource cs = new RGBRandBlendColorSource(-1, 256, 1);
    //artist.setColorSource(cs);

    RandomFilterVector filter = new RandomFilterVector(1);

    RandomDepthFilterVector randomDepthFilterVector =
        new RandomDepthFilterVector();

    /*
    int i;
    for (i = 0; i < 9; i++)
     randomDepthFilterVector.add(new SquareSwapFilter(8, w, h));
    */

    //filter.add(new NullFilter());
    //filter.add(randomDepthFilterVector);
    //filter.add(new SquareSwapFilter(8, w, h));

    try {
        EyeCandyStudio ecs =
            new EyeCandyStudio(artist, cs, new NullFilter(), 2);

      while (true) {
        try {
          Thread.sleep(10000);

        } catch (Exception e) {
          e.printStackTrace();
        }

        cs.randomize();
        ecs.reset();
      }

    } finally {

    }
  }
}
