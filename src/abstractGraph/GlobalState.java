package abstractGraph;

import abstractGraph.conditions.Variable;

public abstract class GlobalState<S extends AbstractState<T>, T extends AbstractTransition<S>> {

  /**
   * Set the active state of `machine` to S.
   * 
   * @param machine
   *          The machine to modify.
   * @param S
   *          The state to set.
   */
  public abstract void setState(AbstractStateMachine<S, T> machine,
      AbstractState<T> S);

  public abstract AbstractState<T> getState(AbstractStateMachine<S, T> machine);

  public abstract boolean getVariableValue(Variable v);

}
