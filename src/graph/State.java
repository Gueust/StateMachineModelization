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

  public void addTransition(Transition t) {
    transitions.add(t);
  }

  @Override
  public Iterator<Transition> iteratorTransitions() {
    return transitions.iterator();
  }

  @Override
  public Iterator<Transition> iteratorTransitions(SingleEvent E) {
    return null;
  }

  @Override
  public boolean equals(Object obj) {
    State s2 = (State) obj;
    return getId().equals(s2.getId());
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("STATE: " + getId() + "\n");
    Iterator<Transition> transitions_iterator = iteratorTransitions();
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
