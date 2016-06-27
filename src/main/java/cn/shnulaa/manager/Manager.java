package cn.shnulaa.manager;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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

	public void addListener(ChangedListener listener) {
		this.setListener(listener);
	}

	/** is the mode recovery **/
	public volatile boolean recovery = false;

	private Manager() {
		this.map = new ConcurrentHashMap<>();
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

}