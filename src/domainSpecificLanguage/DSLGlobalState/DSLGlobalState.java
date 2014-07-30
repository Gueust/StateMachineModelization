package domainSpecificLanguage.DSLGlobalState;

import domainSpecificLanguage.graph.DSLState;
import domainSpecificLanguage.graph.DSLStateMachine;
import domainSpecificLanguage.graph.DSLTransition;
import abstractGraph.AbstractGlobalState;
import abstractGraph.conditions.EnumeratedVariable;

public class DSLGlobalState
    extends
    AbstractGlobalState<DSLStateMachine, DSLState, DSLTransition, CompactValuation> {

  public DSLGlobalState(CompactValuation variables_values) {
    super(variables_values);
  }

  public DSLGlobalState(int nb_variables) {
    super(new CompactValuation(nb_variables));
  }

  /**
   * Clear the AbstractGlobalState: it is as new as a new instance.
   */
  public void clear() {
    state_machines_current_state.clear();
    is_legal_state = true;
    is_safe_state = true;
  }

  @Override
  public DSLGlobalState clone() {
    DSLGlobalState result =
        new DSLGlobalState(new CompactValuation(this.variables_values.size()));
    super.copyTo(this, result);
    return result;
  }

  public String toString(Iterable<EnumeratedVariable> variables) {
    String result = "";

    for (DSLStateMachine state_machine : state_machines_current_state.keySet()) {
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

}
