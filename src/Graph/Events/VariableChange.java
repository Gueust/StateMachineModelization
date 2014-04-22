package Graph.Events;

import abstractGraph.Events.InternalEvent;

public class VariableChange extends InternalEvent {

  public VariableChange(String name) {
    super(name);
  }

}
