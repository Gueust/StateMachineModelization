package Graph;

import Graph.Conditions.Condition;

public abstract class Transition {

	public abstract boolean Eval_condition();
	public abstract int get_state_source();
	public abstract int get_state_destination();
	public abstract Condition get_condition();
	public abstract Actions get_action();
	
}
