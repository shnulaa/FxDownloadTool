package tk.geniusman.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author liuyq
 *
 */
public class DefaultThreadFactoryExt implements ThreadFactory {

  public DefaultThreadFactoryExt() {}

  private final AtomicInteger index = new AtomicInteger();

  @Override
  public Thread newThread(Runnable r) {
    Thread thread = new Thread(r);
    thread.setName("ForkJoinThread-" + index.incrementAndGet());
    thread.setDaemon(true);
    return thread;
  }

}
