package abstractGraph.conditions;

import java.util.HashSet;

import domainSpecificLanguage.DSLGlobalState.CompactValuation;
import abstractGraph.conditions.valuation.AbstractValuation;

public class EnumerationEqualityFormula extends Formula {

  private EnumeratedVariable variable;
  private byte value;
  private boolean is_not;

  public EnumerationEqualityFormula(EnumeratedVariable variable, byte value,
      boolean is_not) {
    this.variable = variable;
    this.value = value;
    this.is_not = is_not;
  }

  @Override
  public HashSet<EnumeratedVariable> allVariables(
      HashSet<EnumeratedVariable> vars) {
    vars.add(variable);
    return vars;
  }

  @Override
  public boolean eval(AbstractValuation valuation) {
    CompactValuation dsl_valuation = (CompactValuation) valuation;
    if (is_not) {
      return dsl_valuation.getValue(variable) != value;
    } else {
      return dsl_valuation.getValue(variable) == value;
    }
  }

  @Override
  public String toString() {
    String middle_string;
    if (is_not) {
      middle_string = " is not ";
    } else {
      middle_string = " is ";
    }
    return variable.getVarname() + middle_string
        + variable.getOptionFromByte(value);
  }

}
