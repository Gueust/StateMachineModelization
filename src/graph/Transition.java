package graph;

import abstractGraph.AbstractTransition;
import abstractGraph.AbstractGlobalState;
import abstractGraph.conditions.Formula;
import abstractGraph.events.Actions;
import abstractGraph.events.Events;

public class Transition extends AbstractTransition<State> {

  public Transition(State from, State to, Events event,
      Formula condition, Actions actions) {
    super(from, to, event, condition, actions);
  }

  // TODO verify that it works and add the tests.
  @Override
  public boolean evalCondition(AbstractGlobalState<?, State, ?> global_state) {
    if (condition == null) {
      return true;
    }
    return condition.eval(global_state.getValuation());
  }

  @Override
  public String toString() {

    return from.getId() + " --> " + to.getId() + " -- EVENT: "
        + events.toString() + "\n" +
        "              CONDITION: "
        + ((condition != null) ? condition.toString() : " Empty") + "\n" +
        "              ACTIONS: " + actions.toString();

  }

}
