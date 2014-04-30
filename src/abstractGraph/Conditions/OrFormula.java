package abstractGraph.Conditions;

public class OrFormula extends Formula {

  Formula p, q;

  public OrFormula(Formula p, Formula q) {
    this.p = p;
    this.q = q;
  }
}
