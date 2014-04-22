package abstractGraph;

import java.util.Iterator;

import abstractGraph.Events.Event;

public abstract class AbstractState {
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

  public abstract Iterator<AbstractTransition> transitions();

  public abstract Iterator<AbstractTransition> get_transitions(Event E);
}
