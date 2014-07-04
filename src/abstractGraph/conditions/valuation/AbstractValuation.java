package abstractGraph.conditions.valuation;

import abstractGraph.conditions.Variable;

public abstract class AbstractValuation {

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

  /**
   * 
   * @param variable
   * @param value
   * @return true if the value in the argument will change the current value of
   *         the variable. False otherwise.
   */
  public abstract boolean variableValueWillChange(Variable variable,
      boolean value);

  /**
   * 
   * @param variable
   * @return false if the variable isn't initialized.
   */
  public abstract boolean variableInitialized(Variable variable);

}
