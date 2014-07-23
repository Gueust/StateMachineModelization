package domainSpecificLanguage.graph;

import abstractGraph.conditions.CustomToString;

public class DSLToString extends CustomToString {

  public static final DSLToString INSTANCE = new DSLToString();

  private DSLToString() {
    NOT = "not";
    OR = "or";
    AND = "and";
  }

}