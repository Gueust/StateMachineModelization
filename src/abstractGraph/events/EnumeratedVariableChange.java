package abstractGraph.events;

import abstractGraph.conditions.EnumeratedVariable;

/**
 * Only used by the {@link DSLGraphSimulator}.
 * 
 * @author 9009183R
 * 
 */
public class EnumeratedVariableChange extends InternalEvent {

  private EnumeratedVariable variable;

  public EnumeratedVariableChange(EnumeratedVariable variable) {
    super(variable.getVarname());
    this.variable = variable;
  }

  public EnumeratedVariable getVariable() {
    return variable;
  }
}
