package tk.geniusman.event;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;


/**
 * EventBus
 * 
 * @author liuyq
 *
 */
public class EventBus {

  private final ConcurrentMap<Class<? extends BaseEvent>, Collection<Consumer<? extends BaseEvent>>> listeners;
  private final ReentrantReadWriteLock readWriteLock;

  public EventBus() {
    this.listeners = new ConcurrentHashMap<>();
    this.readWriteLock = new ReentrantReadWriteLock(false);
  }

  /**
   * addListener
   * 
   * @param eventType
   * @param listener
   */
  public <E extends BaseEvent> void addListener(Class<E> eventType, Consumer<E> listener) {
    Collection<Consumer<? extends BaseEvent>> consumers = this.listeners.get(eventType);
    if (consumers == null) {
      consumers = ConcurrentHashMap.newKeySet();
      Collection<Consumer<? extends BaseEvent>> existing =
          this.listeners.putIfAbsent(eventType, consumers);
      if (existing != null) {
        consumers = existing;
      }
    }

    readWriteLock.writeLock().lock();
    try {
      Consumer<E> safeListener = event -> {
        try {
          listener.accept(event);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      };
      consumers.add(safeListener);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      readWriteLock.writeLock().unlock();
    }
  }

  /**
   * fireEvent
   * 
   * @param event
   */
  @SuppressWarnings("unchecked")
  public <E extends BaseEvent> void fireEvent(E event) {
    readWriteLock.readLock().lock();
    try {
      Collection<Consumer<? extends BaseEvent>> consumers = this.listeners.get(event.getClass());

      if (consumers != null && !consumers.isEmpty()) {
        for (Consumer<? extends BaseEvent> consumer : consumers) {
          ((Consumer<E>) consumer).accept(event);
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      readWriteLock.readLock().unlock();
    }
  }
}
