package abstractGraph.conditions;

import java.util.HashSet;

import abstractGraph.conditions.valuation.AbstractValuation;

public class False extends Formula {

  False() {
  }

  @Override
  public boolean eval(AbstractValuation valuation) {
    return false;
  }

  @Override
  public HashSet<EnumeratedVariable> allVariables(
      HashSet<EnumeratedVariable> vars) {
    return vars;
  }

  @Override
  public String toString() {
    return "false";
  }
}
