package domainSpecificLanguage.DSLValuation;

import java.util.HashSet;

import domainSpecificLanguage.graph.DSLVariable;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.Variable;
import abstractGraph.conditions.valuation.AbstractValuation;

public class EnumerationEqualityFormula extends Formula {

  private DSLVariable variable;
  private byte value;
  private boolean is_not;

  public EnumerationEqualityFormula(DSLVariable variable, byte value,
      boolean is_not) {
    this.variable = variable;
    this.value = value;
    this.is_not = is_not;
  }

  @Override
  public HashSet<Variable> allVariables(HashSet<Variable> vars) {
    vars.add(variable);
    return vars;
  }

  @Override
  public boolean eval(AbstractValuation valuation) {
    DSLValuation dsl_valuation = (DSLValuation) valuation;
    if (is_not) {
      return dsl_valuation.getValue(variable) != value;
    } else {
      return dsl_valuation.getValue(variable) == value;
    }
  }

  @Override
  public String toString() {
    return variable.getVarname() + " is " + variable.getOptionFromByte(value);
  }

}
