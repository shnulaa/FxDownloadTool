package tk.geniusman.worker;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import tk.geniusman.downloader.Args;

public class RemoteForwardPortWorker implements Worker {

  public RemoteForwardPortWorker(Args args) {

  }

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = -3342073955000718580L;

  @Override
  public void extractAndExec() {
    // TODO Auto-generated method stub

    try {
      JSch jsch = new JSch();
      Session session = jsch.getSession("ubuntu", "122.51.212.235", 22);
      session.setPassword("(lyq522095)");

      java.util.Properties config = new java.util.Properties();
      config.put("StrictHostKeyChecking", "no");
      config.put("Compression", "yes");
      config.put("ConnectionAttempts", "2");

      session.setConfig(config);
      session.connect();
      // int assignedPort = session.setPortForwardingL(1234, "122.51.212.235", 22);
      // System.out.println(assignedPort);


      session.setPortForwardingR(1234, "122.51.212.235", 7088);
    } catch (JSchException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
