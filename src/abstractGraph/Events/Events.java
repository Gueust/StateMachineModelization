package abstractGraph.Events;

import java.util.HashMap;
import java.util.Iterator;

import javax.management.openmbean.KeyAlreadyExistsException;

public class Events {

  protected HashMap<String, SingleEvent> events;

  public Events() {
    events = new HashMap<String, SingleEvent>();
  }
  public boolean containsEvent(SingleEvent name_event) {
    return events.get(name_event) != null;
  }

  public void addEvent(SingleEvent event) throws KeyAlreadyExistsException {
    if (containsEvent(event)) {
      throw new KeyAlreadyExistsException();
    } else {
      events.put(event.name, event);
    }
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
}
