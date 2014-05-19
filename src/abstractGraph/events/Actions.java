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

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Actions other = (Actions) obj;
    if (events == null) {
      if (other.events != null) {
        return false;
      }
    }

    if (this.events.size() != other.events.size()) {
      return false;
    }

    for (int i = 0; i < events.size(); i++) {
      if (!events.get(i).toString().equals(other.events.get(i).toString())) {
        return false;
      }
    }
    return true;
  }
}