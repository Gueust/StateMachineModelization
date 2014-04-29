package abstractGraph.Events;

import java.util.HashMap;

import javax.management.openmbean.KeyAlreadyExistsException;

public class Events {

  protected HashMap<String, SingleEvent> events;

  public Events() {
    events = new HashMap<String, SingleEvent>();
  }
  public boolean containsEvent(SingleEvent name_event) {
    return events.get(name_event) == null;
  }

  public void addEvent(SingleEvent event) throws KeyAlreadyExistsException {
    if (containsEvent(event)) {
      throw new KeyAlreadyExistsException();
    } else {
      events.put(event.name, event);
    }
  }
}
