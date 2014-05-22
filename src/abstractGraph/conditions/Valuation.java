package abstractGraph.conditions;


import java.util.HashMap;


/**
 * Mapping of the variables to {true, false}.
 */
public class Valuation {

  /* Equality of variable is pointer equality ! */
  private HashMap<Variable, Boolean> valuation;

  /**
   * Create a new empty valuation.
   */
  public Valuation() {
    valuation = new HashMap<Variable, Boolean>();
  }

  /**
   * Retrieve the value of variable `v` in the current valuation.
   * 
   * @param v
   *          The variable.
   * @return The boolean value of the variable..
   */
  public boolean getValue(Variable v) {
    Boolean res = valuation.get(v);
    if (res == null) {
      throw new NullPointerException("The value for " + v + " does not exist.");
    }

    return res.booleanValue();
  }

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
  public boolean setValue(Variable var, boolean value) {
    Boolean old_value = valuation.get(var);
    valuation.put(var, value);

    return old_value != null && !old_value.equals(value);
  }

  @Override
  public String toString() {
    return valuation.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Valuation other = (Valuation) obj;
    /* The valuation should not be null */
    if ((valuation == null || other.valuation == null)) {
      throw new Error(
          "The valuation HashMap cannot be null since it is always initialized");
    }
    return this.valuation.equals(other);
  }
}
