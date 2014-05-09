package abstractGraph.conditions;

import java.util.HashMap;

/**
 * All formula factories should be able to parse a string and return a valid
 * Formula.
 * 
 * A factory should be able (this is not mandatory) to work in two modes:
 * <ol>
 * <li>
 * A single formula mode, that allows to create multiple not related formulas ;
 * 
 * </li>
 * <li>
 * A united model mode, that allows to create multiple related formulas. This
 * means that the variables are common within the created formulas. For
 * instance, if we have the formulas A & B, and A & C, then both uses of A
 * should use the <b>same</b> variable "A". This is the optional mode. In that
 * case, the subclass must document that it does not implement the united model
 * formula.
 * 
 * </li>
 * </ol>
 * 
 * By default, a factory is in the single formula mode, meaning that it
 * musts create fresh variables.
 * 
 * To create a FormulaFactory in the united model mode, use the constructor
 * {@link #FormulaFactory(boolean)} or {@link #setUnitedModelMode(boolean)}.
 */
public abstract class FormulaFactory {

  protected HashMap<String, Variable> existing_variables;

  /**
   * Initialize the FormulaFactory in the single formula mode.
   */
  public FormulaFactory() {
    this(false);
  };

  /**
   * Initialize the FormulaFactory to be in the united model mode.
   * 
   * @param united_model_mode
   *          True to activate the united model mode.
   */
  public FormulaFactory(boolean united_model_mode) {
    setUnitedModelModel(united_model_mode);
  };

  /**
   * Set the mode of the FormulaFactory. All variables previously created are
   * forgotten even if the model was already in the united model mode.
   * 
   * @param united_model_mode
   *          True to activate the united model mode.
   */
  public void setUnitedModelModel(boolean united_model_mode) {
    if (united_model_mode) {
      existing_variables = new HashMap<String, Variable>();
    } else {
      existing_variables = null;
    }
  }

  /**
   * @return True if the factory in in the united model mode. False otherwise.
   */
  public boolean isUnitedModelMode() {
    return existing_variables == null;
  }

  public abstract Formula parse(String expression, boolean view_tree);

  /**
   * 
   * @param expression
   * @return A valid formula if the expression contains a valid expression.
   *         null can be returned if the formula is empty.
   */
  public abstract Formula parse(String expression);

}
