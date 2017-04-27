package worms;

/**
 * Title:        PNEditor
 * Description:  An editor for XML Process Networks.
 * Copyright:    Copyleft 2001
 * Company:      UvA
 * @author Joe Coffland
 * @version 1.0
 */

public class RandomDist {

	public RandomDist() {
	}

	public static double exp(double exp) {
		return 1 - Math.pow(Math.random(), 1.0/exp);
	}
}