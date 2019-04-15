package tk.geniusman.event;

/**
 * ChangeColorEvent
 * 
 * @author liuyq
 *
 */
public class ChangeColorEvent extends BaseEvent {

  /**
   * 
   * @param id
   * @param timestamp
   */
  protected ChangeColorEvent(long id, long timestamp) {
    super(id, timestamp);
  }

}
