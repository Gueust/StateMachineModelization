package abstractGraph.conditions;

import java.util.HashSet;

import abstractGraph.conditions.valuation.AbstractValuation;

public class True extends Formula {

  True() {
  }

  @Override
  public boolean eval(AbstractValuation valuation) {
    return true;
  }

  @Override
  public HashSet<EnumeratedVariable> allVariables(
      HashSet<EnumeratedVariable> vars) {
    return vars;
  }

  @Override
  public String toString() {
    return "true";
  }
}
