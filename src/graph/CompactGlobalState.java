package graph;

import abstractGraph.AbstractGlobalState;
import abstractGraph.conditions.Variable;
import abstractGraph.conditions.valuation.CompactValuation;

public class CompactGlobalState extends
    AbstractGlobalState<StateMachine, State, Transition, CompactValuation> {

  public CompactGlobalState(CompactValuation variables_values) {
    super(variables_values);
  }

  public String toString(Iterable<Variable> variables) {
    String result = "";

    for (StateMachine state_machine : state_machines_current_state.keySet()) {
      result = result + state_machine.getName()
          + " : state "
          + state_machines_current_state.get(state_machine).getId() + ".\n";
    }
    result = result + "The value of the variables are : "
        + variables_values.toString(variables) + ".\n";
    result += "Safe: " + is_safe_state + ", legal: " + is_legal_state
        + ", isNotP7: " + isNotP7 + "\n";
    return result;
  }

  @Override
  public CompactGlobalState clone() {
    CompactGlobalState result =
        new CompactGlobalState(new CompactValuation(variables_values.size()));
    super.copyTo(this, result);
    return result;
  }

}
