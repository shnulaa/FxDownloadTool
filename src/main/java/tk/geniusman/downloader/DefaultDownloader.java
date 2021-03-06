package tk.geniusman.downloader;

import java.io.File;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;
import tk.geniusman.thread.DefaultThreadPoolExecutorExt;
import tk.geniusman.worker.DefaultDownloadWorker;

/**
 * DefaultDownloader
 * 
 * @author liuyq
 *
 */
public class DefaultDownloader extends AbstractDownloader {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -9071047816647350925L;

    private static final long THRESHOLD = 1024L * 1024L * 500L; // 500M
    private static final long PER = 1024L * 1024L * 10; // 10M

    /**
     * DefaultDownloader
     * 
     * @param args
     */
    public DefaultDownloader(final Args args) {
        super(args);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends ExecutorService> T startMainTask(Args args) throws Exception {
        final long fileSize = args.getFileSize();
        final int threadNum = args.getThreadNumber();
        final URL url = args.getUrl();
        final File dFile = new File(args.getFullPath());
        final Proxy proxy = args.getProxy();

        ExecutorService service = DefaultThreadPoolExecutorExt.getDefaultInstance(threadNum);

        final long per = (fileSize <= THRESHOLD) ? (fileSize / threadNum) : PER;
        final int count = (fileSize <= THRESHOLD) ? threadNum : (int) (fileSize / per);
        IntStream.rangeClosed(1, count).boxed()
                .map((v) -> new DefaultDownloadWorker((v == 1) ? 0 : (v - 1) * per + 1,
                        (v == count && v * per < fileSize ? fileSize : v * per), fileSize, url,
                        dFile, proxy))
                .forEach((o) -> service.submit(o));
        return (T) service;
    }

    /**
     * for test
     * 
     * @param args
     */
    public static void main(String[] args) {
        long fileSize = 1024L * 1024L * 1024L * 1024L;
        int threadNum = 15;


        System.out.println(fileSize / threadNum);
        System.out.println(fileSize % threadNum);
        System.out.println(fileSize <= THRESHOLD);


        if ((fileSize <= THRESHOLD)) {
            final long per = fileSize / threadNum;
            IntStream.rangeClosed(1, threadNum).boxed()
                    .map((v) -> new DefaultDownloadWorker((v == 1) ? 0 : (v - 1) * per + 1,
                            (v == threadNum && v * per < fileSize ? fileSize : v * per), fileSize,
                            null, null, null))
                    .forEach((o) -> System.out.println(o.getStart() + "---" + o.getEnd()));
        } else {
            final long per = PER;
            final int count = (int) (fileSize / per);
            System.out.println(count);
            IntStream.rangeClosed(1, count).boxed()
                    .map((v) -> new DefaultDownloadWorker((v == 1) ? 0 : (v - 1) * per + 1,
                            (v == count && v * per < fileSize ? fileSize : v * per), fileSize, null,
                            null, null))
                    .forEach((o) -> System.out.println(o.getStart() + "---" + o.getEnd()));

        }

        System.out.println(fileSize);



    }

}
