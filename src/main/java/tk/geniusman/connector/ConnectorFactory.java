package tk.geniusman.connector;

import java.lang.reflect.Constructor;

import tk.geniusman.downloader.Args;
import tk.geniusman.worker.Worker;

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
  public static Worker getInstance(ConnectorType type, Args args) throws Exception {
    Class<? extends Worker> clazz = type.getClazz();
    Constructor<? extends Worker> c = clazz.getDeclaredConstructor(Args.class);
    Worker downloader = c.newInstance(args);
    return downloader;
  }
}
