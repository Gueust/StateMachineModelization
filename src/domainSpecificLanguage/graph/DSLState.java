package domainSpecificLanguage.graph;

import java.util.Iterator;
import java.util.LinkedList;

import abstractGraph.AbstractState;
import abstractGraph.events.SingleEvent;

public class DSLState extends AbstractState<DSLTransition> {

  LinkedList<DSLTransition> transitions = new LinkedList<>();

  public DSLState(String id) {
    super(id);
  }

  @Override
  public Iterator<DSLTransition> iterator() {
    return transitions.iterator();
  }

  @Override
  public Iterator<DSLTransition> iteratorTransitions(SingleEvent event) {
    // TODO Improve the memory efficiency of the method
    LinkedList<DSLTransition> list = new LinkedList<>();
    for (DSLTransition transition : transitions) {
      if (transition.getEvents().containsEvent(event)) {
        list.add(transition);
      }
    }
    return list.iterator();
  }

  @Override
  public void addTransition(DSLTransition t) {
    transitions.add(t);
  }

  @Override
  public DSLTransition[] toArray() {
    DSLTransition[] t = new DSLTransition[transitions.size()];
    return transitions.toArray(t);
  }

  @Override
  public String toString() {
    return getId();
  }

}
