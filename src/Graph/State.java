package Graph;

import java.util.Iterator;
import java.util.LinkedList;

import abstractGraph.AbstractState;
import abstractGraph.Events.Event;

public class State extends AbstractState<Transition> {
  LinkedList<Transition> transitions;

  public State(int id) {
    super(id);
  }

  public void addTransition(Transition t) {
    transitions.add(t);
  }

  @Override
  public Iterator<Transition> transitions() {
    return transitions.iterator();
  }

  @Override
  public Iterator<Transition> get_transitions(Event E) {
    return null;
  }

}