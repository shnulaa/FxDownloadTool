package cn.shnulaa.thread;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

public class ForkJoinWorkerThreadExt extends ForkJoinWorkerThread {

	protected ForkJoinWorkerThreadExt(ForkJoinPool pool) {
		super(pool);
		// TODO Auto-generated constructor stub
	}

}
