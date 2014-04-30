package abstractGraph.Conditions;

import abstractGraph.GlobalState;

public abstract class AbstractCondition {

  public AbstractCondition(String condition) {
  }

  public abstract boolean eval(GlobalState valuation);
}
