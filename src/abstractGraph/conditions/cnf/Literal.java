package abstractGraph.conditions.cnf;

import java.util.HashSet;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.Valuation;
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
  public boolean eval(Valuation valuation) {
    if (is_negated) {
      return !variable.eval(valuation);
    } else {
      return variable.eval(valuation);
    }
  }

  public Variable getVariable() {
    return variable;
  }

  public boolean IsNegated() {
    return is_negated;
  }

  @Override
  public HashSet<Variable> allVariables(HashSet<Variable> vars) {
    throw new NotImplementedException();
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
