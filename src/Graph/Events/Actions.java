package Graph.Events;

import java.util.LinkedList;

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

}
