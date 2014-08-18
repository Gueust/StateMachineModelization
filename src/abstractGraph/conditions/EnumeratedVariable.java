package abstractGraph.conditions;

import java.util.HashSet;

import abstractGraph.conditions.valuation.AbstractValuation;

/**
 * @brief
 *        A variable having its value in a defined finite set of values.
 * 
 */
public class EnumeratedVariable extends Formula {

  /** Can be used to print the identifier of the variable */
  public static final boolean DEBUG_TO_STRING = false;

  protected String varname;
  /** The unique identifier of the Variable throughout a simulator. */
  protected int identifier;
  private Enumeration enumeration;

  protected EnumeratedVariable(String s, int identifier) {
    this.varname = s;
    this.identifier = identifier;
  }

  public EnumeratedVariable(String s, int identifier, Enumeration enumeration) {
    this(s, identifier);
    if (enumeration == null) {
      throw new IllegalArgumentException(
          "Enumerated variables must be created with a non null enumeration");
    }
    this.enumeration = enumeration;
  }

  public int getUniqueIdentifier() {
    return identifier;
  }

  public Enumeration getEnumeration() {
    return enumeration;
  }

  public String getOptionFromByte(byte value) {
    return enumeration.getOption(value);
  }

  public String getVarname() {
    return varname;
  }

  public boolean isBool() {
    return (this instanceof BooleanVariable);
  }

  @Override
  public HashSet<EnumeratedVariable> allVariables(
      HashSet<EnumeratedVariable> vars) {
    vars.add(this);
    return vars;
  }

  @Override
  public boolean eval(AbstractValuation valuation) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    if (DEBUG_TO_STRING) {
      return getVarname() + "(" + getUniqueIdentifier() + ")";
    } else {
      return getVarname();
    }
  }
}
