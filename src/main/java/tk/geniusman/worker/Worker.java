package tk.geniusman.worker;

import java.io.Serializable;

import tk.geniusman.manager.Manager;

/**
 * Worker interface
 * 
 * @author liuyq
 *
 */
public interface Worker extends Runnable, Serializable {
    /** the instance of Manager **/
    static final Manager m = Manager.getInstance();

    @Override
    default public void run() {
        extractAndExec();
    }

    /**
     * extract and execute the task
     */
    void extractAndExec();

}
