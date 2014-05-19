package abstractGraph;

import graph.State;
import graph.StateMachine;

import java.util.HashMap;
import java.util.Iterator;

import abstractGraph.conditions.Variable;

public abstract class AbstractGlobalState<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> {

  private HashMap<Variable, Boolean> variables = new HashMap<Variable, Boolean>();
  private HashMap<StateMachine, State> state_machines_tag =
      new HashMap<StateMachine, State>();
  /**
   * Set the active state of `machine` to S.
   * 
   * @param machine
   *          The machine to modify.
   * @param S
   *          The state to set.
   */
  public void setState(StateMachine machine, State state) {
    state_machines_tag.put(machine, state);
  }

  public State getState(StateMachine machine) {
    return state_machines_tag.get(machine);
  }

  public boolean getVariableValue(Variable variable) {
    return variables.get(variable);
  }

  /**
   * Change the value of the variable.
   * 
   * @param variable
   * @param value
   *          the new value to assign
   * @return true if the variable changed it's value, false otherwise.
   */
  public boolean setVariableValue(Variable variable, boolean value) {
    if (variables.get(variable) != value) {
      variables.put(variable, value);
      return true;
    }
    return false;
  }
  
  public Iterator<Variable> iteratorVariables(){
    return variables.keySet().iterator();
  }

}
