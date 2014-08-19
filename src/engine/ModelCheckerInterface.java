package engine;

import java.io.IOException;
import java.util.Collection;

import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;

public interface ModelCheckerInterface<GS extends AbstractGlobalState<M, S, T, ?>, M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> {

  public void addInitialState(GS init);

  public void addAllInitialStates(Collection<GS> init);

  public GS verify(GraphSimulatorInterface<GS, M, S, T> simulator)
      throws Exception;
}
