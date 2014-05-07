package abstractGraph.conditions;

import abstractGraph.GlobalState;

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
  public boolean eval(GlobalState valuation) {
    // TODO Auto-generated method stub
    return false;
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
  public String toString() {
    return "(" + Formula.NOT + " " + f.toString() + ")";
  }
}
