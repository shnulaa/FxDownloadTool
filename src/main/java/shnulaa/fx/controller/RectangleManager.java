package shnulaa.fx.controller;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RectangleManager {

	private Rectangle[][] array;

	private RectangleManager(Rectangle[][] array) {
		this.array = array;
	}

	/**
	 * 
	 * @param current
	 * @param totol
	 */
	public void changeColor(final long current, final long totol) {
		Platform.runLater(() -> {
			int percent = (int) (current * 10000 / totol);
			int x = (int) percent / 100;
			int y = (int) percent % 100;
			array[x][y].setFill(Color.RED);
		});
	}
	//
	// static class SingletonHolder {
	// public static final RectangleManager Manager = new RectangleManager();
	// }
	//
	// public static RectangleManager getInstance() {
	// return SingletonHolder.Manager;
	// }

}
