package domainSpecificLanguage.graph;

import java.util.Iterator;
import java.util.LinkedList;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
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
  public Iterator<DSLTransition> iteratorTransitions(SingleEvent E) {
    // TODO Auto-generated method stub
    throw new NotImplementedException();
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

}
