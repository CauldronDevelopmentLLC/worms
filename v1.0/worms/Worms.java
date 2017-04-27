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
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;


public class Worms extends JFrame implements Runnable, ActionListener {
	Thread thread;
	BufferedImage image;
	int delay = 100;
	int numWorms;
	Vector worms = new Vector();
	boolean stop;

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

		public Worm() {}

		public Worm(int count) {this.count = count;}

		public Worm(int colorType, int count) {
			this.count = count;
			this.colorType = colorType;
		}

		public void paint(BufferedImage image) {
			if (rgb == -1) rgb = (int)(Math.random() * (256 * 256 * 256));
			if (Worms.rgbStatic == -1) Worms.rgbStatic = rgb;
			if (count == -1)
                count = (int)Math.pow(2, (Math.random() * 12.0)) + 1;
			if (x == -1) x = (int)(Math.random() * image.getWidth());
			if (y == -1) y = (int)(Math.random() * image.getHeight());

			switch (colorType) {
			case CT_CYCLE:
				rgb = (rgb + 1) % (256 * 256 * 256);
				break;

			case CT_CYCLE_STATIC:
				rgb = Worms.rgbStatic =
                    (Worms.rgbStatic + 1) % (256 * 256 * 256);
				break;

			case CT_RAND:
				rgb = (int)(Math.random() * (256 * 256 * 256));
				break;

			case CT_SAME: break;
			}

			int i;
			for (i = 0; i < count; i++) {

				switch ((int)(Math.random() * 8)) {
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

				//System.out.println("printing " + x + " " + y);
				image.setRGB(x, y, rgb);
			}
		}

	}

	public Worms(int delay, int width, int height, int numWorms,
                      int count) {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		this.delay = delay;

		// Init graphics
		setResizable(false);
		setSize(width, height);

		reset(numWorms, count);

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
			image = new BufferedImage(width, height,
                                      BufferedImage.TYPE_INT_RGB);
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


			try {Thread.sleep(0, delay);}
            catch (Exception e) {e.printStackTrace();}
		}
	}

	public void reset(int numWorms, int count) {
		reset(numWorms, count, true);
	}

	public void reset(int numWorms, int count, boolean clearScreen) {
		synchronized (this) {
			if (clearScreen) clear();
			rgbStatic = (int)(Math.random() * (256 * 256 * 256));
			this.numWorms = numWorms;

			if (this.numWorms == -1)
				this.numWorms = (int)Math.pow(2, (Math.random() * 12.0));

			if (count == -1) count = (int)Math.pow(2, (Math.random() * 12.0));

			worms.clear();
			for (int i = 0; i < this.numWorms; i++)
				worms.add(new Worm(Worm.CT_CYCLE_STATIC, count));
		}
	}

	public void actionPerformed(ActionEvent e) {reset(-1, -1);}

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
		Worms worms =  new Worms(1000, 1920, 1080, -1, -1);

		javax.swing.Timer timer = new javax.swing.Timer(20000, worms);
		timer.start();
	}

}
