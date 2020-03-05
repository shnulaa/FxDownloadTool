package tk.geniusman.fx.main;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class Test {

  public static void main(String[] args) throws JSchException {


    JSch.setLogger(new com.jcraft.jsch.Logger() {
      // Path path = Paths.get("d:\\jsch.log");

      @Override
      public boolean isEnabled(int level) {
        return true;
      }

      public void log(int level, String message) {
        // try {
        // StandardOpenOption option =
        // !Files.exists(path) ? StandardOpenOption.CREATE : StandardOpenOption.APPEND;
        // Files.write(path, java.util.Arrays.asList(message), option);
        //
        // System.err.println(message);
        // } catch (IOException e) {
        // System.err.println(message);
        // }

        System.err.println(message);
      }
    });

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
    session.setPortForwardingR(1235, "122.51.212.235", 7089);
    session.setPortForwardingR(1236, "122.51.212.235", 7090);
    session.setPortForwardingR(1237, "122.51.212.235", 7091);
  }


}
