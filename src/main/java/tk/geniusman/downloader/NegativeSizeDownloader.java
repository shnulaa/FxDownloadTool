package tk.geniusman.downloader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tk.geniusman.worker.NegativeSizeDownloadWorker;

/**
 * NegativeSizeDownloader
 * 
 * @author liuyq
 *
 */
public class NegativeSizeDownloader extends AbstractDownloader {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 9022310119379908574L;

    /**
     * NegativeSizeDownloader
     * 
     * @param args
     */
    public NegativeSizeDownloader(Args args) {
        super(args);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends ExecutorService> T startMainTask(Args args) throws Exception {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new NegativeSizeDownloadWorker(args));
        return (T) service;
    }

}
