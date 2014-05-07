package abstractGraph.conditions;

import abstractGraph.GlobalState;

public interface AbstractCondition {

  public abstract boolean eval(GlobalState valuation);
}
