package abstractEngine;

import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import abstractGraph.events.ExternalEvent;

public interface AbstractGraphSimulator<GS extends AbstractGlobalState<M, S, T>, M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> {

  /**
   * Execute one external event completely.
   * It does modify the GlobalState given as parameter.
   * 
   * @param e
   *          An external event to execute.
   * @param global_state
   *          The global state on which to run the event.
   * @return A global state being the result of the execution. It is the same as
   *         the parameter.
   */
  public GS execute(GS starting_state, ExternalEvent e);

  /**
   * {@inheritDoc #execute(AbstractGlobalState, ExternalEvent)} Same as
   * {@link #executeOnlyFunctional(AbstractGlobalState, ExternalEvent)} using the
   * GlobalState contained in the GraphSimulator instance.
   * 
   * It does modify the internal GlobalState, meaning that several calls to this
   * function will always return the same pointer to the same GlobalState.
   * 
   * @return The internal GlobalState, that has been modified by the execution.
   */
  public GS execute(ExternalEvent e);

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
