package Graph;

import java.util.Iterator;

import Graph.Events.Event;

public abstract class State_Machine {

	public abstract void update_token_place(State S);
	public abstract State get_current_state();
	public abstract Iterator<Transition> get_transition();
	public abstract Iterator<Transition> get_transition(Event E);
}
