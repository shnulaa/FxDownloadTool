package tk.geniusman.connector;

public enum ConnectorType {

  REMOTE_FORWARD_PORT(RemoteForwardPortConnector.class);

  private Class<? extends Connector> clazz;

  private ConnectorType(Class<? extends Connector> clazz) {
    this.setClazz(clazz);
  }

  public Class<? extends Connector> getClazz() {
    return clazz;
  }

  public void setClazz(Class<? extends Connector> clazz) {
    this.clazz = clazz;
  }

}
