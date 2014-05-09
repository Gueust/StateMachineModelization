package abstractGraph.conditions;

import abstractGraph.conditions.parser.BooleanExpressionFactory;

/**
 * Boolean expression formula
 */
public abstract class Formula implements AbstractCondition {

  /* The AND keyword */
  public static final String AND = "ET";
  /* The OR keyword */
  public static final String OR = "OU";
  /* The NOT keyword */
  public static final String NOT = "NON";

  /**
   * A default factory that parses boolean expressions that uses only OR, AND,
   * and NOT operators. This default factory is unique (i.e. the same instance
   * will be always returned).
   * 
   * This factory is in UNITED MODEL mode.
   * 
   * Variables can have their name using [a-zA-Z0-9_].
   * AND has priority over OR, and NOT is highest priority operator.
   * OR tokens are: 'OR' , 'OU' , '||' , '|'
   * AND tokens are:'AND' ,'&&', '&','ET'
   * NOT tokens are: "NOT", "Not", "NON"
   * 
   * @see FormulaFactory
   */
  public static final FormulaFactory DEFAULT_FACTORY =
      new BooleanExpressionFactory(true);

  static public Formula newFormula(String expression) {
    Formula result = DEFAULT_FACTORY.parse(expression);
    return result;
  }

  @Override
  public abstract String toString();
}
