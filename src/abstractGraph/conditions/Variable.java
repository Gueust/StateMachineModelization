package abstractGraph.conditions;

import java.util.HashSet;

public class Variable extends Formula {

  protected String varname;

  public Variable(String s) {
    varname = s;
  }

  @Override
  public boolean eval(Valuation valuation) {
    return valuation.getValue(this);
  }

  @Override
  public HashSet<Variable> allVariables(HashSet<Variable> vars) {
    vars.add(this);
    return vars;
  }

  public String getVarname() {
    return varname;
  }

  @Override
  public String toString() {
    return getVarname();
  }
}
