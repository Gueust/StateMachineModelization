package graph.events;

import abstractGraph.conditions.cnf.Literal;
import abstractGraph.events.InternalEvent;

public class VariableChange extends InternalEvent {

  /**
   * The literal that will become true after the execution of the VariableChange
   */
  private Literal l;

  public VariableChange(Literal literal) {
    super(literal.toString());
    l = literal;
  }

}
