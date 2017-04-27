package worms;

/**
 * Title:        PNEditor
 * Description:  An editor for XML Process Networks.
 * Copyright:    Copyleft 2001
 * Company:      UvA
 * @author Joe Coffland
 * @version 1.0
 */
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;


public class Worms extends JFrame implements Runnable, ActionListener {
	Thread thread;
	BufferedImage image;
	ShuffleImageOp shuffle[];
	int delay = 100;
	int numWorms;
	Vector worms = new Vector();
	boolean stop;

	public int [] textureBuf;

	public static int rgbStatic = -1;

	public class Worm {
		public static final int CT_SAME  = 0;
		public static final int CT_CYCLE = 1;
		public static final int CT_CYCLE_STATIC = 2;
		public static final int CT_RAND  = 3;

		int x = -1;
		int y = -1;
		int count = -1;
		int rgb = -1;
		int colorType = CT_RAND;
		int length = 1;

		public Worm() {
		}

		public Worm(int count) {
			this.count = count;
		}

		public Worm(int colorType, int count, int length) {
			this.count = count;
			this.colorType = colorType;
			this.length = length;
		}

		public void paint(BufferedImage image) {
			if (rgb == -1) rgb = (int)(Math.random() * (256 * 256 * 256));
			if (Worms.rgbStatic == -1) Worms.rgbStatic = rgb;
			if (count == -1) count = (int)Math.pow(2, (Math.random() * 12.0)) + 1;
			if (x == -1) x = (int)(Math.random() * image.getWidth());
			if (y == -1) y = (int)(Math.random() * image.getHeight());


			switch (colorType) {
			case CT_CYCLE:
				rgb = (rgb + 1) % (256 * 256 * 256);
				break;

			case CT_CYCLE_STATIC:
				int color =  (int)(Math.random() * 3);
				int change = 6 - (int)Math.pow(Math.random() * (int)Math.pow(5, 4), 1.0/4.0);
				if ((int)(Math.random() * 2) == 0) change = -change;

				switch (color) {
				case 0: change *= 256;
				case 1: change *= 256;
				case 2:
				}
				rgb = Worms.rgbStatic = Worms.rgbStatic + change;
				break;

			case CT_RAND:
				rgb = (int)(Math.random() * (256 * 256 * 256));
				break;

			case CT_SAME: break;
			}

			int i;
			for (i = 0; i < count; i++) {

				int dir = (int)(Math.random() * 8);
				int j;
				for (j = 0; j < length; j++) {
					switch (dir) {
					case 0: x--; y++; break;
					case 1:      y++; break;
					case 2: x++; y++; break;
					case 3: x--;      break;
					case 4: x++;      break;
					case 5: x--; y--; break;
					case 6:      y--; break;
					case 7: x++; y--; break;
					}

					if (x < 0) x = image.getWidth() - 1;
					if (x >= image.getWidth()) x = 0;
					if (y < 0) y = image.getHeight() - 1;
					if (y >= image.getHeight()) y = 0;

					Point2D pt = new Point2D.Double(x, y);
					try {
						int k;
						for (k = 0; k < Math.random() * shuffle.length; k++)
							pt = shuffle[k].getPoint2D(pt, null);

						//rgb = textureBuf[x + y * image.getWidth()];

						image.setRGB((int)pt.getX(), (int)pt.getY(), rgb);
					} catch (ArrayIndexOutOfBoundsException e) {
						System.err.println("ArrayIndexOutOfBoundsException " + (int)pt.getX() + " " + (int)pt.getY() + " " + rgb);
					}
				}
			}
		}

	}

	public Worms(int delay, int width, int height, int numWorms, int count,
										int length, Image textureImage, int shuffles) {

		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		this.delay = delay;
		this.textureBuf = new int [width * height];
		PixelGrabber pg = new PixelGrabber(textureImage, 0, 0, width,
																			 height, textureBuf, 0,
																			 width);

		shuffle = new ShuffleImageOp[shuffles];
		int i;
		for (i = 0; i < shuffles; i++)
			shuffle[i] = new ShuffleImageOp(16, 800, 600);

		try {
			pg.grabPixels();
		} catch (Exception e) {
			e.printStackTrace();
		}

		textureBuf = (int[])pg.getPixels();

		// Init graphics
		setResizable(false);
		setSize(width, height);

		reset(numWorms, count, length);

		// Start drawing
		stop = false;
		thread = new Thread(this);
		thread.start();

		validate();
		show();
	}

	public void setSize(int width, int height) {
		super.setSize(width, height);
		synchronized (this) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		}
	}

	public void run() {
		while (!stop) {
			int i;
			for (i = 0; i < numWorms; i++) {
				synchronized(this) {
					if (i >= numWorms) break;
					((Worm)worms.get(i)).paint(image);
				}
				repaint();
			}


			try {Thread.sleep(0, delay);} catch (Exception e) {e.printStackTrace();}
		}
	}

	public static double expDistRand(double exp) {
		return 1 - Math.pow(Math.random(), 1.0/exp);
	}

	public void reset(int numWorms, int count, int length) {
		reset(numWorms, count, length, true);
	}

	public void reset(int numWorms, int count, int length, boolean clearScreen) {
		synchronized (this) {
			if (clearScreen) clear();
			rgbStatic = (int)(Math.random() * (256 * 256 * 256));
			this.numWorms = numWorms;

			if (this.numWorms == -1)
				this.numWorms = (int)Math.pow(2, (Math.random() * 12.0));

			if (count == -1) count = (int)Math.pow(2, (Math.random() * 12.0));

			worms.clear();
			for (int i = 0; i < this.numWorms; i++)
				worms.add(new Worm(Worm.CT_CYCLE_STATIC, count, length));

			for (int i = 0; i < shuffle.length; i++)
				shuffle[i].setOps(1+(int)Math.pow(2, expDistRand(2) * 8), 800, 600);
		}
	}

	public void actionPerformed(ActionEvent e) {
		reset(-1, -1, 1);
//		reset(-1, -1, 50 - (int)Math.pow(Math.random() * (int)Math.pow(50, 12), 1.0/12.0));
	}

	/**Overridden so we can exit when window is closed*/
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			stop = true;
			System.exit(0);
		}
	}

	public void clear() {
		int x, y;
		for (x = 0; x < image.getWidth(); x++)
			for (y = 0; y < image.getHeight(); y++)
				image.setRGB(x, y, 0);
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;

		g2.drawImage(image, 0, 0, this);
	}


	static public void main(String args[]) {
		Image textureImage = Toolkit.getDefaultToolkit().createImage("gcode-viewer-01.png");
		textureImage = textureImage.getScaledInstance(800, 600, Image.SCALE_DEFAULT);

		Worms worms =  new Worms(1000, 800, 600, -1, -1, 1, textureImage, 0);

		javax.swing.Timer timer = new javax.swing.Timer(20000, worms);
		timer.start();
	}

}
