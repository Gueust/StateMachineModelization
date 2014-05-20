package abstractGraph;

import graph.Model;
import graph.State;
import graph.StateMachine;

import java.util.HashMap;
import java.util.Iterator;

import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil.ToStringAdapter;

import abstractGraph.conditions.Valuation;
import abstractGraph.conditions.Variable;

public class AbstractGlobalState<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> {

  private Valuation variables_values;
  private HashMap<StateMachine, State> state_machines_currenst_state =
      new HashMap<StateMachine, State>();

  public AbstractGlobalState(Model model) {
    variables_values = new Valuation(model);
  }

  public AbstractGlobalState() {
    variables_values = new Valuation();
  }

  /**
   * Set the active state of `machine` to S.
   * 
   * @param machine
   *          The machine to modify.
   * @param S
   *          The state to set.
   */
  public void setState(StateMachine machine, State state) {
    state_machines_currenst_state.put(machine, state);
  }

  public State getState(StateMachine machine) {
    return state_machines_currenst_state.get(machine);
  }

  public boolean getVariableValue(Variable variable) {
    return variables_values.getValue(variable);
  }

  /**
   * Change the value of the variable.
   * 
   * @param variable
   * @param value
   *          the new value to assign
   * @return true if the variable changed its value, false otherwise.
   */
  public boolean setVariableValue(Variable variable, boolean value) {
    return variables_values.setValue(variable, value);
  }

  /**
   * @return the environment with the value of the variables.
   */
  public Valuation getValuation() {
    return variables_values;
  }

  @Override
  public String toString() {
    String result = "";
    Iterator<StateMachine> state_machine_iterator = state_machines_currenst_state
        .keySet()
        .iterator();
    while (state_machine_iterator.hasNext()) {
      StateMachine state_machine = state_machine_iterator.next();
      result = result + state_machine.getName()
          + " : state "
          + state_machines_currenst_state.get(state_machine).getId() + ".\n";
    }
    result = result + "The value of the variables are : "
        + variables_values.toString() + ".\n";
    return result;
  }

}
