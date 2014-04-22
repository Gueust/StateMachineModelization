package Graph;

import java.util.Iterator;

import Graph.Events.Event;

public abstract class State {

	public abstract Iterator<Transition> get_transitions();
	public abstract Iterator<Transition> get_transitions(Event E);
	public abstract int get_ID();
}
