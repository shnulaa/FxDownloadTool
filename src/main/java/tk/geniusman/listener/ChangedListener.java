package tk.geniusman.listener;

public interface ChangedListener {

    void change(long start, long end, long totalSize, Thread t);

}
