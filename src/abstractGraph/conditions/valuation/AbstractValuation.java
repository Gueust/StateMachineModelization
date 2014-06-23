package abstractGraph.conditions.valuation;

import abstractGraph.conditions.Variable;

public abstract class AbstractValuation {

  public AbstractValuation(int nb_variables) {

  }

  /**
   * 
   * @return The number of internal variables that are managed.
   */
  public abstract int size();

  /**
   * Retrieve the value of variable `v` in the current valuation.
   * 
   * @param v
   *          The variable.
   * @return The boolean value of the variable..
   */
  public abstract boolean getValue(Variable v);

  /**
   * Set the value or a variable in the valuation.
   * 
   * @param var
   *          The variable to modify.
   * @param value
   *          The value to set.
   * @return False if the value doesn't change or its precedent value was
   *         not defined. True otherwise.
   */
  public abstract boolean setValue(Variable var, boolean value);

  public abstract Object clone();

  public abstract int hashCode();

  public abstract boolean equals(Object obj);
}
