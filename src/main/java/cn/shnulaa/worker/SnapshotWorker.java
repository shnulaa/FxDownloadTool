package cn.shnulaa.worker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import cn.shnulaa.bar.ProgressBar;
import cn.shnulaa.manager.Manager;

/**
 * SnapshotWorker
 * 
 * @author liuyq
 *
 */
public class SnapshotWorker implements Runnable {
	private File sFile;
	private long size;
	/** the instance of Manager **/
	private static final Manager m = Manager.getInstance();

	public SnapshotWorker(File sFile, long size) {
		this.sFile = sFile;
		this.size = size;
	}

	@Override
	public void run() {
		try {
			double current = 100 * (Double.valueOf(m.alreadyRead.get()) / size);
			long speed = m.getPerSecondSpeed();
			System.out.print(ProgressBar.showBarByPoint(current, 100, 70, speed, true));
			System.out.flush();
			writeObject(m, sFile);
			m.getPlistener().change(current / 100, speed, Thread.currentThread());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * writeObject
	 * 
	 * @param object
	 */
	static void writeObject(Object object, File path) {
		try (OutputStream os = new FileOutputStream(path, false);
				ObjectOutputStream oos = new ObjectOutputStream(os);) {
			oos.writeObject(object);
			oos.flush();
		} catch (Exception e) {
			System.err.println("exception occurred when write object.");
			e.printStackTrace();
		}
	}

}
