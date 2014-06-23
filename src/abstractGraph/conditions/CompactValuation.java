package abstractGraph.conditions;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Mapping of the variables to {true, false}. Not defined variable are
 * considered
 * false.
 */
public class CompactValuation extends AbstractValuation {

  /* Equality of variable is pointer equality ! */
  private boolean[] valuation;

  /**
   * Create a new empty valuation.
   */
  public CompactValuation(int nb_variables) {
    super(nb_variables);
    valuation = new boolean[nb_variables];
  }

  public CompactValuation(Valuation val) {
    this(val.size());
    for (Entry<Variable, Boolean> entry : val.valuation.entrySet()) {
      this.valuation[entry.getKey().identifier] = entry
          .getValue()
          .booleanValue();
    }
  }

  @Override
  public boolean getValue(Variable v) {
    Boolean res = valuation[v.identifier];
    if (res == null) {
      throw new NoSuchElementException("The value for " + v
          + " does not exist.");
    }

    return res.booleanValue();
  }

  @Override
  public boolean setValue(Variable var, boolean value) {
    Boolean old_value = valuation[var.identifier];
    valuation[var.identifier] = value;

    return old_value != null && !old_value.equals(value);
  }

  /**
   * Remove the given variable from the valuation.
   */
  public void remove(Variable var) {
    throw new NotImplementedException();
  }

  @Override
  public String toString() {
    return valuation.toString();
  }

  public CompactValuation clone() {
    CompactValuation result = new CompactValuation(valuation.length);
    result.valuation = Arrays.copyOf(valuation, valuation.length);
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
    CompactValuation other = (CompactValuation) obj;
    /* The valuation should not be null */
    if ((valuation == null || other.valuation == null)) {
      throw new Error(
          "The valuation HashMap cannot be null since it is always initialized");
    }
    return this.valuation.equals(other.valuation);
  }

  public void clear() {
    throw new NotImplementedException();
  }

  @Override
  public int size() {
    return valuation.length;
  }
}
