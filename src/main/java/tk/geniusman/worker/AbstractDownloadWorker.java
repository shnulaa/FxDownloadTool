package tk.geniusman.worker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
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
    protected static int THREAD_DOWNLOAD_TIMEOUT = 10000;
    /** one thread download connection timeout **/
    protected static int THREAD_DOWNLOAD_TIMEOUT_LARGE = 30000;
    /** one thread download max retry count **/
    protected static final int THREAD_MAX_RETRY_COUNT = 10;

    private static final long PER = 1024L * 1024L * 10; // 10M

    protected String key; // (start - end)
    protected long start; // the start position to download
    protected long end; // the end position to download
    protected URL url; // the URL will be download
    protected File dFile; // the desc file to be saved
    protected AtomicLong current; // the current position
    protected long fileSize;// total file size
    protected transient Proxy proxy; // socket proxy

    /**
     * AbstractDownloadWorker
     * 
     * @param start
     * @param end
     * @param url
     * @param dFile
     */
    public AbstractDownloadWorker(long start, long end, long fileSize, URL url, File dFile, Proxy proxy) {
        this.start = start;
        this.current = new AtomicLong(start);
        this.end = end;
        this.url = url;
        this.dFile = dFile;
        this.key = (String.valueOf(this.start) + "-" + String.valueOf(this.end));
        this.fileSize = fileSize;
        this.proxy = proxy;
        THREAD_DOWNLOAD_TIMEOUT =
            (this.end - this.start >= PER) ? THREAD_DOWNLOAD_TIMEOUT_LARGE : THREAD_DOWNLOAD_TIMEOUT;

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
                con = getUrlConnection();

                if (getCurrent() > end) {
                    return;
                }

                if (getEnd() > 0) {
                    con.setRequestProperty("Range", "bytes=" + getCurrent() + "-" + end);
                }

                String logString =
                    String.format("TN:%s,Range %s - %s ", Thread.currentThread().getName(), getCurrent(), end);
                m.getLogViewListener().addLog(logString);
                try (BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
                    RandomAccessFile file = new RandomAccessFile(dFile, "rw");) {
                    file.seek(getCurrent());
                    final byte[] bytes = new byte[1024]; // 1024K

                    int readed = 0;
                    while (!t.isInterrupted() && !m.terminate && (readed = bis.read(bytes)) != -1) {

                        while (m.isPause()) {
                            m.getLogViewListener()
                                .addLog(String.format("TN:%s pause£¡ ", Thread.currentThread().getName()));
                            LockSupport.park();
                        }

                        // duplicate check due to terminate this thread as soon
                        // as possible
                        if (t.isInterrupted() || m.terminate) {
                            break;
                        }

                        file.write(bytes, 0, readed);
                        current.getAndAdd(readed);
                        m.getListener().change(current.get(), end, fileSize, t);
                        m.alreadyRead.getAndAdd(readed);
                    }
                    break;
                } catch (Exception e) {
                    // System.err.println("exception occurred while
                    // download..");
                    // e.printStackTrace();
                    m.getLogViewListener().addLog(String.format("TN:%s RERTRY:%s TO:%s EX:%s. ",
                        Thread.currentThread().getName(), retryCount, THREAD_DOWNLOAD_TIMEOUT, e.getMessage()));

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

    /**
     * getUrlConnection
     * 
     * @return
     * @throws IOException
     */
    private HttpURLConnection getUrlConnection() throws IOException {
        HttpURLConnection con;
        con = (HttpURLConnection)((proxy != null) ? url.openConnection(proxy) : url.openConnection());
        con.setReadTimeout(THREAD_DOWNLOAD_TIMEOUT);
        con.setConnectTimeout(THREAD_DOWNLOAD_TIMEOUT);

        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,SSLv3");
        con.setRequestProperty("Accept-Encoding", "identity");
        con.setRequestProperty("User-Agent",
            "Mozilla/5.0 (Linux; U; Android 2.2; en-gb; GT-P1000 Build/FROYO) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

        con.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
        con.setRequestProperty("User-Agent",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36");
        return con;
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
