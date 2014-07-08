package abstractGraph.conditions.valuation;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utils.Monitoring;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.conditions.EnumeratedVariable;

/**
 * Mapping of the variables to {true, false}.
 */
public class Valuation extends AbstractValuation {

  /* Equality of variable is pointer equality ! */
  protected HashMap<BooleanVariable, Boolean> valuation;

  /**
   * Create a new empty valuation.
   */
  public Valuation() {
    valuation = new HashMap<BooleanVariable, Boolean>();
  }

  /**
   * Create a new empty valuation.
   */
  public Valuation(int nb_variables) {
    valuation = new HashMap<BooleanVariable, Boolean>(nb_variables);
  }

  @Override
  public boolean getValue(BooleanVariable v) {
    Boolean res = valuation.get(v);
    if (res == null) {
      throw new NoSuchElementException("The value for " + v
          + " does not exist.");
    }

    return res.booleanValue();
  }

  @Override
  public boolean setValue(BooleanVariable var, boolean value) {
    Boolean old_value = valuation.get(var);
    valuation.put(var, value);

    return old_value != null && !old_value.equals(value);
  }

  /**
   * Remove the given variable from the valuation.
   */
  public void remove(BooleanVariable var) {
    valuation.remove(var);
  }

  private static final boolean DEBUG = false;

  @Override
  public String toString() {
    if (DEBUG) {
      StringBuffer string_buffer = new StringBuffer();
      boolean first = true;
      for (Entry<BooleanVariable, Boolean> entry : valuation.entrySet()) {
        BooleanVariable variable = entry.getKey();
        if (first) {
          first = false;
        } else {
          string_buffer.append(", ");
        }
        string_buffer.append(variable.getVarname() +
            "(" + Monitoring.getAdress(variable) + ")"
            + " = " + entry.getValue());
      }
      return string_buffer.toString();
    } else {
      return valuation.toString();
    }
  }

  @SuppressWarnings("unchecked")
  public Valuation clone() {
    Valuation result = new Valuation(valuation.size());
    result.valuation = (HashMap<BooleanVariable, Boolean>) valuation.clone();
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

  @Override
  public boolean variableValueWillChange(BooleanVariable variable, boolean value) {
    return valuation.get(variable) != null && valuation.get(variable) != value;
  }

  @Override
  public boolean variableInitialized(BooleanVariable variable) {
    Boolean res = valuation.get(variable);
    return res != null;
  }

  /**
   * @return A set of the Pair of defined variables.
   */
  public Set<Entry<BooleanVariable, Boolean>> getSetVariables() {
    return valuation.entrySet();
  }

  @Override
  public byte getValue(EnumeratedVariable v) {
    throw new NotImplementedException();
  }

  @Override
  public boolean setValue(EnumeratedVariable var, byte value) {
    throw new NotImplementedException();
  }

}
