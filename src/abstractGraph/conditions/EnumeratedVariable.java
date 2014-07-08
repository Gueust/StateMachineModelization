package abstractGraph.conditions;

import java.util.HashSet;

import abstractGraph.conditions.valuation.AbstractValuation;

public class EnumeratedVariable extends Formula {

  protected String varname;
  /* The unique identifier of the Variable throughout a model */
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
    return getVarname();
  }
}
