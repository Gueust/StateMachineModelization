package abstractGraph.conditions;

import java.util.HashSet;

import abstractGraph.conditions.valuation.AbstractValuation;

public class Variable extends Formula {

  protected String varname;
  /* The unique identifier of the Variable throughout a model */
  protected int identifier = -1;

  public Variable(String s) {
    varname = s;
  }

  @Override
  public boolean eval(AbstractValuation valuation) {
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

  public int getIdentifier() {
    return identifier;
  }

  public void setIdentifier(int identifier) {
    this.identifier = identifier;
  }

}
