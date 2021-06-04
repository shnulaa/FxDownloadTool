package tk.geniusman.listener;

public interface ChangedListener {

    void change(long current, long totalSize, Thread t);

}
