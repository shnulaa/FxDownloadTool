package cn.shnulaa.manager;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * RectangleManager
 * 
 * @author liuyq
 *
 */
public class UIManager {

	private static final int WIDTH = 100;
	private static final int HEIGHT = 100;
	private static final int PIXELS = WIDTH * HEIGHT;
	private Rectangle[][] array;
	private ProgressBar process;
	private Label speedLab;
	private Label percentLab;

	/**
	 * RectangleManager
	 * 
	 * @param array
	 */
	private UIManager(final Rectangle[][] array, final ProgressBar process, final Label speedLab,
			final Label percentLab) {
		this.array = array;
		this.process = process;
		this.speedLab = speedLab;
		this.percentLab = percentLab;
	}

	/**
	 * changePercent
	 * 
	 * @param rate
	 * @param speed
	 */
	public void changePercent(double rate, long speed) {
		process.progressProperty().set(rate);
		percentLab.setText((int) (rate * 100) + "%");
		speedLab.setText(String.valueOf(speed) + "KB/S");
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
	public static UIManager newInstance(final Rectangle[][] array, final ProgressBar process, final Label speedLab,
			final Label percentLab) {
		return new UIManager(array, process, speedLab, percentLab);
	}

}
