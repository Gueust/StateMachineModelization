package domainSpecificLanguage;

import domainSpecificLanguage.DSLValuation.Enumeration;
import domainSpecificLanguage.graph.DSLVariable;
import abstractGraph.conditions.Variable;
import abstractGraph.events.SingleEvent;

public class Assignment extends SingleEvent {

  private DSLVariable variable;
  private byte value;

  public Assignment(DSLVariable variable, byte value) {
    super(variable.getVarname());
    this.variable = variable;
    this.value = value;
  }

  public Variable getVariable() {
    return variable;
  }

  public byte getValue() {
    return value;
  }

  @Override
  public String toString() {
    if (variable.isBool()) {
      return variable + " : = " + Enumeration.getBool(value);
    } else if (variable.isEnumeration()) {
      return variable + " := " + variable.getEnumeration().getOption(value);
    }
    throw new Error();
  }
}
