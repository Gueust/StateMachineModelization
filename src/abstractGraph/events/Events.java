package abstractGraph.events;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.management.openmbean.KeyAlreadyExistsException;

import abstractGraph.conditions.CustomToString;

public class Events implements Iterable<SingleEvent> {

  /** An empty unique instance of Events */
  public static final Events NONE = new Events();

  protected Set<SingleEvent> events;

  public Events() {
    events = new LinkedHashSet<>();
  }

  public boolean containsEvent(SingleEvent event) {
    return event != null && events.contains(event);
  }

  /**
   * Check for a common event with the given parameter.
   * 
   * @param events_2
   *          An other list of events.
   * @return True if events_2 has a common element with `this`.
   */
  public boolean notEmptyIntersection(Events events_2) {
    for (SingleEvent event : events) {
      if (events_2.events.contains(event)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Add the given event if it does not exist. An exception is raised if it
   * exists already.
   * 
   * @param event
   *          The event to add into the list.
   * @throws KeyAlreadyExistsException
   *           If the event already exists.
   */
  public void addEvent(SingleEvent event) throws KeyAlreadyExistsException {
    if (containsEvent(event)) {
      throw new KeyAlreadyExistsException();
    } else {
      events.add(event);
    }
  }

  @Override
  public String toString() {
    return toString(null);
  }

  public String toString(CustomToString customizer) {
    StringBuilder sb = new StringBuilder();
    boolean is_first = true;
    for (SingleEvent single_event : events) {
      if (is_first) {
        is_first = false;
      } else {
        sb.append(" + ");
      }
      sb.append(single_event.toString(customizer));
    }
    return sb.toString();
  }

  @Override
  public Iterator<SingleEvent> iterator() {
    return events.iterator();
  }

  public Set<SingleEvent> getCollection() {
    return events;
  }
}
