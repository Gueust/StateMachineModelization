package domainSpecificLanguage.graph;

import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.events.InternalEvent;

public class DSLVariableEvent extends InternalEvent {

  private EnumeratedVariable variable;

  public DSLVariableEvent(EnumeratedVariable variable) {
    super(variable.getVarname());
    this.variable = variable;
  }

  public EnumeratedVariable getVariable() {
    return variable;
  }
}
