package tk.geniusman.thread;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

import tk.geniusman.manager.Manager;

/**
 * ForkJoinWorkerThreadExt
 * 
 * @author liuyq
 *
 */
public class ForkJoinWorkerThreadExt extends ForkJoinWorkerThread {
    private Manager manager = Manager.getInstance();

    /**
     * ForkJoinWorkerThreadExt
     * 
     * @param pool
     */
    protected ForkJoinWorkerThreadExt(ForkJoinPool pool) {
        super(pool);
    }

    @Override
    protected void onStart() {
        manager.addThread(this);
    }

    @Override
    protected void onTermination(Throwable exception) {
        manager.removeThread(this);
    }
}
