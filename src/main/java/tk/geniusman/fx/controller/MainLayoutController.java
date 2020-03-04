package tk.geniusman.fx.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.StageStyle;
import tk.geniusman.downloader.Args;
import tk.geniusman.downloader.Downloader;
import tk.geniusman.downloader.DownloaderFactory;
import tk.geniusman.downloader.Type;
import tk.geniusman.manager.Manager;
import tk.geniusman.manager.UIManager;

/**
 * MainLayoutController
 * 
 * @author liuyq
 *
 */
public class MainLayoutController {

  /**
   * thread number
   */
  private static final int THREAD_NUMBER = 20;

  /**
   * the instance of SimpleDateFormat
   */
  private static final SimpleDateFormat DATA_FORMAT = new SimpleDateFormat("yyyyMMddmmss");

  /**
   * download prefix
   */
  private static final String DOWNLOAD_PREFIX = "download_";

  @FXML
  private TextField address; // download URL address

  @FXML
  private TextField localAddress; // local IP address

  @FXML
  private Button open;

  @FXML
  private Button download;

  @FXML
  private Button pauseOrResume;

  @FXML
  private Pane processPane;

  @FXML
  private Pane root;

  @FXML
  private ProgressBar process;

  @FXML
  private Label speedLab;

  @FXML
  private Label percentLab;

  @FXML
  private Button terminate;

  @FXML
  private AnchorPane taskListPane;

  @FXML
  private ComboBox<Type> type;
  @FXML
  private TextField proxyAddress;
  @FXML
  private TextField proxyPort;

  @FXML
  private TextField remoteSshHost;

  @FXML
  private TextField remoteSshPort;

  @FXML
  private TextField remoteSshPass;

  @FXML
  private TextField remoteForwardPort;

  @FXML
  private TextField localForwardPort;

  @FXML
  private Button portForwardRemote;



  /** Rectangle object array */
  private Rectangle[][] array; // save the Rectangle object to array
  private static final int WIDTH = 100;
  private static final int HEIGHT = 100;
  // private static final int PIXELS = WIDTH * HEIGHT;
  private final Manager m = Manager.getInstance();
  private UIManager uiManager;

  /**
   * MainLayoutController constructor
   */
  public MainLayoutController() {}

  @FXML
  private void initialize() {
    address.setText("http://down.360safe.com/cse/360cse_8.5.0.126.exe");
    localAddress.setText("d:\\tools\\");
    pauseOrResume.setDisable(true);

    this.array = new Rectangle[WIDTH][HEIGHT];
    uiManager = UIManager.newInstance(array, process, speedLab, percentLab, processPane, type);
    uiManager.init();

    // add change Color listener
    m.addListener((current, fileSize, t) -> Platform
        .runLater(() -> uiManager.changeColor(current, fileSize, t)));

    // add change Percent listener
    m.addProcessListener(
        (rate, speed, t) -> Platform.runLater(() -> uiManager.changePercent(rate, speed)));

    // add finish listener
    m.addFinishListener((hasError, message) -> {
      Platform.runLater(() -> {
        download.setDisable(false);
        pauseOrResume.setText("Pause");
        pauseOrResume.setDisable(true);
        showAlert("File Download Tools", message,
            hasError ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
      });
    });
  }

  /**
   * the action for handle the button of Download
   */
  @FXML
  private void handleDownload() throws Exception {
    String addressTxt = address.getText();
    if (addressTxt == null || addressTxt.isEmpty()) {
      showAlert("File Download Tools", "address URL must be specified..", Alert.AlertType.ERROR);
      return;
    }

    String localAddressTxt = localAddress.getText();
    if (localAddressTxt == null || localAddressTxt.isEmpty()) {
      showAlert("File Download Tools", "Local saved Path must be specified..",
          Alert.AlertType.ERROR);
      return;
    }

    Type t = type.getValue();
    if (t == null) {
      showAlert("File Download Tools", "download type must be specified..", Alert.AlertType.ERROR);
      return;
    }

    uiManager.clearColor();
    final Args args = Args.newInstance(addressTxt, THREAD_NUMBER, localAddressTxt,
        DOWNLOAD_PREFIX + DATA_FORMAT.format(new Date()), proxyAddress.getText(),
        proxyPort.getText());
    Downloader downloader = DownloaderFactory.getInstance(t, args);
    Executors.newSingleThreadExecutor().submit(downloader);

    download.setDisable(true);
    pauseOrResume.setDisable(false);
  }

  /**
   * The action for handle the button of Open File
   */
  @FXML
  private void handleOpen() {
    final DirectoryChooser dChooser = new DirectoryChooser();
    dChooser.setTitle("Choose the Saved Path");

    File defaultDirectory = new File("d:\\");
    dChooser.setInitialDirectory(defaultDirectory);
    File file = dChooser.showDialog(open.getScene().getWindow());
    if (file != null) {
      localAddress.setText(file.getAbsolutePath());
    }
  }

  @FXML
  private void handlePauseOrResume() {
    if (m.isPause()) {
      m.resume();
      pauseOrResume.setText("Pause");
    } else {
      m.pause();
      pauseOrResume.setText("Continue");
    }
  }

  @FXML
  private void handleTerminate() {
    Optional<ButtonType> ret =
        showAlert("File Download Tools", "Confirm to terminate..", Alert.AlertType.CONFIRMATION);
    if (ret.get() == ButtonType.OK) {
      Manager.getInstance().terminate();
    }
  }

  /**
   * showAlert
   * 
   * @param title
   * @param message
   * @param type
   */
  private Optional<ButtonType> showAlert(String title, String message, Alert.AlertType type) {
    final Alert a = new Alert(type);
    a.initStyle(StageStyle.UNDECORATED);
    a.setTitle(title);
    a.setHeaderText(type.name());
    a.setResizable(false);
    a.setContentText(message);
    return a.showAndWait();
  }

  @FXML
  private void handlePortForwardRemote() throws Exception {

    showAlert("File Download Tools", "address URL must be specified..", Alert.AlertType.ERROR);
    
    
    

  }
}
