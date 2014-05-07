package abstractGraph.conditions;

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

  /**
   * An OR formula represents : p OR q. This function returns p.
   * 
   * @return The first formula p of this OR
   */
  public Formula getFirst() {
    return p;
  }

  /**
   * An OR formula represents : p OR q. This function returns q.
   * 
   * @return The second formula q of this OR
   */
  public Formula getSecond() {
    return q;
  }

  @Override
  public String toString() {
    return parenthesis(p) + " " + Formula.OR + " " + parenthesis(q);
  }

  private String parenthesis(Formula f) {
    return f.toString();
  }
}
