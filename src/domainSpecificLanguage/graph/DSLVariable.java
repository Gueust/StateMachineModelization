package domainSpecificLanguage.graph;

import domainSpecificLanguage.DSLValuation.Enumeration;
import abstractGraph.conditions.Variable;

public class DSLVariable extends Variable {

  private Enumeration enumeration;

  public DSLVariable(String s, Enumeration enumeration) {
    super(s);
    this.enumeration = enumeration;
  }

  public Enumeration getEnumeration() {
    return enumeration;
  }

  public boolean isBool() {
    return enumeration == null;
  }

  public boolean isEnumeration() {
    return enumeration != null;
  }

}
