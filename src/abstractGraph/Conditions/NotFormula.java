package abstractGraph.Conditions;

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
  public String toString() {
    return "(" + Formula.NOT + " " + f.toString() + ")";
  }
}
