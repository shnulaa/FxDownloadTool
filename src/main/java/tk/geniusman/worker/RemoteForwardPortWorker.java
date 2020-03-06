package tk.geniusman.worker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.jcraft.jsch.JSch;
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
    // TODO Auto-generated method stub

    JSch.setLogger(new com.jcraft.jsch.Logger() {
      Path path = Paths.get("d:\\jsch.log");

      @Override
      public boolean isEnabled(int level) {
        return true;
      }

      public void log(int level, String message) {
        try {
          StandardOpenOption option =
              !Files.exists(path) ? StandardOpenOption.CREATE : StandardOpenOption.APPEND;
          Files.write(path, java.util.Arrays.asList(message), option);
        } catch (IOException e) {
          System.err.println(message);
        }
      }
    });

    try {
      JSch jsch = new JSch();
      Session session = jsch.getSession(args.getRemoteSshUser(), args.getRemoteSshHost(),
          Integer.valueOf(args.getRemoteSshPort()));
      session.setPassword(args.getRemoteSshPass());

      java.util.Properties config = new java.util.Properties();
      config.put("StrictHostKeyChecking", "no");
      config.put("Compression", "yes");
      config.put("ConnectionAttempts", "2");

      session.setConfig(config);
      session.connect();
      // int assignedPort = session.setPortForwardingL(1234, "122.51.212.235", 22);
      // System.out.println(assignedPort);

      // session.setPortForwardingR(Integer.valueOf(args.getRemoteForwardPort()),
      // args.getRemoteSshHost(), Integer.valueOf(args.getLocalListningPort()));



    } catch (JSchException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
