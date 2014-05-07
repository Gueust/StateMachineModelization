package graph.events;

import abstractGraph.events.InternalEvent;

public class VariableChange extends InternalEvent {

  public VariableChange(String name) {
    super(name);
  }

}
