package abstractGraph;


import graph.State;
import graph.StateMachine;
import graph.Transition;
import graph.conditions.aefdParser.AEFDFormulaFactory;

import java.util.HashMap;
import java.util.Iterator;

import abstractGraph.conditions.Valuation;
import abstractGraph.conditions.Variable;
import abstractGraph.events.VariableChange;

public class AbstractGlobalState<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> {

  private Valuation variables_values;
  private HashMap<StateMachine, State> state_machines_current_state =
      new HashMap<StateMachine, State>();

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
    state_machines_current_state.put(machine, state);
  }

  /**
   * Return the current state of a state machine.
   * 
   * @param machine
   * @return the current state if the state machine exists, null otherwise.
   */
  public State getState(StateMachine machine) {
    return state_machines_current_state.get(machine);
  }

  /**
   * Search for a state machine by name and return its current state.
   * 
   * @param state_machine_name
   * @return the current state if the state machine exists, null otherwise.
   */
  public State getState(String state_machine_name) {
    Iterator<StateMachine> state_machine_iterator = state_machines_current_state
        .keySet()
        .iterator();
    while (state_machine_iterator.hasNext()) {
      StateMachine state_machine = state_machine_iterator.next();
      if (state_machine.getName().equals(state_machine_name)) {
        return state_machines_current_state.get(state_machine);
      }
    }
    return null;
  }

  /**
   * 
   * @param variable
   * @return the value of the variable
   */
  public boolean getVariableValue(Variable variable) {
    return variables_values.getValue(variable);
  }

  /**
   * Search a variable by its name and gives its value
   * 
   * @param variable_name
   * @return
   */
  public boolean getVariableValue(String variable_name) {
    AEFDFormulaFactory factory = new AEFDFormulaFactory(true);
    VariableChange new_event = new VariableChange(factory
        .getLiteral(variable_name));
    Variable variable = new_event.getModifiedVariable();
    System.out.print(variable.toString() + " ******************** \n");
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
    Iterator<StateMachine> state_machine_iterator = state_machines_current_state
        .keySet()
        .iterator();
    while (state_machine_iterator.hasNext()) {
      StateMachine state_machine = state_machine_iterator.next();
      result = result + state_machine.getName()
          + " : state "
          + state_machines_current_state.get(state_machine).getId() + ".\n";
    }
    result = result + "The value of the variables are : "
        + variables_values.toString() + ".\n";
    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AbstractGlobalState<StateMachine, State, Transition> other = (AbstractGlobalState<StateMachine, State, Transition>) obj;
    if (state_machines_current_state == null) {
      if (other.state_machines_current_state != null)
        return false;
    } else if (!state_machines_current_state
        .equals(other.state_machines_current_state))
      return false;
    if (variables_values == null) {
      if (other.variables_values != null)
        return false;
    } else if (!variables_values.equals(other.variables_values))
      return false;
    return true;
  }

}
