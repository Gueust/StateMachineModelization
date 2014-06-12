package abstractGraph.conditions;

import java.util.HashSet;
import java.util.Iterator;

import abstractGraph.conditions.parser.BooleanExpressionFactory;

/**
 * @package abstractGraph.conditions
 * 
 *          The root elements to use this package is the {@link FormulaFactory}
 *          class. This allows to create {@link Formula}s.
 *          
 *          To evaluate a formula, you first need to create a {@link Valuation}
 *          for the formula. In particular, to set the value for a variable,
 *          one need to retrieve de variable from the FormulaFactory.
 */
/**
 * Boolean expression formula
 */
public abstract class Formula {

  /** The AND keyword */
  public static final String AND = "ET";
  /** The OR keyword */
  public static final String OR = "OU";
  /** The NOT keyword */
  public static final String NOT = "NON";

  /** The unique immutable TRUE value */
  public static final True TRUE = new True();

  /** The unique immutable FALSE value */
  public static final False FALSE = new False();

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
  public static FormulaFactory newDefaultFactory() {
    return new BooleanExpressionFactory(true);
  }

  /**
   * Add into the given HashSet variables that are in the given formula
   * 
   * @return A set that contains (not exclusively) the variables of the formula.
   */
  public abstract HashSet<Variable> allVariables(HashSet<Variable> vars);

  /**
   * Evaluate the formula within an environment.
   * 
   * @param valuation
   *          The valuation for the variables.
   * @return The evaluation of the formula.
   */
  public abstract boolean eval(Valuation valuation);

  @Override
  final public boolean equals(Object o) {
    return equals(this, (Formula) o);
  }

  private static final boolean equals(Formula f1, Formula f2) {
    HashSet<Variable> s1 = f1.allVariables(new HashSet<Variable>());
    HashSet<Variable> s2 = f2.allVariables(new HashSet<Variable>());

    /* We use the greater set of variables */
    HashSet<Variable> all_variables = new HashSet<Variable>(s1);
    all_variables.addAll(s2);

    Valuation valuation = new Valuation();
    return partialEquals(all_variables, valuation, f1, f2);
  }

  /**
   * Check that the 2 formulas are equal considering the given valuation under
   * the assumption that the variables in `vars` are not fixed.
   * 
   * @return
   */
  private static final boolean partialEquals(HashSet<Variable> vars,
      Valuation valuation, Formula f1, Formula f2) {

    /* Terminal case */
    if (vars.isEmpty()) {
      return f1.eval(valuation) == f2.eval(valuation);
    }

    /* Recursion */
    Iterator<Variable> it = vars.iterator();
    Variable v = it.next();
    it.remove();

    valuation.setValue(v, true);
    if (!partialEquals(new HashSet<Variable>(vars), valuation, f1, f2)) {
      return false;
    }

    valuation.setValue(v, false);
    if (!partialEquals(new HashSet<Variable>(vars), valuation, f1, f2)) {
      return false;
    }

    return true;
  }

  public static final CustomToString DEFAULT_STRINGIFIER =
      new CustomToString();

  /**
   * Allow custum printing of formulas
   * 
   * @param c
   *          A customizer that will print the object.
   * @return
   */
  public String toString(CustomToString c) {
    if (c == null) {
      return toString();
    } else {
      return c.toString(this);
    }
  }

  @Override
  public abstract String toString();
}
