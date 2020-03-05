package tk.geniusman.connector;

import tk.geniusman.downloader.Args;
import tk.geniusman.worker.Worker;

/**
 * the interface of the Connector
 * 
 * @author liuyq
 *
 */
public interface Connector extends Worker {
  /** the file destination folder **/
  static final String FOLDER = "./";
  /** ForkJoinPool pool size **/
  static final int POOL_SIZE = 15;

  /**
   * start the downloader with specified argument
   * 
   * @param args the argument
   */
  void start(Args args) throws Exception;

  /**
   * terminate the downloader
   */
  void terminate();

  default boolean isEmpty(String str) {
    return (str == null || str.isEmpty());
  }

  default boolean isNull(Object object) {
    return (object == null);
  }

  default boolean isNotEmpty(String str) {
    return (!isEmpty(str));
  }

  /**
   * checkAndSetArgs
   * 
   * @param args
   * @return
   */
  default void checkAndSetArgs(final Args args) {
    
    
  }

}
