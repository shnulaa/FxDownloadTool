package tk.geniusman.event;

/**
 * ChangeColorEvent
 * 
 * @author liuyq
 *
 */
public class FinishEvent extends BaseEvent {

  /**
   * 
   * @param id
   * @param timestamp
   */
  protected FinishEvent(long id, long timestamp) {
    super(id, timestamp);
  }

}
