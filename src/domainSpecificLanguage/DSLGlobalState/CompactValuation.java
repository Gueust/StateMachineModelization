package domainSpecificLanguage.DSLGlobalState;

import java.util.Arrays;
import java.util.NoSuchElementException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.conditions.valuation.AbstractValuation;

/**
 * Mapping of the variables to {true, false}. Not defined variable are
 * considered
 * false.
 */
public class CompactValuation extends AbstractValuation {

  private byte[] valuation;

  /**
   * Create a new empty valuation.
   */
  public CompactValuation(int nb_variables) {
    valuation = new byte[nb_variables];
  }

  private CompactValuation(CompactValuation dsl_valuation) {
    this.valuation = dsl_valuation.valuation.clone();
  }

  public CompactValuation clone() {
    return new CompactValuation(this);
  }

  public byte getValue(EnumeratedVariable v) {
    Byte res = valuation[v.getUniqueIdentifier()];
    if (res == null) {
      throw new NoSuchElementException("The value for " + v
          + " does not exist.");
    }

    return res.byteValue();
  }

  public boolean setValue(EnumeratedVariable var, byte value) {
    byte old_value = valuation[var.getUniqueIdentifier()];
    valuation[var.getUniqueIdentifier()] = value;

    return old_value != value;
  }

  @Override
  public String toString() {
    return Arrays.toString(valuation);
  }

  public String toString(Iterable<EnumeratedVariable> variables) {
    StringBuffer string_buffer = new StringBuffer();
    boolean first = true;
    for (EnumeratedVariable var : variables) {
      if (!first) {
        string_buffer.append(", ");
      }
      byte value = valuation[var.getUniqueIdentifier()];
      string_buffer.append(var.getVarname() + ": "
          + var.getOptionFromByte(value));
      first = false;
    }
    return string_buffer.toString();
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

  @Override
  public int size() {
    return valuation.length;
  }

  @Override
  public boolean getValue(BooleanVariable v) {
    return BooleanVariable
        .getBooleanFromByte(valuation[v.getUniqueIdentifier()]);
  }

  @Override
  public boolean setValue(BooleanVariable var, boolean value) {
    byte old_value = valuation[var.getUniqueIdentifier()];
    byte new_value = BooleanVariable.getByteFromBool(value);
    valuation[var.getUniqueIdentifier()] = new_value;
    return new_value != old_value;
  }

  @Override
  public boolean variableValueWillChange(BooleanVariable variable, boolean value) {
    throw new NotImplementedException();
  }

  @Override
  public boolean variableInitialized(BooleanVariable variable) {
    throw new NotImplementedException();
  }

}
