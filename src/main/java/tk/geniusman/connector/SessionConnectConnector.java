package tk.geniusman.connector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tk.geniusman.downloader.Args;
import tk.geniusman.worker.SessionConnectWorker;

/**
 * 
 * @author liuyq
 *
 */
public class SessionConnectConnector extends AbstractConnector {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = -2937255625031061832L;

  /**
   * RemoteForwardPortConnector
   * 
   * @param args
   */
  public SessionConnectConnector(final Args args) {
    super(args);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected <T extends ExecutorService> T startMainTask(Args args) throws Exception {
    ExecutorService service = Executors.newSingleThreadExecutor();
    service.execute(new SessionConnectWorker(args));
    return (T) service;
  }

}
