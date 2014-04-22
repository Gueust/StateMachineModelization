package abstractGraph;

import java.util.Iterator;

import abstractGraph.Conditions.Condition;
import abstractGraph.Events.AbstractActions;
import abstractGraph.Events.Event;

public abstract class AbstractStateMachine {

  protected String name;

  public AbstractStateMachine(String name) {
    this.name = name;
  }

  /**
   * The order of the elements is not specified.
   * 
   * @return An iterator over all the sates
   */
  public abstract Iterator<AbstractState> states();

  /**
   * The order of the elements is not specified.
   * 
   * @return An iterator over all the transitions
   */
  public abstract Iterator<AbstractTransition> transitions();

  public abstract Iterator<AbstractTransition> get_transition(Event E);

  /**
   * Add a transition to a state machine
   * 
   * @param from
   *          The source of the transition
   * @param to
   *          The destination of the transition
   * @param event
   *          The event (eventually null) of the transition
   * @param guard
   *          The guard of the transition
   * @param actions
   *          The actions
   */
  public abstract void addTransition(AbstractState from, AbstractState to,
      Event event,
      Condition guard, AbstractActions actions);

  public String getName() {
    return name;
  }
}
