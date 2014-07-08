package abstractGraph.events;

import abstractGraph.conditions.BooleanVariable;
import abstractGraph.conditions.EnumeratedVariable;

public class Assignment extends SingleEvent {

  private EnumeratedVariable variable;
  private byte value;

  public Assignment(EnumeratedVariable variable, byte value) {
    super(variable.getVarname());
    this.variable = variable;
    this.value = value;
  }

  public EnumeratedVariable getVariable() {
    return variable;
  }

  public byte getValue() {
    return value;
  }

  @Override
  public String toString() {
    if (variable instanceof BooleanVariable) {
      return variable + " : = " + BooleanVariable.getStringFromByte(value);
    } else if (variable instanceof EnumeratedVariable) {
      return variable + " := " + variable.getEnumeration().getOption(value);
    }
    throw new Error();
  }
}
