package shnulaa.fx.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import cn.shnulaa.main.ForkJoinDownload;
import cn.shnulaa.manager.ChangedListener;
import cn.shnulaa.manager.Manager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MainLayoutController {
	@FXML
	private TextField address; // local IP address

	@FXML
	private TextField localAddress; // local IP address

	@FXML
	private Button open;

	@FXML
	private Button download;

	@FXML
	private Pane processPane;

	/** array */
	private Rectangle[][] array; // save the Rectangle object to array

	private static final int WIDTH = 100;
	private static final int HEIGHT = 100;

	private static final int PIXELS = WIDTH * HEIGHT;

	private Manager m = Manager.getInstance();
	private ExecutorService service;

	/**
	 * constructor
	 */
	public MainLayoutController() {
	}

	@FXML
	private void initialize() {
		address.setText("http://down.360safe.com/cse/360cse_8.5.0.126.exe");
		localAddress.setText("d:\\");

		this.array = new Rectangle[WIDTH][HEIGHT];
		for (int j = 0; j < WIDTH; j++) {
			for (int i = 0; i < HEIGHT; i++) {
				final Rectangle r = new Rectangle();
				r.setX(i * 3);
				r.setY(j * 2);
				r.setWidth(3);
				r.setHeight(2);
				r.setFill(Color.WHITE);
				array[j][i] = r;
				processPane.getChildren().add(r);
			}
		}

		m.addListener(new ChangedListener() {
			@Override
			public void change(final long current, final Thread t) {
				// final long size = m.getSize();
				// long newCurrent = (current * PIXELS) / size;
				changeColor(current, m.getSize());
			}
		});

		service = Executors.newCachedThreadPool(new ThreadFactory() {
			private AtomicInteger index = new AtomicInteger();

			@Override
			public Thread newThread(Runnable r) {

				Thread t = new Thread(r);
				t.setDaemon(true);
				t.setName("Change-UI" + index.incrementAndGet());
				return t;
			}
		});

		// Collection<DownloadWorker> workers = m.getCollections();
		// if (workers.isEmpty()) {
		// return;
		// }
		//
		// for (final DownloadWorker worker : workers) {
		// worker.addListener(new ChangedListener() {
		// @Override
		// public void change(final long current, final Thread t) {
		// final long size = m.getSize();
		// long newCurrent = (current / size) * PIXELS;
		// changeColor(newCurrent, PIXELS);
		// }
		// });
		// }
	}

	/**
	 * changeColor
	 * 
	 * @param current
	 * @param totol
	 */
	public void changeColor(final long current, final long totol) {
		// service.submit(() -> {
		// int percent = (int) (current * PIXELS / totol);
		//
		// System.out.println(percent);
		// int x = (int) percent / 100;
		// int y = (int) percent % 100;
		// if (x >= 100 || y >= 100) {
		// System.err.println("ArrayIndexOutOfBoundsException 100");
		// return;
		// }
		// final Rectangle r = array[x][y];
		// synchronized (r) {
		// if (r.getFill() != Color.RED) {
		// array[x][y].setFill(Color.RED);
		// }
		// }
		//
		// });

		Platform.runLater(() -> {
			int percent = (int) (current * PIXELS / totol);

			System.out.println(percent);
			int x = (int) percent / 100;
			int y = (int) percent % 100;
			if (x >= 100 || y >= 100) {
				System.err.println("ArrayIndexOutOfBoundsException 100");
				return;
			}
			final Rectangle r = array[x][y];
			synchronized (r) {
				if (r.getFill() != Color.RED) {
					array[x][y].setFill(Color.RED);
				}
			}
		});
	}

	/**
	 * the action for handle the button of listen
	 */
	@FXML
	private void handleDownload() {
		String addressTxt = address.getText();
		if (StringUtils.isEmpty(addressTxt)) {
			showAlert("Download Tools", "address URL must be specified..", Alert.AlertType.ERROR);
			return;
		}

		String localAddressTxt = localAddress.getText();
		if (StringUtils.isEmpty(localAddressTxt)) {
			showAlert("Download Tools", "Local saved Address must be specified..", Alert.AlertType.ERROR);
			return;
		}

		final String[] args = { addressTxt, "15", localAddressTxt, "1.exe" };
		try {
			ForkJoinDownload.main(args);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Rectangle[][] getArray() {
		return array;
	}

	public void setArray(Rectangle[][] array) {
		this.array = array;
	}

	private void showAlert(String title, String message, Alert.AlertType type) {
		Alert a = new Alert(type);
		a.setTitle(title);
		a.setHeaderText(type.name());
		a.setResizable(false);
		a.setContentText(message);
		a.showAndWait();
	}

}
