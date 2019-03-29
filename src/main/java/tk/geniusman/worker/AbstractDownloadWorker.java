package tk.geniusman.worker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

/**
 * AbstractDownloadWorker
 * 
 * @author liuyq
 *
 */
public abstract class AbstractDownloadWorker extends RecursiveAction implements Worker {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = -5856787129136734965L;
  /** one thread download connection timeout **/
  protected static final int THREAD_DOWNLOAD_TIMEOUT = 5000;
  /** one thread download max retry count **/
  protected static final int THREAD_MAX_RETRY_COUNT = 10;

  protected String key; // (start - end)
  protected long start; // the start position to download
  protected long end; // the end position to download
  protected URL url; // the URL will be download
  protected File dFile; // the desc file to be saved
  protected AtomicLong current; // the current position
  protected long fileSize;// total file size
  protected Proxy proxy; // socket proxy

  /**
   * AbstractDownloadWorker
   * 
   * @param start
   * @param end
   * @param url
   * @param dFile
   */
  public AbstractDownloadWorker(long start, long end, long fileSize, URL url, File dFile,
      Proxy proxy) {
    this.start = start;
    this.current = new AtomicLong(start);
    this.end = end;
    this.url = url;
    this.dFile = dFile;
    this.key = (String.valueOf(this.start) + "-" + String.valueOf(this.end));
    this.fileSize = fileSize;
    this.proxy = proxy;
  }

  @Override
  public void compute() {
    extractAndExec();
  }

  /**
   * the main flow of download execution
   */
  protected void execute() throws Exception {
    int retryCount = 0;
    final Thread t = Thread.currentThread();
    while (retryCount++ < THREAD_MAX_RETRY_COUNT) {
      HttpURLConnection con = null;
      HttpURLConnection.setFollowRedirects(true);
      try {
        con = (HttpURLConnection) ((proxy != null)
            ? url.openConnection(proxy)
            : url.openConnection());
        con.setReadTimeout(THREAD_DOWNLOAD_TIMEOUT);
        con.setConnectTimeout(THREAD_DOWNLOAD_TIMEOUT);

        if (getCurrent() > end) {
          return;
        }

        if (getEnd() > 0) {
          con.setRequestProperty("Range", "bytes=" + getCurrent() + "-" + end);
        }

        try (BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
            RandomAccessFile file = new RandomAccessFile(dFile, "rw");) {
          file.seek(getCurrent());
          final byte[] bytes = new byte[1024];

          int readed = 0;
          while (!t.isInterrupted() && !m.terminate && (readed = bis.read(bytes)) != -1) {

            while (m.isPause()) {
              LockSupport.park();
            }

            // duplicate check due to terminate this thread as soon
            // as possible
            if (t.isInterrupted() || m.terminate) {
              break;
            }

            file.write(bytes, 0, readed);
            current.getAndAdd(readed);
            m.getListener().change(current.get(), fileSize, t);
            m.alreadyRead.getAndAdd(readed);
          }
          break;
        } catch (Exception e) {
          // System.err.println("exception occurred while
          // download..");
          // e.printStackTrace();
          Thread.sleep(1000);
          continue; // write exception or read timeout, retry
        }
      } catch (Exception ex) {
        // System.err.println("exception occurred while download..");
        // ex.printStackTrace();
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          // System.err.println("InterruptedException occurred while
          // download..");
          // e.printStackTrace();
        }
        continue;
      } finally {
        if (con != null) {
          con.disconnect();
        }
      }
    }
    return;
  }

  public long getStart() {
    return start;
  }

  public long getEnd() {
    return end;
  }

  public long getCurrent() {
    return current.get();
  }

  public String getKey() {
    return key;
  }

  @Override
  public String toString() {
    return String.format("start: %s, end: %s, current: %s", start, end, current);
  }

}
