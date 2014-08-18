package graph;

import abstractGraph.AbstractGlobalState;
import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.conditions.valuation.Valuation;

public class GlobalState extends
    AbstractGlobalState<StateMachine, State, Transition, Valuation> {

  public GlobalState(int number_state_machines, Valuation variables_values) {
    super(number_state_machines, variables_values);
  }

  public GlobalState(int number_state_machines) {
    this(number_state_machines, new Valuation());
  }

  @Override
  public GlobalState clone() {
    GlobalState result =
        new GlobalState(state_machines_current_state.length,
            new Valuation(this.variables_values.size()));
    super.copyTo(this, result);
    return result;
  }

  @Override
  public String toString(Iterable<StateMachine> state_machines,
      Iterable<EnumeratedVariable> variables) {
    String result = "";

    for (StateMachine state_machine : state_machines) {
      result = result + state_machine.getName()
          + " : state "
          + getState(state_machine).getId() + ".\n";
    }
    result = result + "The value of the variables are : "
        + variables_values.toString() + ".\n";
    result += "Safe: " + is_safe_state + ", legal: " + is_legal_state
        + ", isNotP7: " + isNotP7 + "\n";
    return result;
  }

}
