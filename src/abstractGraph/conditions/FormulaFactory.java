package abstractGraph.conditions;

import java.util.HashMap;
import java.util.Iterator;

/**
 * All formula factories should be able to parse a string and return a valid
 * Formula.
 * 
 * A factory is able to work in two modes:
 * <ol>
 * <li>
 * A single formula mode, that allows to create multiple not related formulas.
 * Variables will be memorized by the FormulaFactory until the next formula
 * parsing.
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
 * 
 * To create an implementation (i.e. using the key word "extends
 * FormulaFactory) one need :
 * - to override {@link #parse(String, boolean)} and the first action of the
 * implementation must be {@link #reset()}.
 * 
 * The modes (single formula and united model) are managed by this abstract
 * FormulaFactory class. Just use {@link #getVariable(String)} to retrieve
 * a variable.
 */
public abstract class FormulaFactory {

  private boolean united_model;
  private HashMap<String, Variable> existing_variables;
  /* Set the unique id for all variables */
  private int variable_id = 0;

  /**
   * Initialize the FormulaFactory in the single formula mode.
   */
  public FormulaFactory() {
    this(false);
  };

  /**
   * Initialize the FormulaFactory to be in the united model mode.
   * All the previous registered variables will be forgotten.
   * 
   * @param united_model_mode
   *          True to activate the united model mode.
   */
  public FormulaFactory(boolean united_model_mode) {
    this.united_model = united_model_mode;
    existing_variables = new HashMap<String, Variable>();
  };

  /**
   * Set the mode of the FormulaFactory. All variables previously created are
   * forgotten even if the model was already in the united model mode.
   * 
   * @param united_model_mode
   *          True to activate the united model mode.
   */
  public void setUnitedModelModel(boolean united_model_mode) {
    this.united_model = united_model_mode;
    existing_variables = new HashMap<String, Variable>();

  }

  /**
   * Return a variable named `variable_name`. If the variable already exists it
   * is returned.
   * In particular, in the single formula mode, it exists iff it was used in the
   * last parsed formula. In the united model mode, it exists iff it was used in
   * a previous formula. It is created otherwise.
   * 
   * @param variable_name
   * @return The associated variable
   */
  public Variable getVariable(String variable_name) {
    Variable temp_variable = existing_variables.get(variable_name);
    if (temp_variable == null) {
      temp_variable = new Variable(variable_name);
      temp_variable.setIdentifier(variable_id);
      variable_id++;
      existing_variables.put(variable_name, temp_variable);
    }

    return temp_variable;
  }

  /**
   * Remove a variable from the factory. Calling this function may break the
   * factory. Only
   * 
   * @param variable_name
   */
  public void removeVariable(String variable_name) {
    existing_variables.remove(variable_name);
  }

  /**
   * Allow to know whether a variable exists in the FormulaFactory or not.
   * 
   * @param variable_name
   *          The variable name to look for.
   * @return true if the variable_name exists. false otherwise.
   */
  public boolean contains(String variable_name) {
    return existing_variables.containsKey(variable_name);
  }

  /**
   * {@inheritDoc #contains(String)}
   * 
   * @param variable
   *          The variable to look for.
   * @return true if the variable exists. false otherwise.
   */
  public boolean contains(Variable variable) {
    return existing_variables.containsKey(variable.toString());
  }

  /**
   * @return True if the factory in in the united model mode. False otherwise.
   */
  public boolean isUnitedModelMode() {
    return united_model;
  }

  /**
   * Reset the factory to be ready for a new parsing.
   * 
   * @details It creates a new environment to save the variables if we are in
   *          the single formula mode. If we are in the united model mode, does
   *          nothing.
   */
  public void reset() {
    if (!united_model) {
      existing_variables = new HashMap<String, Variable>();
    }
  }

  /**
   * This function must be implemented.
   * The first action should be {@link #reset()}.
   * 
   * @param expression
   * @param view_tree
   *          A debug value. True should display the parsed tree.
   *          Using ANTLR, one should put this code before returning:
   * 
   *          <pre> {@code if (view_tree)
   *          TreeViewer viewer = new TreeViewer(null, tree);
   *          viewer.open();
   *          } </pre>
   * @return A valid formula if the expression contains a valid expression.
   *         null can be returned if the formula is empty.
   */
  public abstract Formula parse(String expression, boolean view_tree);

  /**
   * {@inheritDoc #parse(String, boolean)}
   * 
   * @details Equivalent to {@code parse(expression, false)}.
   */
  public final Formula parse(String expression) {
    return parse(expression, false);
  }

  /**
   * Iterate the variables that are contained in the conditions.
   * 
   * @return
   */
  public Iterator<Variable> iteratorExistingVariables() {
    return existing_variables.values().iterator();
  }

}
