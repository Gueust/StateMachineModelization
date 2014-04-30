package Graph.Events;

import java.util.Iterator;
import java.util.LinkedList;

import Graph.StateMachine;
import abstractGraph.Events.AbstractActions;
import abstractGraph.Events.SingleEvent;

public class Actions extends AbstractActions {

  private LinkedList<SingleEvent> events;

  public Actions() {
    events = new LinkedList<SingleEvent>();
  }

  @Override
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
