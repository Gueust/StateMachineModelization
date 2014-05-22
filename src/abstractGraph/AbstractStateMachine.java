package abstractGraph;

import java.util.Iterator;
import java.util.LinkedList;

import abstractGraph.conditions.Formula;
import abstractGraph.events.Actions;
import abstractGraph.events.Events;
import abstractGraph.events.SingleEvent;

import javax.management.openmbean.KeyAlreadyExistsException;

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

  /**
   * The order of the elements is not specified.
   * 
   * @return An iterator over all the sates
   */
  public abstract Iterator<S> iteratorStates();

  /**
   * The order of the elements is not specified.
   * 
   * @return An iterator over all the transitions
   */
  public abstract Iterator<T> iteratorTransitions();

  /**
   * Return the transitions that have the event E in the event field.
   * @param E
   * @return
   */
  public abstract LinkedList<T> getTransition(SingleEvent E);

  /**
   * Add a transition to a state machine
   * 
   * @param from
   *          The source of the transition
   * @param to
   *          The destination of the transition
   * @param events
   *          The event (eventually null) of the transition
   * @param guard
   *          The guard of the transition
   * @param actions
   *          The actions
   */
  public abstract T addTransition(S from, S to,
      Events events,
      Formula guard, Actions actions);

  /**
   * Add a new state to the state machine, and throws an exception if the state
   * already exists.
   * 
   * @param state_name
   *          The name of the state to create
   * @return The new state created.
   */
  public abstract S addState(String state_name)
      throws KeyAlreadyExistsException;

  /**
   * Return the state entitled `state_name`.
   * 
   * @param state_name
   * @return The state associated to the name if it exists. null otherwise.
   */
  public abstract S getState(String state_name);

}
