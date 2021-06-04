package tk.geniusman.thread;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ForkJoinWorkerThreadFactoryExt
 * 
 * @author liuyq
 *
 */
public class ForkJoinWorkerThreadFactoryExt implements ForkJoinWorkerThreadFactory {

    /**
     * ForkJoinWorkerThreadFactoryExt
     */
    public ForkJoinWorkerThreadFactoryExt() {
    }

    private final AtomicInteger index = new AtomicInteger();

    @Override
    public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
        ForkJoinWorkerThread thread = new ForkJoinWorkerThreadExt(pool);
        thread.setName("ForkJoinThread-" + index.incrementAndGet());
        thread.setDaemon(true);
        return thread;
    }

}
