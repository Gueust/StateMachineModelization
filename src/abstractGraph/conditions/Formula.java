package abstractGraph.conditions;

import java.util.HashSet;
import java.util.Iterator;

import abstractGraph.conditions.parser.BooleanExpressionFactory;
import abstractGraph.conditions.valuation.AbstractValuation;
import abstractGraph.conditions.valuation.Valuation;

/**
 * Boolean expression formula.
 * It includes the following operands: And, or, not. In particular it does not
 * have => or <=>.
 * 
 * The formulas can be:
 * <ol>
 * <li>the constant true, or false formula.</li>
 * <li>The and of a formula, the conjunction (and) or disjunction (or) of 2
 * formulas.</li>
 * <li>The equality between a enumerated variable (i.e. a variable having its
 * value in a finite set) and a term of this set.</li>
 * </ol>
 * 
 * An other group of formulas (which cannot be mixed with the already defined
 * formulas except for true and false formulas) is contained in the cnf package.
 * The Conjunctive Normal Form is only required as the input of a SAT solver.
 * A CNF is the conjunction of clauses. A clause is a disjunction of literals.
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
  public abstract HashSet<EnumeratedVariable> allVariables(
      HashSet<EnumeratedVariable> vars);

  /**
   * Evaluate the formula within an environment.
   * 
   * @param valuation
   *          The valuation for the variables.
   * @return The evaluation of the formula.
   */
  public abstract boolean eval(AbstractValuation valuation);

  final public boolean equals(Formula o) {
    return equalsBooleanFormula(this, o);
  }

  private static final boolean equalsBooleanFormula(Formula f1, Formula f2) {
    HashSet<EnumeratedVariable> s1 = f1
        .allVariables(new HashSet<EnumeratedVariable>());
    HashSet<EnumeratedVariable> s2 = f2
        .allVariables(new HashSet<EnumeratedVariable>());

    /* We use the greater set of variables */
    HashSet<EnumeratedVariable> all_variables = new HashSet<>(s1);
    all_variables.addAll(s2);

    Valuation valuation = new Valuation(all_variables.size());
    return partialEqualsBooleanFormula(all_variables, valuation, f1, f2);
  }

  /**
   * Check that the 2 formulas are equal considering the given valuation under
   * the assumption that the variables in `vars` are not fixed.
   * 
   * @return
   */
  private static final boolean partialEqualsBooleanFormula(
      HashSet<EnumeratedVariable> vars,
      Valuation valuation, Formula f1, Formula f2) {

    /* Terminal case */
    if (vars.isEmpty()) {
      return f1.eval(valuation) == f2.eval(valuation);
    }

    /* Recursion */
    Iterator<EnumeratedVariable> it = vars.iterator();
    BooleanVariable v = (BooleanVariable) it.next();
    it.remove();

    valuation.setValue(v, true);
    if (!partialEqualsBooleanFormula(new HashSet<EnumeratedVariable>(vars),
        valuation, f1, f2)) {
      return false;
    }

    valuation.setValue(v, false);
    if (!partialEqualsBooleanFormula(new HashSet<EnumeratedVariable>(vars),
        valuation, f1, f2)) {
      return false;
    }

    return true;
  }

  public static final CustomToString DEFAULT_STRINGIFIER =
      new CustomToString();

  /**
   * Allow custom printing of formulas
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
