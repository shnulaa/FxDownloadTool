package tk.geniusman.manager;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
import tk.geniusman.listener.ChangedListener;
import tk.geniusman.listener.FinishedListener;
import tk.geniusman.listener.LogViewListener;
import tk.geniusman.listener.ProcessChangedListener;
import tk.geniusman.worker.ForkJoinDownloadWorker;

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
    private final Map<String, ForkJoinDownloadWorker> map;

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

    /** the listener of the process changed **/
    private transient LogViewListener logViewListener;

    /** the listener of the process changed **/
    private transient FinishedListener flistener;

    /** the flag represents pause all the download thread **/
    private transient volatile AtomicBoolean pause = new AtomicBoolean(false);

    /** the collections of all the download thread **/
    public transient Set<Thread> threads;

    /** the flag represents shutdown all the download thread **/
    public transient volatile boolean terminate = false;

    /** ExecutorService **/
    public transient ExecutorService singleService = Executors.newSingleThreadExecutor();

    private Manager() {
        this.map = new ConcurrentHashMap<>();
        this.threads = new CopyOnWriteArraySet<>();
    }

    public Map<String, ForkJoinDownloadWorker> getMap() {
        return this.map;
    }

    public ForkJoinDownloadWorker get(String key) {
        return map.get(key);
    }

    public Collection<ForkJoinDownloadWorker> getCollections() {
        return map.values();
    }

    public Manager add(ForkJoinDownloadWorker task) {
        final String key = task.getKey();
        map.put(key, task); // overwrite
        return this;
    }

    public void remove(ForkJoinDownloadWorker task) {
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

    public LogViewListener getLogViewListener() {
        return logViewListener;
    }

    public void setLogViewListener(LogViewListener logViewListener) {
        this.logViewListener = logViewListener;
    }


    public boolean isPause() {
        return pause.get();
    }

    public void pause() {
        this.setPause(true);
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
    public void addFinishListener(FinishedListener listener) {
        this.setFlistener(listener);
    }

    /**
     * 
     * @param listener
     */
    public void addProcessListener(ProcessChangedListener listener) {
        this.setPlistener(listener);
    }

    /**
     * 
     * @param listener
     */
    public void addLogListener(LogViewListener listener) {
        this.setLogViewListener(listener);
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

    public FinishedListener getFlistener() {
        return flistener;
    }

    public void setFlistener(FinishedListener flistener) {
        this.flistener = flistener;
    }

    /**
     * terminate the thread pool
     */
    public void terminate() {
        this.terminate = true;
        this.pause.set(false);

        for (final Thread t : this.threads) {
            if (t != null) {
                LockSupport.unpark(t);
                t.interrupt();
            }
        }

        final java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
        for (java.awt.TrayIcon icon : tray.getTrayIcons()) {
            tray.remove(icon);
        }

        singleService.shutdown();
        UIManager.terminal();
        // System.exit(0);
    }

    public static void main(String[] args) {

        MyThreads t = new MyThreads();
        new Thread(t).start();
        new Thread(t).start();
        new Thread(t).start();
        new Thread(t).start();
        new Thread(t).start();
        new Thread(t).start();
        new Thread(t).start();
        new Thread(t).start();
        new Thread(t).start();
        new Thread(t).start();
        new Thread(t).start();
        new Thread(t).start();
        new Thread(t).start();
        new Thread(t).start();

        // Integer.parseInt("100");

    }

    static class MyThreads implements Runnable {
        AtomicInteger tk = new AtomicInteger(100);

        public void run() {
            int value;
            for (; ((value = tk.getAndDecrement()) > 0);) {
                System.out.println(Thread.currentThread().getName() + "  " + value);
            }
        }
    }

}
