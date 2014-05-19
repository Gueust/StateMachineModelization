package abstractGraph;

import abstractGraph.conditions.Variable;

public interface AbstractGlobalState<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> {

  /**
   * Set the active state of `machine` to S.
   * 
   * @param machine
   *          The machine to modify.
   * @param S
   *          The state to set.
   */
  public abstract void setState(M machine, S state);

  public abstract S getState(M machine);

  public abstract boolean getVariableValue(Variable variable);

  /**
   * Change the value of the variable.
   * 
   * @param variable
   * @param value
   *          the new value to assign
   * @return true if the variable changed it's value, false otherwise.
   */
  public abstract boolean setVariableValue(Variable variable, boolean value);

}
