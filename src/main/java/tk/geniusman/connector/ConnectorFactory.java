package tk.geniusman.connector;

import java.lang.reflect.Constructor;

import tk.geniusman.downloader.Args;

/**
 * DownloaderFactory
 * 
 * @author liuyq
 *
 */
public class ConnectorFactory {

  /**
   * getInstance
   * 
   * @param type
   * @return the instance of downloader
   */
  public static Connector getInstance(ConnectorType type, Args args) throws Exception {
    Class<? extends Connector> clazz = type.getClazz();
    Constructor<? extends Connector> c = clazz.getDeclaredConstructor(Args.class);
    Connector downloader = c.newInstance(args);
    return downloader;
  }
}
