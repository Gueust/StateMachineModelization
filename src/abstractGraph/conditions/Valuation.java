package abstractGraph.conditions;

import java.util.HashMap;

/**
 * Mapping of the variables to {true, false}.
 */
public class Valuation {

  public static final byte FALSE = 0;
  public static final byte TRUE = 1;

  /* Equality of variable is pointer equality ! */
  private HashMap<Variable, Byte> valuation;

  /**
   * Create a new empty valuation.
   */
  public Valuation() {
    valuation = new HashMap<Variable, Byte>();
  }

  /**
   * Retrieve the value of variable `v` in the current valuation.
   * 
   * @param v
   *          The variable.
   * @return The boolean value of the variable..
   */
  public boolean getValue(Variable v) {
    Byte res = valuation.get(v);
    if (res == null) {
      throw new NullPointerException("The value for " + v + " does not exist.");
    }

    return res.byteValue() == TRUE;
  }

  /**
   * Set the value or a variable in the valuation.
   * 
   * @param var
   *          The variable to modify.
   * @param value
   *          The value to set.
   */
  public void setValue(Variable var, boolean value) {
    if (value) {
      valuation.put(var, TRUE);
    } else {
      valuation.put(var, FALSE);
    }
  }

  @Override
  public String toString() {
    return valuation.toString();
  }
}
