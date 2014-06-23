package graph;

import abstractGraph.AbstractGlobalState;
import abstractGraph.conditions.valuation.Valuation;

public class GlobalState extends
    AbstractGlobalState<StateMachine, State, Transition, Valuation> {

  public GlobalState(Valuation variables_values) {
    super(variables_values);
  }

  public GlobalState() {
    super(new Valuation());
  }

  /**
   * Clear the AbstractGlobalState: it is as new as a new instance.
   */
  public void clear() {
    variables_values.clear();
    state_machines_current_state.clear();
    is_legal_state = true;
    is_safe_state = true;
  }

  @Override
  public GlobalState clone() {
    GlobalState result =
        new GlobalState(new Valuation(this.variables_values.size()));
    super.copyTo(this, result);
    return result;
  }

}
