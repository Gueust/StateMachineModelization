package abstractEngine;

import java.util.LinkedList;

import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import abstractGraph.events.ExternalEvent;

public interface AbstractGraphSimulator<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> {

  public void eat(LinkedList<ExternalEvent> l);

  public AbstractGlobalState<M, S, T> execute(ExternalEvent e);

  public AbstractGlobalState<M, S, T> execute(
      AbstractGlobalState<M, S, T> starting_state,
      ExternalEvent e);

  /**
   * 
   * @return true if a safety property is not verified
   */
  public boolean isP5();

  /**
   * 
   * @return true if a predicate is not ensure.
   */
  public boolean isP6();

  /**
   * 
   * @return true if a functionality is not ensured.
   */
  public boolean isP7();

}
