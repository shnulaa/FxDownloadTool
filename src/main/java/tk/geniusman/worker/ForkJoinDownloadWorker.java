package tk.geniusman.worker;

import java.io.File;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.ForkJoinTask;

/**
 * <p>
 * ForkJoinDownloadWorker extends DefaultDownloadWorker
 * </p>
 * <p>
 * get stream from the URL with header Range bytes=start-end
 * </p>
 * <p>
 * This worker only download the range with specified start
 * </p>
 * <p>
 * and end position
 * </p>
 * 
 * @author liuyq
 */
public class ForkJoinDownloadWorker extends AbstractDownloadWorker {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = 4514193294297980984L;
  /** the default forkJoin thresholds **/
  private static final long THRESHOLDS = (1024 * 1024 * 1); // 1M

  /**
   * DownloadWorker
   * 
   * @param start
   * @param end
   * @param url
   * @param dFile
   */
  public ForkJoinDownloadWorker(long start, long end, long fileSize, URL url, File dFile,
      Proxy proxy) {
    super(start, end, fileSize, url, dFile, proxy);
  }

  @Override
  public void extractAndExec() {
    if (end - start <= THRESHOLDS) {
      try {
        ForkJoinDownloadWorker reTask = null;
        current
            .set((m.recovery && (reTask = m.get(getKey())) != null) ? reTask.getCurrent() : start);
        m.add(this);
        execute();
      } catch (Exception ex) {
        System.err.println("exception occurred when exec task..");
        ex.printStackTrace();
      } finally {
        // m.remove(this);
      }
    } else {
      long middle = (start + end) / 2;
      ForkJoinTask<?> childTask1 =
          new ForkJoinDownloadWorker(start, middle, fileSize, url, dFile, proxy);
      ForkJoinTask<?> childTask2 =
          new ForkJoinDownloadWorker(middle + 1, end, fileSize, url, dFile, proxy);
      invokeAll(childTask1, childTask2);
    }
  }
}
