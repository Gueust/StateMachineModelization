package abstractGraph.Conditions;

public class AndFormula extends Formula {

  Formula p;
  Formula q;

  public AndFormula(NotFormula p, NotFormula q) {
    this.p = p;
    this.q = q;
  }

}
