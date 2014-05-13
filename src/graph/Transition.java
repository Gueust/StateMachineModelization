package graph;

import abstractGraph.AbstractTransition;
import abstractGraph.GlobalState;
import abstractGraph.conditions.AbstractCondition;
import abstractGraph.events.AbstractActions;
import abstractGraph.events.Events;

public class Transition extends AbstractTransition<State> {

  public Transition(State from, State to, Events event,
      AbstractCondition condition, AbstractActions actions) {
    super(from, to, event, condition, actions);
  }

  @Override
  public boolean evalCondition(GlobalState<State, ?> env) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public String toString() {
    return from.getId() + " --> " + to.getId() + " -- EVENT: "
        + events.toString() + "\n" +
        "              CONDITION: " + condition.toString() + "\n" +
        "              ACTIONS: " + actions.toString();
  }
}
