package tk.geniusman.manager;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import tk.geniusman.downloader.Type;

/**
 * UIManager
 * 
 * @author liuyq
 *
 */
public class UIManager {

  private static final Map<String, Color> THREAD_COLOR = new ConcurrentHashMap<>();
  private static final int WIDTH = 100;
  private static final int HEIGHT = 100;
  private static final int PIXELS = WIDTH * HEIGHT;
  private Rectangle[][] array;
  private ProgressBar process;
  private Label speedLab;
  private Label percentLab;
  private Pane processPane;
  private ComboBox<Type> type;

  /**
   * 
   */
  public static void terminal() {
    Platform.exit();
  }

  /**
   * UIManager
   * 
   * @param array
   */
  private UIManager(final Rectangle[][] array, final ProgressBar process, final Label speedLab,
      final Label percentLab, Pane processPane, ComboBox<Type> type) {
    this.array = array;
    this.process = process;
    this.speedLab = speedLab;
    this.percentLab = percentLab;
    this.processPane = processPane;
    this.type = type;
  }

  /**
   * changePercent
   * 
   * @param rate
   * @param speed
   */
  public void changePercent(double rate, long speed) {
    process.progressProperty().set(rate);
    percentLab.setText((int) (rate * 100) + "%");
    speedLab.setText(String.valueOf(speed) + "KB/S");
  }

  /**
   * init the array to processPane
   * 
   * @param array
   * @param processPane
   */
  public void init() {
    for (int j = 0; j < WIDTH; j++) {
      for (int i = 0; i < HEIGHT; i++) {
        final Rectangle r = new Rectangle();
        r.setX(i * 4);
        r.setY(j * 2);
        r.setWidth(4);
        r.setHeight(2);
        r.setFill(Color.AZURE);
        array[j][i] = r;
        processPane.getChildren().add(r);
      }
    }

    final Field[] fields = Color.class.getDeclaredFields();
    final AtomicInteger index = new AtomicInteger(1);
    Supplier<Stream<Field>> supplier = () -> {
      return Arrays.asList(fields).parallelStream()
          .filter((f) -> f.getType().isAssignableFrom(Color.class));
    };
    supplier.get().skip(new Random().nextInt((int) supplier.get().count() - 15)).forEach((f) -> {
      try {
        f.setAccessible(true);
        THREAD_COLOR.put("ForkJoinThread-" + index.getAndIncrement(), (Color) f.get(null));
      } catch (Exception e) {} finally {
        f.setAccessible(false);
      }
    });

    Arrays.asList(Type.values()).stream().forEach((t) -> type.getItems().add(t));
  }

  /**
   * changeColor
   * 
   * @param current
   * @param totol
   */
  public void changeColor(final long current, final long total, final Thread t) {
    if (total <= 0) {
      System.out.println("total is negative..");
      return;
    }

    int percent = (int) (current * PIXELS / total);

    int x = (int) percent / WIDTH;
    int y = (int) percent % HEIGHT;
    if (x >= 100 || x >= 100) {
      return;
    }

    setColor(current, x, y, t);
  }

  /**
   * clear Color
   */
  public void clearColor() {
    if (array == null) {
      return;
    }
    for (int j = 0; j < WIDTH; j++) {
      for (int i = 0; i < HEIGHT; i++) {
        final Rectangle r = array[j][i];
        if (r != null) {
          r.setFill(Color.AZURE);
        }
      }
    }
  }

  /**
   * set background color
   * 
   * @param x
   * @param y
   */
  private void setColor(final long current, int x, int y, Thread t) {
    final Rectangle r = array[x][y];
    if (r == null) {
      return;
    }
    synchronized (r) {
      final Color color =
          THREAD_COLOR.containsKey(t.getName()) ? THREAD_COLOR.get(t.getName()) : Color.BLUE;
      if (r.getFill() != color) {
        array[x][y].setFill(color);
      }
    }
  }

  /**
   * newInstance
   * 
   * @param array
   * @return new instance of RectangleManager
   */
  public static UIManager newInstance(final Rectangle[][] array, final ProgressBar process,
      final Label speedLab, final Label percentLab, final Pane processPane,
      final ComboBox<Type> type) {
    return new UIManager(array, process, speedLab, percentLab, processPane, type);
  }



  /**
   * 
   * @param args
   */
  public static void main(String[] args) {
    final Field[] fields = Color.class.getDeclaredFields();
    final AtomicInteger index = new AtomicInteger(1);
    Arrays.asList(fields).parallelStream().filter((f) -> f.getType().isAssignableFrom(Color.class))
        .forEach((f) -> {
          try {
            f.setAccessible(true);
            THREAD_COLOR.put("ForkJoinThread-" + index.getAndIncrement(), (Color) f.get(null));
          } catch (Exception e) {
            // e.printStackTrace();
          } finally {
            f.setAccessible(false);
          }
        });

    THREAD_COLOR.forEach((name, color) -> {
      System.out.println("name:" + name + ", color:" + color.toString());
    });

    // System.out.println(String.format("PIXELS:%s, total:%s..", PIXELS,
    // total));
    // System.out.println(String.format("percent:%s, x:%s, y:%s", percent,
    // x, y));

    //
    // if (total < PIXELS) {
    // for (int j = 0; j < (percent / x); j++) { // row
    // for (int i = 0; i < x; i++) { // height
    // if (j >= 100 || i >= 100) {
    // continue;
    // }
    // setColor(current, j, i, t);
    // }
    // }
    // return;
    // }

  }

}
