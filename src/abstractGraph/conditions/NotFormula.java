package abstractGraph.conditions;

import java.util.HashSet;

import abstractGraph.conditions.valuation.AbstractValuation;

public class NotFormula extends Formula {

  Formula f;

  /**
   * Create NOT f
   * 
   * @param f
   *          The formula to negate
   */
  public NotFormula(Formula f) {
    this.f = f;
  }

  @Override
  public boolean eval(AbstractValuation valuation) {
    return !f.eval(valuation);
  }

  /**
   * A NOT formula represents NOT f, where f is a formula. This functions
   * returns f.
   * 
   * @return The negated formula f
   */
  public Formula getF() {
    return f;
  }

  @Override
  public HashSet<Variable> allVariables(HashSet<Variable> vars) {
    f.allVariables(vars);
    return vars;
  }

  @Override
  public String toString() {
    return "(" + Formula.NOT + "( " + f.toString() + ") )";
  }
}
