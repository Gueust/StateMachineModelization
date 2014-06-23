package abstractGraph.conditions;

import java.util.HashSet;

public class False extends Formula {

  False() {
  }

  @Override
  public boolean eval(AbstractValuation valuation) {
    return false;
  }

  @Override
  public HashSet<Variable> allVariables(HashSet<Variable> vars) {
    return vars;
  }

  @Override
  public String toString() {
    return "false";
  }
}
