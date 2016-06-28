package cn.shnulaa.manager;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * RectangleManager
 * 
 * @author liuyq
 *
 */
public class RectangleManager {

	private static final int WIDTH = 100;
	private static final int HEIGHT = 100;
	private static final int PIXELS = WIDTH * HEIGHT;
	private Rectangle[][] array;

	/**
	 * RectangleManager
	 * 
	 * @param array
	 */
	private RectangleManager(final Rectangle[][] array) {
		this.array = array;
	}

	/**
	 * changeColor
	 * 
	 * @param current
	 * @param totol
	 */
	public void changeColor(final long current, final long total) {
		Platform.runLater(() -> {

			int percent = (int) (current * PIXELS / total);

			int x = (int) percent / WIDTH;
			int y = (int) percent % HEIGHT;
			if (x >= 100 || y >= 100) {
				return;
			}

			// System.out.println(String.format("percent:%s, x:%s, y:%s",
			// percent, x, y));
			final Rectangle r = array[x][y];
			if (r == null) {
				return;
			}
			synchronized (r) {
				if (r.getFill() != Color.RED) {
					array[x][y].setFill(Color.RED);
				}
			}
		});
	}

	/**
	 * newInstance
	 * 
	 * @param array
	 * @return new instance of RectangleManager
	 */
	public static RectangleManager newInstance(final Rectangle[][] array) {
		return new RectangleManager(array);
	}

}
