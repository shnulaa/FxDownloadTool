package tk.geniusman.event;

/**
 * 
 * @author liuyq
 *
 */
public interface Event {

  /**
   * @return Unique event ID
   * @since 1.6
   */
  Object getId();

  /**
   * Relative time of event. Should NOT be used for ordering of events.
   *
   * @return Timestamp
   * @since 1.6
   */
  long getTimestamp();
}
