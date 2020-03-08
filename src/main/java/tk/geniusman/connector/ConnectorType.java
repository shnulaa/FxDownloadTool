package tk.geniusman.connector;

import tk.geniusman.worker.Worker;

public enum ConnectorType {
  SESSION_CONNECT(SessionConnectConnector.class), REMOTE_FORWARD_PORT(
      RemoteForwardPortConnector.class);

  private Class<? extends Worker> clazz;

  private ConnectorType(Class<? extends Worker> clazz) {
    this.setClazz(clazz);
  }

  public Class<? extends Worker> getClazz() {
    return clazz;
  }

  public void setClazz(Class<? extends Worker> clazz) {
    this.clazz = clazz;
  }

}
