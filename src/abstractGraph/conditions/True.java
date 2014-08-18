package abstractGraph.conditions;

import java.util.HashSet;

import abstractGraph.conditions.valuation.AbstractValuation;

/**
 * @brief
 *        An always true formula.
 * @details
 *          This class cannot be instantiated by the user. One should use
 *          {@link Formula#TRUE} to get the unique instance of the true formula.
 * 
 */
public class True extends Formula {

  /**
   * No one should be able to create an instance of this class.
   */
  True() {
  }

  @Override
  public boolean eval(AbstractValuation valuation) {
    return true;
  }

  @Override
  public HashSet<EnumeratedVariable> allVariables(
      HashSet<EnumeratedVariable> vars) {
    return vars;
  }

  @Override
  public String toString() {
    return "true";
  }
}
