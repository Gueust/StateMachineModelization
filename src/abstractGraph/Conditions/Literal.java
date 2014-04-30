package abstractGraph.Conditions;

public class Literal {
  Variable variable;
  boolean is_negated;

  public Literal(Variable variable, boolean is_negated) {
    this.variable = variable;
    this.is_negated = is_negated;
  }

  public Literal(Variable variable) {
    this(variable, false);
  }
}
