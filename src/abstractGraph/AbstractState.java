package abstractGraph;

import java.util.Iterator;

import abstractGraph.Events.Event;

public abstract class AbstractState<T extends AbstractTransition> {
  protected int id;

  public AbstractState(int id) {
    this.id = id;
  }

  /**
   * @return the unique id of the state
   */
  public int getId() {
    return id;
  }

  public abstract Iterator<T> transitions();

  public abstract Iterator<T> get_transitions(Event E);
}
