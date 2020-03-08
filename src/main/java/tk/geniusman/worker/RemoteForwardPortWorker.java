package tk.geniusman.worker;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import tk.geniusman.downloader.Args;

public class RemoteForwardPortWorker implements Worker {

  private Args args;

  public RemoteForwardPortWorker(Args args) {
    this.args = args;
  }

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = -3342073955000718580L;

  @Override
  public void extractAndExec() {
    try {
      Session session = connectorManager.getSession();
      if (session != null) {
        session.setPortForwardingR(Integer.valueOf(args.getRemoteForwardPort()),
            args.getRemoteSshHost(), Integer.valueOf(args.getLocalListningPort()));
      }
    } catch (JSchException e) {
      e.printStackTrace();
    }
  }

}
