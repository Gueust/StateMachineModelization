package Graph.Events;

import java.util.LinkedList;

import abstractGraph.Events.AbstractActions;
import abstractGraph.Events.Event;

public class Actions extends AbstractActions {

  private LinkedList<Event> events;

  public Actions() {
    events = new LinkedList<Event>();
  }

  @Override
  public void addEvent(Event e) {
    events.add(e);
  }

}
