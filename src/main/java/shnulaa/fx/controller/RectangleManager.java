package shnulaa.fx.controller;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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
	private RectangleManager(Rectangle[][] array) {
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

	public static RectangleManager newInstance(Rectangle[][] array) {
		return new RectangleManager(array);
	}

}
