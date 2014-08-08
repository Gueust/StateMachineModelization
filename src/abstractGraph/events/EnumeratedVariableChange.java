package abstractGraph.events;

import abstractGraph.conditions.EnumeratedVariable;
import domainSpecificLanguage.engine.DSLSequentialGraphSimulator;

/**
 * Only used by the {@link DSLSequentialGraphSimulator}.
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((variable == null) ? 0 : variable.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    EnumeratedVariableChange other = (EnumeratedVariableChange) obj;
    if (variable == null) {
      if (other.variable != null)
        return false;
    } else if (!variable.equals(other.variable))
      return false;
    return true;
  }
}
