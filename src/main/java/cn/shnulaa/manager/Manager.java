package cn.shnulaa.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

import cn.shnulaa.worker.DownloadWorker;

/**
 * Manager the DownloadWorker
 * 
 * @author liuyq
 * 
 */
public class Manager implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3879351808960296777L;
	/** key(start-end) value(task info) **/
	private final Map<String, DownloadWorker> map;

	public final AtomicLong alreadyRead = new AtomicLong(0);
	public final AtomicLong preAlreadyRead = new AtomicLong(0);

	private transient long size;

	private transient ChangedListener listener;
	private transient ProcessChangedListener plistener;

	private transient volatile AtomicBoolean pause = new AtomicBoolean(false);

	public transient List<Thread> threads;

	public void addListener(ChangedListener listener) {
		this.setListener(listener);
	}

	public void addProcessListener(ProcessChangedListener listener) {
		this.setPlistener(listener);
	}

	public void clear() {
		if (map != null) {
			map.clear();
		}
		alreadyRead.set(0);
		preAlreadyRead.set(0);
		size = 0;
	}

	/** is the mode recovery **/
	public volatile boolean recovery = false;

	private Manager() {
		this.map = new ConcurrentHashMap<>();
		this.threads = new ArrayList<>();
	}

	public Map<String, DownloadWorker> getMap() {
		return this.map;
	}

	public DownloadWorker get(String key) {
		return map.get(key);
	}

	public Collection<DownloadWorker> getCollections() {
		return map.values();
	}

	public Manager add(DownloadWorker task) {
		final String key = task.getKey();
		map.put(key, task); // overwrite
		return this;
	}

	public void remove(DownloadWorker task) {
		final String key = task.getKey();
		this.map.remove(key);
	}

	static class SingletonHolder {
		public static final Manager Manager = new Manager();
	}

	public static Manager getInstance() {
		return SingletonHolder.Manager;
	}

	public void recovry(Manager re) {
		alreadyRead.set(re.alreadyRead.get());
		preAlreadyRead.set(re.preAlreadyRead.get());
		map.putAll(re.getMap());
	}

	public long getPerSecondSpeed() {
		long speed = alreadyRead.get() - preAlreadyRead.get();
		preAlreadyRead.set(alreadyRead.get());
		return (speed / 1000);
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public ChangedListener getListener() {
		return listener;
	}

	public void setListener(ChangedListener listener) {
		this.listener = listener;
	}

	public ProcessChangedListener getPlistener() {
		return plistener;
	}

	public void setPlistener(ProcessChangedListener plistener) {
		this.plistener = plistener;
	}

	public boolean isPause() {
		return pause.get();
	}

	public void setPause(boolean pause) {
		this.pause.set(pause);
	}

	public void addThread(Thread t) {
		this.threads.add(t);
	}

	public void resume() {
		if (this.pause.compareAndSet(true, false)) {
			for (Thread t : this.threads) {
				LockSupport.unpark(t);

			}

		}

	}
}