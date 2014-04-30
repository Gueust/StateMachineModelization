package Graph.Conditions;

import abstractGraph.GlobalState;
import abstractGraph.Conditions.AbstractCondition;

public class Condition extends AbstractCondition {

  String condition;

  public Condition(String condition) {
    super(condition);
    this.condition = condition;
  }

  @Override
  public boolean eval(GlobalState valuation) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * Returns the AEFDFormat
   */
  @Override
  public String toString() {
    return condition;
  }
}
