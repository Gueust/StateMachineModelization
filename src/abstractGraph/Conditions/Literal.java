package abstractGraph.Conditions;

public class Literal extends Formula {
  private Variable variable;
  boolean is_negated;

  public Literal(Variable variable, boolean is_negated) {
    this.variable = variable;
    this.is_negated = is_negated;
  }

  public Literal(Variable variable) {
    this(variable, false);
  }

  public Variable getVariable() {
    return variable;
  }

  public boolean IsNegated() {
    return is_negated;
  }

  @Override
  public String toString() {
    if (is_negated) {
      return Formula.NOT + " " + variable.toString();
    } else {
      return variable.toString();
    }
  }
}
