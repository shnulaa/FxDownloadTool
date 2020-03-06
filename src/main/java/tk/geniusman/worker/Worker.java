package tk.geniusman.worker;

import java.io.Serializable;

import tk.geniusman.manager.Manager;

/**
 * Worker
 * 
 * @author liuyq
 *
 */
public interface Worker extends Runnable, Serializable {
  /** the instance of Manager **/
  static final Manager m = Manager.getInstance();
  // static final UIManager um = UIManager.getInstance();


  @Override
  default public void run() {
    extractAndExec();
  }

  /**
   * extract and execute the task
   */
  void extractAndExec();

}
