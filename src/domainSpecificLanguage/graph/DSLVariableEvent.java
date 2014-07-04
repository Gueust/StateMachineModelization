package domainSpecificLanguage.graph;

import abstractGraph.events.InternalEvent;

public class DSLVariableEvent extends InternalEvent {

  private DSLVariable variable;

  public DSLVariableEvent(DSLVariable variable) {
    super(variable.getVarname());
    this.variable = variable;
  }

  public DSLVariable getVariable() {
    return variable;
  }
}
