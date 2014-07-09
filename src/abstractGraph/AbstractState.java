package abstractGraph;

import java.util.Iterator;

import domainSpecificLanguage.graph.DSLTransition;
import abstractGraph.events.SingleEvent;

public abstract class AbstractState<T extends AbstractTransition<? extends AbstractState<T>>>
    implements Iterable<T> {
  protected String id;

  public AbstractState(String id) {
    this.id = id;
  }

  /**
   * @return the unique id of the state
   */
  public String getId() {
    return id;
  }

  @Override
  public abstract Iterator<T> iterator();

  /**
   * 
   * @return An array containing all the transitions of the state.
   */
  public abstract T[] toArray();

  public abstract Iterator<T> iteratorTransitions(SingleEvent E);

  public abstract void addTransition(T t);

}
