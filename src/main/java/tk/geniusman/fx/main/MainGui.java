package tk.geniusman.fx.main;

import java.awt.AWTException;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.io.IOException;
import javax.imageio.ImageIO;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tk.geniusman.manager.Manager;

/**
 * MainGui
 * 
 * @author liuyq
 *
 */
@SuppressWarnings("restriction")
public class MainGui extends Application {

    private static final String RESOURCE_FXML = "/ui/MainLayout2.fxml";
    private static final String ICON = "/image/icon1.png";
    private static final String TITLE = "Java Download Tools";

    // private static final String THEME_KEY = "com.sun.javafx.highContrastTheme";
    // private static final String THEME_value = "BLACKONWHITE";
    // private static final String ACCESSIBILITY_THEME = "High Contrast #2";

    private static final String RESOURCE_CSS = "/theme/modena_dark.css";

    private Stage primaryStage;
    private Scene rootScene;
    private TrayIcon trayIcon;

    private volatile double xOffset = 0;
    private volatile double yOffset = 0;

    /**
     * the main entrance of program
     * 
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws InterruptedException {
        // System.out.println("Stop called: try to let background threads complete...");
        Manager.getInstance().terminate();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        Platform.setImplicitExit(false);

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(TITLE);

        setUserAgentStylesheet(STYLESHEET_MODENA);
        // System.setProperty(THEME_KEY, THEME_value);
        // com.sun.javafx.application.PlatformImpl.setAccessibilityTheme(ACCESSIBILITY_THEME);

        try {
            // Load the root layout from the FXML file
            FXMLLoader mainLayoutLoader = new FXMLLoader(MainGui.class.getResource(RESOURCE_FXML));
            Pane rootLayout = mainLayoutLoader.load();
            setDragable(rootLayout);

            rootScene = new Scene(rootLayout);

            rootScene.getStylesheets().add(RESOURCE_CSS);

            primaryStage.setScene(rootScene);
            primaryStage.setResizable(false);

            addToTray();

            primaryStage.getIcons().add(new Image(MainGui.class.getResource(ICON).toString()));
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * set the root layout dragable
     * 
     * @param n
     *            the instance of Node
     */
    private void setDragable(Node n) {
        n.setOnMousePressed((event) -> {
            xOffset = primaryStage.getX() - event.getScreenX();
            yOffset = primaryStage.getY() - event.getScreenY();
        });
        n.setOnMouseDragged((event) -> {
            primaryStage.setX(event.getScreenX() + xOffset);
            primaryStage.setY(event.getScreenY() + yOffset);
        });
    }

    /**
     * set the root layout dragable
     * 
     * @param the
     *            instance of Pane
     */
    private void setDragable(Pane p) {
        setDragable((Node)p);
        for (Node n : p.getChildren()) {
            setDragable(n);
        }
    }

    private void addToTray() {
        // ensure awt is initialized
        java.awt.Toolkit.getDefaultToolkit();

        // make sure system tray is supported
        if (!java.awt.SystemTray.isSupported()) {
            System.out.println("No system tray support!");
            // log.warn("No system tray support!");
        }

        final java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
        try {

            java.awt.Image image = ImageIO.read(MainGui.class.getResource("/image/icon2.png"));
            trayIcon = new TrayIcon(image);
            trayIcon.addActionListener((e) -> Platform.runLater(() -> primaryStage.show()));

            java.awt.MenuItem openItem = new java.awt.MenuItem("Display");
            openItem.addActionListener((e) -> Platform.runLater(() -> show()));

            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            exitItem.addActionListener((e) -> Platform.exit());

            PopupMenu popup = new PopupMenu();
            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);
            trayIcon.setToolTip("Not Connected");
            tray.add(trayIcon);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void show() {
        primaryStage.show();
    }

    public void hide() {
        primaryStage.hide();
    }

    public void setTooltip(String message) {
        if (trayIcon != null) {
            trayIcon.setToolTip(message);
        }
    }

    public void showNotification(String message) {
        trayIcon.displayMessage("File Download Tools", message, java.awt.TrayIcon.MessageType.INFO);
    }
}
