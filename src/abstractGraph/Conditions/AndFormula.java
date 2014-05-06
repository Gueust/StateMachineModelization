package abstractGraph.Conditions;

public class AndFormula extends Formula {

  Formula p;
  Formula q;

  /**
   * Create p AND q.
   * 
   * @param p
   *          The left formula.
   * @param q
   *          The right formula.
   */
  public AndFormula(Formula p, Formula q) {
    this.p = p;
    this.q = q;
  }

  @Override
  public String toString() {
    return "( " + p.toString() + " " + Formula.AND + " " + q.toString() + " )";
  }
}
