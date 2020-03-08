package tk.geniusman.manager;

import java.io.Serializable;

import com.jcraft.jsch.Session;

/**
 * Singleton instance.
 * 
 * @author liuyq
 * 
 */
public class ConnectorManager implements Serializable {
  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = 3879351808960296777L;

  private Session session;

  static class SingletonHolder {
    public static final ConnectorManager connectorManager = new ConnectorManager();
  }

  public static ConnectorManager getInstance() {
    return SingletonHolder.connectorManager;
  }

  public Session getSession() {
    return session;
  }

  public void setSession(Session session) {
    this.session = session;
  }


}
