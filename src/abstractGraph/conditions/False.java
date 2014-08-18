package abstractGraph.conditions;

import java.util.HashSet;

import abstractGraph.conditions.valuation.AbstractValuation;

/**
 * @brief
 *        An always false formula.
 * 
 *        This class cannot be instantiated by the user. One should use
 *        {@link Formula#FALSE} to get the unique instance of the false formula.
 * 
 */
public class False extends Formula {

  /**
   * No one should be able to create an instance of this class.
   */
  False() {
  }

  @Override
  public boolean eval(AbstractValuation valuation) {
    return false;
  }

  @Override
  public HashSet<EnumeratedVariable> allVariables(
      HashSet<EnumeratedVariable> vars) {
    return vars;
  }

  @Override
  public String toString() {
    return "false";
  }
}
