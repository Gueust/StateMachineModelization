package domainSpecificLanguage.DSLGlobalState;

import domainSpecificLanguage.graph.DSLState;
import domainSpecificLanguage.graph.DSLStateMachine;
import domainSpecificLanguage.graph.DSLTransition;
import abstractGraph.AbstractGlobalState;
import abstractGraph.conditions.EnumeratedVariable;

public class DSLGlobalState
    extends
    AbstractGlobalState<DSLStateMachine, DSLState, DSLTransition, CompactValuation> {

  public DSLGlobalState(int number_state_machines,
      CompactValuation variables_values) {
    super(number_state_machines, variables_values);
  }

  public DSLGlobalState(int number_state_machines, int nb_variables) {
    this(number_state_machines, new CompactValuation(nb_variables));
  }

  @Override
  public DSLGlobalState clone() {
    DSLGlobalState result =
        new DSLGlobalState(state_machines_current_state.length,
            new CompactValuation(this.variables_values.size()));
    super.copyTo(this, result);
    return result;
  }

  @Override
  public String toString(Iterable<DSLStateMachine> state_machines,
      Iterable<EnumeratedVariable> variables) {
    String result = "";

    for (DSLStateMachine state_machine : state_machines) {
      result = result + state_machine.getName()
          + " : state "
          + getState(state_machine).getId() + ".\n";
    }
    result = result + "The value of the variables are : "
        + variables_values.toString(variables) + ".\n";
    result += "Safe: " + is_safe_state + ", legal: " + is_legal_state
        + ", isNotP7: " + isNotP7 + "\n";
    return result;
  }

}
