package graph;

import java.util.HashMap;

import abstractGraph.AbstractGlobalState;
import abstractGraph.conditions.Valuation;

public class GlobalState extends
    AbstractGlobalState<StateMachine, State, Transition> {

  @SuppressWarnings("unchecked")
  @Override
  public GlobalState clone() {
    GlobalState result = new GlobalState();
    result.state_machines_current_state =
        (HashMap<StateMachine, State>) state_machines_current_state.clone();
    result.variables_values = (Valuation) this.variables_values.clone();
    return result;
  }

}
