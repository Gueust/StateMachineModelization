package Graph;

import java.util.Iterator;
import java.util.LinkedList;

import abstractGraph.AbstractState;
import abstractGraph.AbstractTransition;
import abstractGraph.Events.Event;

public class State extends AbstractState {
  LinkedList<AbstractTransition> transitions;

  public State(int id) {
    super(id);
  }

  public void addTransition(Transition t) {
    transitions.add(t);
  }

  @Override
  public Iterator<AbstractTransition> transitions() {
    return transitions.iterator();
  }

  @Override
  public Iterator<AbstractTransition> get_transitions(Event E) {
    return null;
  }

}
