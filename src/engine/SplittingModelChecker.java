package engine;

import java.util.Collection;

import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;

public class SplittingModelChecker<GS extends AbstractGlobalState<M, S, T, ?>, M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> {

  private ModelChecker<GS, M, S, T> model_checker;

  public SplittingModelChecker() {
    super();
    this.model_checker = new ModelChecker<GS, M, S, T>();
  }

  /**
   * @see ModelChecker#addAllInitialStates(Collection)
   */
  public void addAllInitialStates(Collection<GS> init) {
    model_checker.addAllInitialStates(init);
  }

  /**
   * @see ModelChecker#addInitialState(AbstractGlobalState)
   */
  public void addInitialState(GS init) {
    model_checker.addInitialState(init);
  }

  /**
   * Launch the proof trying to separate the proofs.
   * 
   * {@see ModelChecker#verify(GraphSimulatorInterface)}
   * 
   */
  public GS verify(GraphSimulatorInterface<GS, M, S, T> simulator) {

    BuildActivationGraph<M, S, T> split_engine = new BuildActivationGraph<>(simulator);

    split_engine.printToImage("tmp");

    return null;
  }

}
