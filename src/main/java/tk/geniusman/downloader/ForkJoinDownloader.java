package tk.geniusman.downloader;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import tk.geniusman.thread.ForkJoinWorkerThreadFactoryExt;
import tk.geniusman.worker.ForkJoinDownloadWorker;

/**
 * ForkJoinDownloader
 * 
 * @author liuyq
 *
 */
public class ForkJoinDownloader extends AbstractDownloader {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = -459576904233613260L;

  /**
   * ForkJoinDownloader
   * 
   * @param args
   */
  public ForkJoinDownloader(final Args args) {
    super(args);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected <T extends ExecutorService> T startMainTask(final Args args) throws Exception {
    ForkJoinPool pool =
        new ForkJoinPool(args.getThreadNumber(), new ForkJoinWorkerThreadFactoryExt(), (t, e) -> {
          final String errorMessage =
              "Unknown Exception occurred while using fork join thread pool, err: "
                  + e.getMessage();
          System.err.println(errorMessage);
        }, false);

    pool.submit((RecursiveAction) new ForkJoinDownloadWorker(0, args.getFileSize(),
        args.getFileSize(), args.getUrl(), new File(args.getFullPath()), args.getProxy()));
    return (T) pool;
  }

}
