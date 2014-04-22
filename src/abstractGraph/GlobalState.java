package abstractGraph;

import abstractGraph.Conditions.AbstractVariable;

public abstract class GlobalState {

  /**
   * Set the active state of machine to S
   * 
   * @param machine
   *          The machine to modify
   * @param S
   *          The state to set
   */
  public abstract void setState(AbstractStateMachine machine, AbstractState S);

  public abstract AbstractState getState(AbstractStateMachine machine);

  public abstract boolean getVariableValue(AbstractVariable v);

}
