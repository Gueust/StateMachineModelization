package abstractGraph.conditions.valuation;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import abstractGraph.conditions.Variable;

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
      this.valuation[entry.getKey().getIdentifier()] = entry
          .getValue()
          .booleanValue();
    }
  }

  @Override
  public boolean getValue(Variable v) {
    Boolean res = valuation[v.getIdentifier()];
    if (res == null) {
      throw new NoSuchElementException("The value for " + v
          + " does not exist.");
    }

    return res.booleanValue();
  }

  @Override
  public boolean setValue(Variable var, boolean value) {
    Boolean old_value = valuation[var.getIdentifier()];
    valuation[var.getIdentifier()] = value;

    return old_value != null && !old_value.equals(value);
  }

  @Override
  public String toString() {
    return Arrays.toString(valuation);
  }

  public String toString(Iterable<Variable> variables) {
    StringBuffer string_buffer = new StringBuffer();
    boolean first = true;
    for (Variable var : variables) {
      if (!first) {
        string_buffer.append(", ");
      }
      string_buffer.append(var.getVarname() + ": " + getValue(var));
      first = false;
    }
    return string_buffer.toString();
  }

  public CompactValuation clone() {
    CompactValuation result = new CompactValuation(valuation.length);
    result.valuation = Arrays.copyOf(valuation, valuation.length);
    return result;
  }

  @Override
  public int size() {
    return valuation.length;
  }

  /* Generated using Eclipse */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(valuation);
    return result;
  }

  /* Generated using Eclipse */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CompactValuation other = (CompactValuation) obj;
    if (!Arrays.equals(valuation, other.valuation))
      return false;
    return true;
  }
}
