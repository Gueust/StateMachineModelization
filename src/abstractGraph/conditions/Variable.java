package abstractGraph.conditions;

import java.util.HashSet;
import java.util.NoSuchElementException;

public class Variable extends Formula {

  protected String varname;

  public Variable(String s) {
    varname = s;
  }

  @Override
  public boolean eval(Valuation valuation) {
    Boolean result = valuation.getValue(this);

    if (result == null) {
      throw new NoSuchElementException("The variable " + varname
          + " is not set in the valuation " + valuation);
    } else {
      return valuation.getValue(this);
    }
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
