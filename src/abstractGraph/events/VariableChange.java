package abstractGraph.events;

import engine.SequentialGraphSimulator;
import abstractGraph.conditions.CustomToString;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.conditions.cnf.Literal;

/**
 * Used in the ACTION field of AEFD Graphs and within the execution of
 * {@link GraphSimulator} and {@link SequentialGraphSimulator}.
 */
public class VariableChange extends InternalEvent {

  /**
   * The literal that will become true after the execution of the VariableChange
   */
  private Literal l;

  public VariableChange(Literal literal) {
    super(literal.toString());
    assert (literal != null);
    l = literal;
  }

  /**
   * 
   * @return The variable that is modified by this VariableChange
   */
  public BooleanVariable getModifiedVariable() {
    return l.getVariable();
  }

  public boolean isNegated() {
    return l.isNegated();
  }

  public String toString(CustomToString customizer) {
    if (customizer == null)
      return l.toString();
    else {
      return customizer.toString(l);
    }
  }
}
