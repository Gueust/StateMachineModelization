package abstractGraph.events;

import java.util.HashMap;
import java.util.Iterator;

import javax.management.openmbean.KeyAlreadyExistsException;

public class Events implements Iterable<SingleEvent> {

  protected HashMap<String, SingleEvent> events;

  public Events() {
    events = new HashMap<String, SingleEvent>();
  }

  public boolean containsEvent(SingleEvent event) {
    return events.get(event.name) != null;
  }

  /**
   * Check for a common event with the given parameter.
   * 
   * @param events_2
   *          An other list of events.
   * @return True if events_2 has a common element with `this`.
   */
  public boolean notEmptyIntersection(Events events_2) {
    for (String event_name : events.keySet()) {
      if (events_2.events.get(event_name) != null) {
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
      events.put(event.name, event);
    }
  }

  public Iterator<SingleEvent> singleEvent() {
    return events.values().iterator();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Iterator<SingleEvent> iterator = events.values().iterator();
    while (iterator.hasNext()) {
      SingleEvent single_event = iterator.next();
      sb.append(single_event.toString() + ";");
    }
    return sb.toString();
  }

  @Override
  public Iterator<SingleEvent> iterator() {
    return events.values().iterator();
  }
}
