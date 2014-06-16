package engine;

import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import abstractGraph.events.ExternalEvent;

public interface GraphSimulatorInterface<GS extends AbstractGlobalState<M, S, T>, M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> {

  public GraphSimulatorInterface<GS, M, S, T> clone();

  /**
   * Execute one external event completely.
   * It MUST NOT modify the GlobalState given as parameter.
   * 
   * @param e
   *          An external event to execute.
   * @param starting_state
   *          The global state on which to run the event.
   * @return A global state being the result of the execution.
   */
  public GS execute(GS starting_state, ExternalEvent e);

  /**
   * {@inheritDoc #execute(AbstractGlobalState, ExternalEvent)} Same as
   * {@link #executeOnlyFunctional(AbstractGlobalState, ExternalEvent)} using
   * the GlobalState contained in the GraphSimulator instance.
   */
  public void execute(ExternalEvent e);
}
