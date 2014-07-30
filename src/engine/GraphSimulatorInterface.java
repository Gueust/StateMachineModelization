package engine;

import java.util.LinkedHashSet;

import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractModel;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import abstractGraph.events.ExternalEvent;

public interface GraphSimulatorInterface<GS extends AbstractGlobalState<M, S, T, ?>, M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> {

  /** The initial event given to the machine to force its initialization */
  public static final ExternalEvent ACT_INIT = new ExternalEvent("ACT_Init");

  public AbstractModel<M, S, T> getModel();

  public AbstractModel<M, S, T> getProof();

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
   * 
   * @return An iterable over the external events that the simulator can eat
   *         from the current states of the state machines.
   */
  public LinkedHashSet<ExternalEvent> getPossibleEvent(GS global_state);

  /**
   * 
   * @param global_state
   *          A global state fitting the current graph simulator.
   * @return A string description of the given global_state.
   */
  public String globalStateToString(GS global_state);

}
