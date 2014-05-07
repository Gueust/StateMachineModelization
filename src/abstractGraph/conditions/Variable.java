package abstractGraph.conditions;

import abstractGraph.GlobalState;

public class Variable extends Formula {

  String varname;

  public Variable(String s) {
    varname = s;
  }

  @Override
  public boolean eval(GlobalState valuation) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public String toString() {
    return varname;
  }
}
