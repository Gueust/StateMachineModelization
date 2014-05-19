package abstractGraph.events;

import abstractGraph.conditions.Variable;
import abstractGraph.conditions.cnf.Literal;

public class VariableChange extends InternalEvent {

  /**
   * The literal that will become true after the execution of the VariableChange
   */
  private Literal l;

  public VariableChange(Literal literal) {
    super(literal.toString());
    l = literal;
  }

  /**
   * 
   * @return The variable that is modified by this VariableChange
   */
  public Variable getModifiedVariable() {
    return l.getVariable();
  }

}