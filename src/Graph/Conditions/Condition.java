package Graph.Conditions;

import Graph.GlobalState;

public abstract class Condition {

	public abstract boolean eval(GlobalState valuation);
}
