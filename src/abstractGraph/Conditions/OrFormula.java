package abstractGraph.Conditions;

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
  public String toString() {
    return "( " + p.toString() + " " + Formula.OR + " " + q.toString() + " )";
  }
}
