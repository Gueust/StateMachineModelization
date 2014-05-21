package abstractGraph.conditions.cnf;

import java.util.HashSet;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.Valuation;
import abstractGraph.conditions.Variable;

public class Literal extends Formula {
  private Variable variable;
  boolean is_negated;

  /**
   * Create a new literal associated to `variable`.
   * It is negated if `is_negated` is true.
   * 
   * @param variable
   *          The variable being in the literal.
   * @param is_negated
   *          True to get the (NOT `variable`) literal.
   *          False to get the (`variable`) literal.
   */
  public Literal(Variable variable, boolean is_negated) {
    this.variable = variable;
    this.is_negated = is_negated;
  }

  /**
   * Create a new positive literal associated to `variable`.
   * 
   * @param variable
   *          A variable.
   */
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

  public boolean isNegated() {
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
