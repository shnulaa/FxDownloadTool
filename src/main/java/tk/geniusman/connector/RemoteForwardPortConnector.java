package tk.geniusman.connector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tk.geniusman.downloader.Args;
import tk.geniusman.worker.RemoteForwardPortWorker;

/**
 * 
 * @author liuyq
 *
 */
public class RemoteForwardPortConnector extends AbstractConnector {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = -2937255625031061832L;

  /**
   * RemoteForwardPortConnector
   * 
   * @param args
   */
  public RemoteForwardPortConnector(final Args args) {
    super(args);
  }

  @Override
  protected <T extends ExecutorService> T startMainTask(Args args) throws Exception {
    ExecutorService service = Executors.newSingleThreadExecutor();
    service.execute(new RemoteForwardPortWorker(args));
    return (T) service;
  }

}
