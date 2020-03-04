package tk.geniusman.connector;

import java.util.concurrent.ExecutorService;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import tk.geniusman.downloader.Args;

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
    // TODO Auto-generated method stub


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


    return null;
  }

}
