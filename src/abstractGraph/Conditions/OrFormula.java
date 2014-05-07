package abstractGraph.Conditions;

import abstractGraph.GlobalState;

public class OrFormula extends Formula {

  Formula p, q;

  /**
   * Create p OR q.
   * 
   * @param p
   *          The left formula.
   * @param q
   *          The right formula.
   */
  public OrFormula(Formula p, Formula q) {
    this.p = p;
    this.q = q;
  }

  @Override
  public boolean eval(GlobalState valuation) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public String toString() {
    return parenthesis(p) + " " + Formula.OR + " " + parenthesis(q);
  }

  private String parenthesis(Formula f) {
    return f.toString();
  }
}
