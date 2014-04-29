package abstractGraph;

import java.util.Iterator;

import abstractGraph.Events.SingleEvent;

public abstract class AbstractState<T extends AbstractTransition<? extends AbstractState<T>>> {
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

  public abstract Iterator<T> transitions();

  public abstract Iterator<T> get_transitions(SingleEvent E);
}
