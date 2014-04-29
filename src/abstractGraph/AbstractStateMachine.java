package abstractGraph;

import java.util.Iterator;

import abstractGraph.Conditions.Condition;
import abstractGraph.Events.AbstractActions;
import abstractGraph.Events.Event;

/**
 * 
 * @author 9009183R
 * 
 * @param <S>
 *          A state class extending AbstractState
 * @param <T>
 */
public abstract class AbstractStateMachine<S extends AbstractState<T>, T extends AbstractTransition<S>> {

  protected String name;

  public AbstractStateMachine(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public abstract S getState(String name);

  /**
   * The order of the elements is not specified.
   * 
   * @return An iterator over all the sates
   */
  public abstract Iterator<S> states();

  /**
   * The order of the elements is not specified.
   * 
   * @return An iterator over all the transitions
   */
  public abstract Iterator<T> transitions();

  public abstract Iterator<T> get_transition(Event E);

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
  public abstract void addTransition(S from, S to,
      Event event,
      Condition guard, AbstractActions actions);

}
