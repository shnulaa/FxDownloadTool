package cn.shnulaa.thread;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;

import cn.shnulaa.manager.Manager;

/**
 * ForkJoinWorkerThreadFactoryExt
 * 
 * @author shnulaa
 *
 */
public class ForkJoinWorkerThreadFactoryExt implements ForkJoinWorkerThreadFactory {

	public ForkJoinWorkerThreadFactoryExt() {
	}

	private final AtomicInteger index = new AtomicInteger();
	private Manager m = Manager.getInstance();

	@Override
	public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
		ForkJoinWorkerThread thread = new ForkJoinWorkerThreadExt(pool);
		thread.setName("ForkJoinThread-" + index.incrementAndGet());
		thread.setDaemon(true);
		m.addThread(thread);
		return thread;
	}

}
