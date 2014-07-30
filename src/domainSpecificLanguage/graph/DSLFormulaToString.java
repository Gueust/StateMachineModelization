package domainSpecificLanguage.graph;

import abstractGraph.conditions.CustomToString;

public class DSLFormulaToString extends CustomToString {

  public static final DSLFormulaToString INSTANCE = new DSLFormulaToString();

  private DSLFormulaToString() {
    NOT = "not";
    OR = "or";
    AND = "and";
  }

}