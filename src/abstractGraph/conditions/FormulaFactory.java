package abstractGraph.conditions;

import org.antlr.v4.runtime.misc.Nullable;

/**
 * All formula factory should be able to parse a string and return a valid
 * Formula.
 */
public interface FormulaFactory {

  public Formula parse(String expression, boolean view_tree);

  /**
   * 
   * @param expression
   * @return A valid formula if the expression contains a valid expression.
   *         null can be returned if the formula is empty.
   */
  public @Nullable
  Formula parse(String expression);
}
