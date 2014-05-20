package abstractGraph.conditions;

import graph.Model;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Mapping of the variables to {true, false}.
 */
public class Valuation {

  public static final byte FALSE = 0;
  public static final byte TRUE = 1;
  public static final byte INCOHERENT = 2;

  /* Equality of variable is pointer equality ! */
  private HashMap<Variable, Byte> valuation;

  /**
   * Create a valuation with all the variables found in model.
   * Initialize those variable to the value INCOHERENT.
   */
  public Valuation(Model model) {
    valuation = new HashMap<Variable, Byte>();

    Iterator<Variable> it = model.iteratorExistingVariables();
    while (it.hasNext()) {
      Variable var = it.next();
      valuation.put(var, INCOHERENT);
    }
  }

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
    if (res == INCOHERENT) {
      throw new NullPointerException("The value for " + v + " is INCOHERENT and can't be get.");
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
   * @return False if the value doesn't change or its precedent value was
   *         INCOHERENT. True otherwise.
   */
  public boolean setValue(Variable var, boolean value) {
    if (value) {
      if (valuation.get(var) == TRUE) {
        return false;
      } else if (valuation.get(var) == INCOHERENT) {
        valuation.put(var, TRUE);
        return false;
      }
      valuation.put(var, TRUE);
      return true;
    } else {
      if (valuation.get(var) == FALSE) {
        return false;
      } else if (valuation.get(var) == INCOHERENT) {
        valuation.put(var, FALSE);
        return false;
      }
      valuation.put(var, FALSE);
      return true;
    }
  }

  @Override
  public String toString() {
    return valuation.toString();
  }
}
