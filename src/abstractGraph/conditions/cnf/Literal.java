package abstractGraph.conditions.cnf;

import java.util.HashSet;

import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.conditions.valuation.AbstractValuation;

/**
 * Literals are used in CNFFormulas.
 */
public class Literal extends Formula {
  private BooleanVariable variable;
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
  public Literal(BooleanVariable variable, boolean is_negated) {
    this.variable = variable;
    this.is_negated = is_negated;
    assert (this.variable != null);
  }

  /**
   * Create a new positive literal associated to `variable`.
   * 
   * @param variable
   *          A variable.
   */
  public Literal(BooleanVariable variable) {
    this(variable, false);
  }

  @Override
  public boolean eval(AbstractValuation valuation) {
    if (is_negated) {
      return !variable.eval(valuation);
    } else {
      return variable.eval(valuation);
    }
  }

  public BooleanVariable getVariable() {
    return variable;
  }

  public boolean isNegated() {
    return is_negated;
  }

  @Override
  public HashSet<EnumeratedVariable> allVariables(
      HashSet<EnumeratedVariable> vars) {
    vars.add(variable);
    return vars;
  }

  @Override
  public String toString() {
    if (is_negated) {
      assert (variable != null);
      return Formula.NOT + " " + variable.toString();
    } else {
      return variable.toString();
    }
  }
}
