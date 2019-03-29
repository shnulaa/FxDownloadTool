package tk.geniusman.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import tk.geniusman.manager.Manager;

/**
 * DefaultThreadPoolExecutorExt
 * 
 * @author liuyq
 *
 */
public class DefaultThreadPoolExecutorExt extends ThreadPoolExecutor {

    private static final Manager m = Manager.getInstance();

    /**
     * getDefaultInstance
     * 
     * @param threadNum
     * @return
     */
    public static ExecutorService getDefaultInstance(int threadNum) {
        return new DefaultThreadPoolExecutorExt(threadNum, threadNum, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new DefaultThreadFactoryExt());
    }

    /**
     * DefaultThreadPoolExecutorExt
     * 
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     */
    public DefaultThreadPoolExecutorExt(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue<Runnable> workQueue, ThreadFactory threadFoctory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFoctory);
    }

    /**
     * beforeExecute
     */
    protected void beforeExecute(Thread t, Runnable r) {
        m.addThread(t);
    }

    /**
     * afterExecute
     */
    protected void afterExecute(Runnable r, Throwable t) {
    }

}
