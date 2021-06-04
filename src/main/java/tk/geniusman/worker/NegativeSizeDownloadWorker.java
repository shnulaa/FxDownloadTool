package tk.geniusman.worker;

import java.io.File;
import java.net.Proxy;
import java.net.URL;

import tk.geniusman.downloader.Args;

/**
 * NegativeSizeDownloadWorker
 * 
 * @author liuyq
 *
 */
public class NegativeSizeDownloadWorker extends DefaultDownloadWorker {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = 7274659522176478046L;

  /**
   * NegativeSizeDownloadWorker
   * 
   * @param args
   */
  public NegativeSizeDownloadWorker(Args args) {
    this(0, 0, args.getFileSize(), args.getUrl(), new File(args.getFullPath()), args.getProxy());
  }

  /**
   * 
   * @param start
   * @param end
   * @param fileSize
   * @param url
   * @param dFile
   * @param proxy
   */
  public NegativeSizeDownloadWorker(long start, long end, long fileSize, URL url, File dFile,
      Proxy proxy) {
    super(start, end, fileSize, url, dFile, proxy);
  }

}
