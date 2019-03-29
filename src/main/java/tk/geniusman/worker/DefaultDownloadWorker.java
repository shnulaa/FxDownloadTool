package tk.geniusman.worker;

import java.io.File;
import java.net.Proxy;
import java.net.URL;

/**
 * DefaultDownloadWorker implements Callable <br>
 * get stream from the URL with header Range bytes=start-end <br>
 * This worker only download the range with specified start <br>
 * and end position
 * 
 * @author liuyq
 */
public class DefaultDownloadWorker extends AbstractDownloadWorker {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1124747637487107023L;

    /**
     * DefaultDownloadWorker
     * 
     * @param start
     * @param end
     * @param fileSize
     * @param url
     * @param dFile
     */
    public DefaultDownloadWorker(long start, long end, long fileSize, URL url, File dFile, Proxy proxy) {
        super(start, end, fileSize, url, dFile, proxy);
    }

    @Override
    public void extractAndExec() {
        try {
            execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}