package abstractGraph.events;

import java.util.Iterator;
import java.util.LinkedList;

public class Actions {

  private LinkedList<SingleEvent> events;

  public Actions() {
    events = new LinkedList<SingleEvent>();
  }

  public void add(SingleEvent e) {
    events.add(e);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Iterator<SingleEvent> iterator = events.iterator();
    while (iterator.hasNext()) {
      SingleEvent single_event = iterator.next();
      sb.append(single_event.toString() + ";");
    }
    return sb.toString();
  }
}