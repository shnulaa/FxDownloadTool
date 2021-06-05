package tk.geniusman.fx.controller;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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

@SuppressWarnings("restriction")
public class MainLayoutController {

    /**
     * thread number
     */
    private static final int THREAD_NUMBER = 20;

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
    private Button clearLog;

    @FXML
    private Button stop;

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
    private TextField theadNumber;
    @FXML
    private ListView<String> logListView;

    private final Manager m = Manager.getInstance();
    private UIManager uiManager;

    /**
     * MainLayoutController constructor
     */
    public MainLayoutController() {}

    @FXML
    private void initialize() {
        // address.setText(
        // "https://mirrors.bfsu.edu.cn/apache/zookeeper/zookeeper-3.7.0/apache-zookeeper-3.7.0-bin.tar.gz");
        address.setText("http://42.192.237.45:8888/tmp.100M");
        localAddress.setText("e:\\download\\test\\");
        pauseOrResume.setDisable(true);
        Arrays.asList(Type.values()).stream().forEach((t) -> type.getItems().add(t));
        type.setValue(Type.DEFAULT);

        uiManager = UIManager.newInstance(process, speedLab, percentLab, processPane);
        uiManager.init();
        stop.setDisable(true);

        // init logListView
        ObservableList<String> data = FXCollections.observableArrayList();
        logListView.setItems(data);

        // add change Color listener
        m.addListener((start, end, fileSize, t) -> Platform
                .runLater(() -> uiManager.changeColor(start, end, fileSize, t)));

        // add change Percent listener
        m.addProcessListener(
                (rate, speed, t) -> Platform.runLater(() -> uiManager.changePercent(rate, speed)));

        // add finish listener
        m.addFinishListener((hasError, message) -> {
            Platform.runLater(() -> {
                download.setDisable(false);
                pauseOrResume.setText("Pause");
                pauseOrResume.setDisable(true);
                m.getLogViewListener().addLog("Download finish. message£º" + message);
                showAlert("File Download Tools", message,
                        hasError ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
            });
        });

        m.addLogListener((log) -> {
            Platform.runLater(() -> {
                logListView.getItems().add(log);
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
            showAlert("File Download Tools", "address URL must be specified..",
                    Alert.AlertType.ERROR);
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
            showAlert("File Download Tools", "download type must be specified..",
                    Alert.AlertType.ERROR);
            return;
        }

        int threadNumInt = THREAD_NUMBER;
        if (theadNumber.getText() != null && !theadNumber.getText().isEmpty()) {
            threadNumInt = Integer.valueOf(theadNumber.getText());
        }

        // uiManager.clearColor();
        logListView.getItems().clear();
        uiManager.init();
        Manager.getInstance().clear();
        final Args args = Args.newInstance(addressTxt, threadNumInt, localAddressTxt, null,
                proxyAddress.getText(), proxyPort.getText(), "start");
        Downloader downloader = DownloaderFactory.getInstance(t, args);

        m.getLogViewListener().addLog("Download start.");
        m.singleService.submit(downloader);

        stop.setDisable(false);
        download.setDisable(true);
        pauseOrResume.setDisable(false);
    }

    /**
     * The action for handle the button of Open File
     */
    @FXML
    private void handleOpen() {
        String defaultPath = "c:\\";
        try {
            openDirectory((localAddress.getText() != null && !localAddress.getText().isEmpty())
                    ? localAddress.getText()
                    : defaultPath);
        } catch (Exception e) {
            openDirectory(defaultPath);
        }

    }

    /**
     * openDirectory
     * 
     * @param path
     */
    private void openDirectory(String path) {
        final DirectoryChooser dChooser = new DirectoryChooser();
        dChooser.setTitle("Choose the Saved Path");
        File defaultDirectory = new File(path);
        dChooser.setInitialDirectory(defaultDirectory);
        File file = dChooser.showDialog(open.getScene().getWindow());
        if (file != null) {
            localAddress.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void handleClearLog() throws Exception {
        Platform.runLater(() -> {
            logListView.getItems().clear();
        });
    }

    @FXML
    private void handleStop() throws Exception {
        Optional<ButtonType> ret =
                showAlert("File Download Tools", "Confirm to stop..", Alert.AlertType.CONFIRMATION);
        if (ret.get() == ButtonType.OK) {
            download.setDisable(true);
            stop.setDisable(true);
            pauseOrResume.setDisable(true);
            Platform.runLater(() -> m.stop());;
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
        Optional<ButtonType> ret = showAlert("File Download Tools", "Confirm to terminate..",
                Alert.AlertType.CONFIRMATION);
        if (ret.get() == ButtonType.OK) {
            Platform.runLater(() -> Platform.exit());;
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
}
