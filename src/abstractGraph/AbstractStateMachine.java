package abstractGraph;

import java.util.Iterator;
import java.util.LinkedList;

import javax.management.openmbean.KeyAlreadyExistsException;

import abstractGraph.conditions.Formula;
import abstractGraph.events.Actions;
import abstractGraph.events.Events;
import abstractGraph.events.SingleEvent;

/**
 * 
 * @author 9009183R
 * 
 * @param <S>
 *          A state class extending AbstractState
 * @param <T>
 */
public abstract class AbstractStateMachine<S extends AbstractState<T>, T extends AbstractTransition<S>>
    implements Iterable<S> {

  protected String state_machine_name;
  /* The unique identifier of the Variable throughout a simulator. */
  protected int identifier;

  /**
   * 
   * @param name
   * @param identifier
   *          The identifier for the state machine. It should be unique
   *          throughout a simulator (both functional and proof model).
   */
  public AbstractStateMachine(String name, int identifier) {
    this.state_machine_name = name;
    this.identifier = identifier;
  }

  public String getName() {
    return state_machine_name;
  }

  public int getUniqueIdentifier() {
    return identifier;
  }

  /**
   * Only users knowing how to keep data integrity should use this function.
   * 
   * @param value
   */
  public void setUniqueIdentifier(int value) {
    identifier = value;
  }

  /**
   * The order of the elements is not specified.
   * 
   * @return An iterator over all the sates
   */
  public abstract Iterator<S> iterator();

  /**
   * The order of the elements is not specified.
   * 
   * @return An iterator over all the transitions
   */
  public abstract Iterator<T> iteratorTransitions();

  /**
   * Return the transitions that have the event E in the event field.
   * 
   * @param E
   * @return
   */
  public abstract LinkedList<T> getTransitions(SingleEvent E);

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
   * Add a new state to the state machine, and throws an exception if the
   * state already exists.
   * 
   * @param state
   *          The state to add
   */
  public abstract void addState(S state)
      throws KeyAlreadyExistsException;

  /**
   * Return the state entitled `state_name`.
   * 
   * @param state_name
   * @return The state associated to the name if it exists. null otherwise.
   */
  public abstract S getState(String state_name);

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((state_machine_name == null) ? 0 : state_machine_name.hashCode());
    return result;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AbstractStateMachine other = (AbstractStateMachine) obj;
    if (state_machine_name == null) {
      if (other.state_machine_name != null)
        return false;
    } else if (!state_machine_name.equals(other.state_machine_name))
      return false;
    return true;
  }
}
