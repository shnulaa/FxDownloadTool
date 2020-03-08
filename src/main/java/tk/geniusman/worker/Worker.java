package tk.geniusman.worker;

import java.io.Serializable;

import tk.geniusman.manager.ConnectorManager;
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

  static final ConnectorManager connectorManager = ConnectorManager.getInstance();



  @Override
  default public void run() {
    extractAndExec();
  }

  /**
   * extract and execute the task
   */
  void extractAndExec();

}
