package cn.shnulaa.thread;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

import cn.shnulaa.manager.Manager;

public class ForkJoinWorkerThreadExt extends ForkJoinWorkerThread {
	private Manager manager = Manager.getInstance();

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
