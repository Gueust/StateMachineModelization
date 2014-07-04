package domainSpecificLanguage.graph;

import domainSpecificLanguage.DSLValuation.Enumeration;
import abstractGraph.conditions.Variable;

public class DSLVariable extends Variable {

  private Enumeration enumeration;
  protected int identifier;

  public DSLVariable(String s, int identifier, Enumeration enumeration) {
    super(s);
    this.identifier = identifier;
    this.enumeration = enumeration;
  }

  public Enumeration getEnumeration() {
    return enumeration;
  }

  public String getOptionFromByte(byte value) {
    return enumeration.getOption(value);
  }

  public boolean isBool() {
    return enumeration == null;
  }

  public boolean isEnumeration() {
    return enumeration != null;
  }

  public int getUniqueIdentifier() {
    return identifier;
  }
}
