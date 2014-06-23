package abstractGraph.conditions.valuation;

import java.util.HashMap;
import java.util.NoSuchElementException;

import abstractGraph.conditions.Variable;

/**
 * Mapping of the variables to {true, false}.
 */
public class Valuation extends AbstractValuation {

  /* Equality of variable is pointer equality ! */
  protected HashMap<Variable, Boolean> valuation;

  /**
   * Create a new empty valuation.
   */
  public Valuation() {
    super(0);
    valuation = new HashMap<Variable, Boolean>();
  }

  /**
   * Create a new empty valuation.
   */
  public Valuation(int nb_variables) {
    super(nb_variables);
    valuation = new HashMap<Variable, Boolean>(nb_variables);
  }

  @Override
  public boolean getValue(Variable v) {
    Boolean res = valuation.get(v);
    if (res == null) {
      throw new NoSuchElementException("The value for " + v
          + " does not exist.");
    }

    return res.booleanValue();
  }

  @Override
  public boolean setValue(Variable var, boolean value) {
    Boolean old_value = valuation.get(var);
    valuation.put(var, value);

    return old_value != null && !old_value.equals(value);
  }

  /**
   * Remove the given variable from the valuation.
   */
  public void remove(Variable var) {
    valuation.remove(var);
  }

  @Override
  public String toString() {
    return valuation.toString();
  }

  @SuppressWarnings("unchecked")
  public Valuation clone() {
    Valuation result = new Valuation(valuation.size());
    result.valuation = (HashMap<Variable, Boolean>) valuation.clone();
    return result;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((valuation == null) ? 0 : valuation.hashCode());
    return result;
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
    return this.valuation.equals(other.valuation);
  }

  public void clear() {
    valuation.clear();
  }

  @Override
  public int size() {
    return valuation.size();
  }
}
