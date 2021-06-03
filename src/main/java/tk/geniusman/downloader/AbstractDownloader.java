package tk.geniusman.downloader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import tk.geniusman.worker.SnapshotWorker;

/**
 * AbstractDownloader
 * 
 * @author liuyq
 *
 */
public abstract class AbstractDownloader implements Downloader {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2347547655193496558L;
    /** the instance of Args **/
    private final Args args;

    /**
     * AbstractDownloader
     * 
     * @param args
     */
    public AbstractDownloader(final Args args) {
        this.args = args;
    }

    @Override
    public void extractAndExec() {
        try {
            start(this.args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(final Args args) throws Exception {
        long start = System.currentTimeMillis();
        try {
            // check the argument
            checkAndSetArgs(args);

            // check the remote file is exist
            checkRemoteFile(args);

            ScheduledExecutorService s = null;
            ExecutorService es = null;
            try {
                recovery(args.getFullTmpPath());
                m.setSize(args.getFileSize());
                es = startMainTask(args);
                s = startScheduledTask(args);
            } catch (Exception e) {
                throw e;
            } finally {
                if (es != null) {
                    es.shutdown();
                }
                es.awaitTermination(30, TimeUnit.HOURS);

                if (s != null) {
                    s.shutdown();
                }
                if (args.getFullTmpPath().exists()) {
                    args.getFullTmpPath().delete();
                }
            }
            // System.out.print(ProgressBar.showBarByPoint(100, 100, 70,
            // m.getPerSecondSpeed(), true));
            // System.out.flush();
            long end = System.currentTimeMillis();
            System.out.println("cost time: " + (end - start) / 1000 + "s");
            m.getPlistener().change(1, 0, Thread.currentThread());
            m.getFlistener().finish(false, "Download Complete Successfully..");
        } catch (Exception e) {
            e.printStackTrace();
            m.getFlistener().finish(false, "Download Complete With Exception..");
        }
    }

    @Override
    public void terminate() {
        m.terminate();
    }

    /**
     * startMainTask
     * 
     * @return
     */
    protected abstract <T extends ExecutorService> T startMainTask(final Args args)
            throws Exception;

    /**
     * startScheduledTask
     * 
     * @param size
     * @param sFile
     */
    // @SuppressWarnings("unused")
    private ScheduledExecutorService startScheduledTask(final Args args) {
        ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();
        s.scheduleAtFixedRate(new SnapshotWorker(args.getFullTmpPath(), args.getFileSize()), 0, 1,
                TimeUnit.SECONDS);
        return s;
    }

}
