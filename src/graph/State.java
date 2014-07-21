package graph;

import java.util.Iterator;
import java.util.LinkedList;

import abstractGraph.AbstractState;
import abstractGraph.events.SingleEvent;

public class State extends AbstractState<Transition> {
  LinkedList<Transition> transitions;

  public State(String id) {
    super(id);
    transitions = new LinkedList<Transition>();
  }

  @Override
  public void addTransition(Transition t) {
    transitions.add(t);
  }

  @Override
  public Iterator<Transition> iterator() {
    return transitions.iterator();
  }

  @Override
  public Iterator<Transition> iteratorTransitions(SingleEvent event) {
    LinkedList<Transition> transition_event = new LinkedList<>();
    for (Transition transition : transitions) {

      if (transition.getEvents().containsEvent(event)) {
        transition_event.add(transition);
      }
    }
    return transition_event.iterator();
  }

  @Override
  public boolean equals(Object obj) {
    State s2 = (State) obj;
    return getId().equals(s2.getId());
  }

  @Override
  public int hashCode() {
    return transitions.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("STATE: " + getId() + "\n");
    Iterator<Transition> transitions_iterator = iterator();
    while (transitions_iterator.hasNext()) {
      Transition t = transitions_iterator.next();
      sb.append(" ¤ " + t.toString() + "\n");
    }
    return sb.toString();
  }

  @Override
  public Transition[] toArray() {
    Transition[] t = new Transition[transitions.size()];
    return transitions.toArray(t);
  }

}
