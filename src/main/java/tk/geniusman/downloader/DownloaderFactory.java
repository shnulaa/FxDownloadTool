package tk.geniusman.downloader;

import java.lang.reflect.Constructor;

/**
 * DownloaderFactory
 * 
 * @author liuyq
 *
 */
public class DownloaderFactory {

  /**
   * getInstance
   * 
   * @param type
   * @return the instance of downloader
   */
  public static Downloader getInstance(Type type, Args args) throws Exception {
    Class<? extends Downloader> clazz = type.getClazz();
    Constructor<? extends Downloader> c = clazz.getDeclaredConstructor(Args.class);
    Downloader downloader = c.newInstance(args);
    return downloader;
  }
}
