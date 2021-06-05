package tk.geniusman.downloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.lang3.StringUtils;
import tk.geniusman.manager.Manager;
import tk.geniusman.worker.Worker;

/**
 * the interface of the downloader
 * 
 * @author liuyq
 *
 */
public interface Downloader extends Worker {
    /** the file destination folder **/
    static final String FOLDER = "./";
    /** ForkJoinPool pool size **/
    static final int POOL_SIZE = 15;
    /** one thread download connection timeout **/
    static final int THREAD_DOWNLOAD_TIMEOUT = 50000;

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
        if (args == null || isEmpty(args.getDownloadUrl()) || isNull(args.getThreadNumber())
                || isEmpty(args.getSavedPath())) {
            System.err.println("argument error..");
            System.err.println(
                    "usage: java -jar [XX.jar] [downloadUrl] [threadNumber] [savedPath] [savedName]");
            throw new RuntimeException("argument error..");
        }

        if (isEmpty(args.getSavedPath())) {
            args.setSavedPath(FOLDER);
        }
    }

    /**
     * resetFileArgs
     * 
     * @param args
     */
    default void resetFileArgs(final Args args) {
        String downloadURL = args.getDownloadUrl();

        if (isEmpty(args.getFullFileName())) {
            String fileName = downloadURL.substring(downloadURL.lastIndexOf("/") + 1);
            args.setFullFileName(fileName);
        }

        String fullPath = args.getSavedPath() + args.getFullFileName();
        if (!args.getSavedPath().endsWith(File.separator)) {
            args.setFullPath(args.getSavedPath() + File.separator + args.getFullFileName());
        } else {
            args.setFullPath(fullPath);
        }

        if (new File(fullPath).exists()) {
            fullPath += "-" + System.currentTimeMillis();
            args.setFullPath(fullPath);
        }

        final String sFilePath = fullPath + ".s";
        args.setFullTmpPath(new File(sFilePath));
    }

    /**
     * checkRemoteFile
     * 
     * @param args
     * @throws MalformedURLException
     */
    default long checkRemoteFile(Args args) throws Exception {
        URL url = new URL(args.getDownloadUrl());
        args.setUrl(url);

        HttpsURLConnection.setFollowRedirects(true);
        HttpURLConnection.setFollowRedirects(true);
        URLConnection connection = null;
        try {
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,SSLv3");
            connection = (args.getProxy() != null) ? url.openConnection(args.getProxy())
                    : url.openConnection();
            connection.setReadTimeout(THREAD_DOWNLOAD_TIMEOUT);
            connection.setConnectTimeout(THREAD_DOWNLOAD_TIMEOUT);
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Linux; U; Android 2.2; en-gb; GT-P1000 Build/FROYO) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");

            connection.setRequestProperty("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36");


            Map<String, List<String>> crunchifyHeader = connection.getHeaderFields();
            // If URL is getting 301 and 302 redirection HTTP code then get new URL link.
            // This below for loop is totally optional if you are sure that your URL is not getting
            // redirected to anywhere
            try {
                if (crunchifyHeader != null && !crunchifyHeader.isEmpty()) {
                    for (String header : crunchifyHeader.get(null)) {
                        if (header.contains(" 302 ") || header.contains(" 301 ")) {
                            String link = crunchifyHeader.get("Location").get(0);
                            url = new URL(link);
                            connection = (HttpURLConnection) url.openConnection();
                            crunchifyHeader = connection.getHeaderFields();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            long size, code = 0;
            if (args.getDownloadUrl().startsWith("https")) {
                code = ((HttpsURLConnection) connection).getResponseCode();
            } else {
                code = ((HttpURLConnection) connection).getResponseCode();
            }
            m.getLogViewListener().addLog("response code: " + code);
            System.out.println("response code: " + code);

            size = ((HttpURLConnection) connection).getContentLength();
            m.getLogViewListener().addLog("remote file content size:" + size);
            System.out.println("remote file content size:" + size);
            if (size <= 0) {
                m.getLogViewListener().addLog("remote file size is negative(" + size + ")");
                System.err.println("remote file size is negative(" + size + ")");
            }

            if (code != 200) {
                m.getLogViewListener().addLog(
                        "remote file response code is not 200 =" + code + ", skip download..");
                System.err.println("remote file response code is not 200, skip download..");
                throw new RuntimeException("remote file response code is not 200, skip download..");
            }
            args.setFileSize(size);

            // get file name
            String raw = ((HttpURLConnection) connection).getHeaderField("Content-Disposition");
            // raw = "attachment; filename=abc.jpg"
            if (raw != null && raw.indexOf("attachment") != -1) {
                Pattern regex = Pattern.compile("(?<=filename=\").*?(?=\")");
                Matcher regexMatcher = regex.matcher(raw);
                if (regexMatcher.find()) {
                    args.setFullFileName(regexMatcher.group().replaceAll("\"", StringUtils.EMPTY));
                }
            }
            return size;
        } catch (

        Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString();
            m.getLogViewListener().addLog(sStackTrace);
            throw e;
        } finally {
            if (connection instanceof HttpURLConnection) {
                if (connection != null)
                    ((HttpURLConnection) connection).disconnect();
            } else {
                System.err.println("connection is not the instance of HttpURLConnection..");
            }
        }
    }

    /**
     * recovery from the snapshot file
     * 
     * @param sFile the snapshot file
     */
    default void recovery(final File sFile) {
        if (sFile.exists()) {
            Manager re = readObject(sFile);
            m.recovry(re);
            m.recovery = true;
        }
    }

    /**
     * read the Object from file
     * 
     * @param path
     * @return
     */
    @SuppressWarnings("unchecked")
    default <T> T readObject(File path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (T) ois.readObject();
        } catch (Exception e) {
            System.err.println("exception occurred when read object.");
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String fileName = null;
        Pattern regex = Pattern.compile("(?<=filename=\").*?(?=\")");
        Matcher regexMatcher = regex.matcher(
                "attachment; filename=\"7000+%E5%BD%B1%E8%A7%864K%E5%8F%8A%E5%8E%9F%E7%9B%98%E7%A7%8D%E5%AD%90%E8%B5%84%E6%BA%90.zip\"; filename*=UTF-8''7000%20%E5%BD%B1%E8%A7%864K%E5%8F%8A%E5%8E%9F%E7%9B%98%E7%A7%8D%E5%AD%90%E8%B5%84%E6%BA%90.zip");
        if (regexMatcher.find()) {
            fileName = regexMatcher.group();
        }

        System.out.println(fileName);
    }
}
