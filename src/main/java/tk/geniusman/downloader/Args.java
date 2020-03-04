package tk.geniusman.downloader;

import java.io.File;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * 
 * @author liuyq
 *
 */
public class Args implements Serializable {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = -2856157124256094457L;


  public String getRemoteSshHost() {
    return remoteSshHost;
  }

  public void setRemoteSshHost(String remoteSshHost) {
    this.remoteSshHost = remoteSshHost;
  }

  public String getRemoteSshPort() {
    return remoteSshPort;
  }

  public void setRemoteSshPort(String remoteSshPort) {
    this.remoteSshPort = remoteSshPort;
  }

  public String getRemoteSshPass() {
    return remoteSshPass;
  }

  public void setRemoteSshPass(String remoteSshPass) {
    this.remoteSshPass = remoteSshPass;
  }

  public String getRemoteForwardPort() {
    return remoteForwardPort;
  }

  public void setRemoteForwardPort(String remoteForwardPort) {
    this.remoteForwardPort = remoteForwardPort;
  }

  public String getLocalListningPort() {
    return localListningPort;
  }

  public void setLocalListningPort(String localListningPort) {
    this.localListningPort = localListningPort;
  }


  public String getDownloadUrl() {
    return downloadUrl;
  }

  public void setDownloadUrl(String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  public Integer getThreadNumber() {
    return threadNumber;
  }

  public void setThreadNumber(Integer threadNumber) {
    this.threadNumber = threadNumber;
  }

  public String getSavedPath() {
    return savedPath;
  }

  public void setSavedPath(String savedPath) {
    this.savedPath = savedPath;
  }

  public String getFullFileName() {
    return fullFileName;
  }

  public void setFullFileName(String fullFileName) {
    this.fullFileName = fullFileName;
  }

  public Long getFileSize() {
    return fileSize;
  }

  public void setFileSize(Long fileSize) {
    this.fileSize = fileSize;
  }

  public String getFullPath() {
    return fullPath;
  }

  public void setFullPath(String fullPath) {
    this.fullPath = fullPath;
  }

  public File getFullTmpPath() {
    return fullTmpPath;
  }

  public void setFullTmpPath(File fullTmpPath) {
    this.fullTmpPath = fullTmpPath;
  }

  public URL getUrl() {
    return url;
  }

  public void setUrl(URL url) {
    this.url = url;
  }

  public String getProxyAddress() {
    return proxyAddress;
  }

  public void setProxyAddress(String proxyAddress) {
    this.proxyAddress = proxyAddress;
  }

  public String getProxyPort() {
    return proxyPort;
  }

  public void setProxyPort(String proxyPort) {
    this.proxyPort = proxyPort;
  }

  public Proxy getProxy() {
    if (proxyAddress != null && !proxyAddress.isEmpty() && proxyPort != null
        && !proxyPort.isEmpty()) {
      return new Proxy(Proxy.Type.SOCKS,
          new InetSocketAddress(proxyAddress, Integer.valueOf(proxyPort)));
    }
    return null;
  }

  private String downloadUrl;
  private Integer threadNumber;
  private String savedPath;
  private String fullFileName;
  private Long fileSize;
  private String fullPath;
  private File fullTmpPath;
  private URL url;
  private String proxyAddress;
  private String proxyPort;

  private String remoteSshHost;
  private String remoteSshPort;
  private String remoteSshUser;
  private String remoteSshPass;
  private String remoteForwardPort;
  private String localListningPort;

  /**
   * 
   * @param remoteSshHost
   * @param remoteSshPort
   * @param remoteSshUser
   * @param remoteSshPass
   * @param remoteForwardPort
   * @param localListningPort
   */
  private Args(String remoteSshHost, String remoteSshPort, String remoteSshUser,
      String remoteSshPass, String remoteForwardPort, String localListningPort) {
    this.remoteSshHost = remoteSshHost;
    this.remoteSshPort = remoteSshPort;
    this.remoteSshUser = remoteSshUser;
    this.remoteSshPass = remoteSshPass;
    this.remoteForwardPort = remoteForwardPort;
    this.localListningPort = localListningPort;
  }


  /**
   * Args constructor
   * 
   * @param downloadUrl
   * @param threadNumber
   * @param savedPath
   * @param fullFileName
   */
  private Args(String downloadUrl, Integer threadNumber, String savedPath, String fullFileName,
      String proxyAddress, String proxyPort) {
    this.downloadUrl = downloadUrl;
    this.threadNumber = threadNumber;
    this.savedPath = savedPath;
    this.fullFileName = fullFileName;
    this.proxyAddress = proxyAddress;
    this.proxyPort = proxyPort;
  }

  /**
   * newInstance
   * 
   * @param downloadUrl
   * @param threadNumber
   * @param savedPath
   * @param fullFileName
   * @param proxyAddress
   * @param proxyPort
   * @return
   */
  public static Args newInstance(String downloadUrl, Integer threadNumber, String savedPath,
      String fullFileName, String proxyAddress, String proxyPort) {
    return new Args(downloadUrl, threadNumber, savedPath, fullFileName, proxyAddress, proxyPort);
  }

  /**
   * newInstance
   * 
   * @param remoteSshHost
   * @param remoteSshPort
   * @param remoteSshUser
   * @param remoteSshPass
   * @param remoteForwardPort
   * @param localListningPort
   * @return
   */
  public static Args newInstance(String remoteSshHost, String remoteSshPort, String remoteSshUser,
      String remoteSshPass, String remoteForwardPort, String localListningPort) {
    return new Args(remoteSshHost, remoteSshPort, remoteSshUser, remoteSshPass, remoteForwardPort,
        localListningPort);
  }

  public String getRemoteSshUser() {
    return remoteSshUser;
  }

  public void setRemoteSshUser(String remoteSshUser) {
    this.remoteSshUser = remoteSshUser;
  }

}
