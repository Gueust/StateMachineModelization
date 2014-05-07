package abstractGraph.conditions.cnf;

import abstractGraph.GlobalState;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.Variable;

public class Literal extends Formula {
  private Variable variable;
  boolean is_negated;

  public Literal(Variable variable, boolean is_negated) {
    this.variable = variable;
    this.is_negated = is_negated;
  }

  public Literal(Variable variable) {
    this(variable, false);
  }

  @Override
  public boolean eval(GlobalState valuation) {
    // TODO Auto-generated method stub
    return false;
  }

  public Variable getVariable() {
    return variable;
  }

  public boolean IsNegated() {
    return is_negated;
  }

  @Override
  public String toString() {
    if (is_negated) {
      return Formula.NOT + " " + variable.toString();
    } else {
      return variable.toString();
    }
  }
}
