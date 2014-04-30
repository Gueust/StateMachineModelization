package abstractGraph.Conditions;

public class NotFormula extends Formula {

  Formula f;

  public NotFormula(Formula f) {
    this.f = f;
  }
}
