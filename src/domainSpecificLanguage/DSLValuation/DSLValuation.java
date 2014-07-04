package domainSpecificLanguage.DSLValuation;

import java.util.Arrays;
import java.util.NoSuchElementException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import abstractGraph.conditions.Variable;
import abstractGraph.conditions.valuation.AbstractValuation;
import domainSpecificLanguage.graph.DSLVariable;

/**
 * Mapping of the variables to {true, false}. Not defined variable are
 * considered
 * false.
 */
public class DSLValuation extends AbstractValuation {

  private byte[] valuation;

  /**
   * Create a new empty valuation.
   */
  public DSLValuation(int nb_variables) {
    valuation = new byte[nb_variables];
  }

  private DSLValuation(DSLValuation dsl_valuation) {
    this.valuation = dsl_valuation.valuation.clone();
  }

  public DSLValuation clone() {
    return new DSLValuation(this);
  }

  public byte getValue(DSLVariable v) {
    Byte res = valuation[v.getUniqueIdentifier()];
    if (res == null) {
      throw new NoSuchElementException("The value for " + v
          + " does not exist.");
    }

    return res.byteValue();
  }

  public boolean setValue(DSLVariable var, byte value) {
    byte old_value = valuation[var.getUniqueIdentifier()];
    valuation[var.getUniqueIdentifier()] = value;

    return old_value != value;
  }

  @Override
  public String toString() {
    return Arrays.toString(valuation);
  }

  public String toString(Iterable<DSLVariable> variables) {
    StringBuffer string_buffer = new StringBuffer();
    boolean first = true;
    for (DSLVariable var : variables) {
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
    DSLValuation other = (DSLValuation) obj;
    if (!Arrays.equals(valuation, other.valuation))
      return false;
    return true;
  }

  @Override
  public int size() {
    throw new NotImplementedException();
  }

  @Override
  public boolean getValue(Variable v) {
    throw new NotImplementedException();
  }

  @Override
  public boolean setValue(Variable var, boolean value) {
    throw new NotImplementedException();
  }

  @Override
  public boolean variableValueWillChange(Variable variable, boolean value) {
    throw new NotImplementedException();
  }

  @Override
  public boolean variableInitialized(Variable variable) {
    throw new NotImplementedException();
  }

}
