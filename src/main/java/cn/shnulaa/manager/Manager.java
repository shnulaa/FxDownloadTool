package cn.shnulaa.manager;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

import cn.shnulaa.worker.DownloadWorker;

/**
 * Singleton instance.
 * 
 * @author liuyq
 * 
 */
public class Manager implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3879351808960296777L;
	/** key(start-end) value(task info), keep all worker to this map **/
	private final Map<String, DownloadWorker> map;

	/** the size that already download **/
	public final AtomicLong alreadyRead = new AtomicLong(0);

	/** the previous size that already download **/
	public final AtomicLong preAlreadyRead = new AtomicLong(0);

	/** is the mode recovery **/
	public volatile boolean recovery = false;

	/** download file size **/
	private transient long size;

	/** the listener of the file size changed **/
	private transient ChangedListener listener;

	/** the listener of the process changed **/
	private transient ProcessChangedListener plistener;

	/** the flag represents pause all the download thread **/
	private transient volatile AtomicBoolean pause = new AtomicBoolean(false);

	/** the collections of all the download thread **/
	public transient Set<Thread> threads;

	private Manager() {
		this.map = new ConcurrentHashMap<>();
		this.threads = new CopyOnWriteArraySet<>();
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

	public void removeThread(Thread t) {
		this.threads.remove(t);
	}

	public void resume() {
		if (this.pause.compareAndSet(true, false)) {
			for (Thread t : this.threads) {
				if (t != null) {
					LockSupport.unpark(t);
				}
			}
		}
	}

	/**
	 * 
	 * @param listener
	 */
	public void addListener(ChangedListener listener) {
		this.setListener(listener);
	}

	/**
	 * 
	 * @param listener
	 */
	public void addProcessListener(ProcessChangedListener listener) {
		this.setPlistener(listener);
	}

	/**
	 * clear
	 */
	public void clear() {
		if (map != null) {
			map.clear();
		}
		if (threads != null) {
			threads.clear();
		}
		alreadyRead.set(0);
		preAlreadyRead.set(0);
		size = 0;
	}
}