package Graph;

import abstractGraph.AbstractTransition;
import abstractGraph.GlobalState;
import abstractGraph.Conditions.AbstractCondition;
import abstractGraph.Events.AbstractActions;
import abstractGraph.Events.Events;

public class Transition extends AbstractTransition<State> {

  public Transition(State from, State to, Events event,
      AbstractCondition condition, AbstractActions actions) {
    super(from, to, event, condition, actions);
  }

  @Override
  public boolean evalCondition(GlobalState env) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public String toString() {
    return from.getId() + " --> " + to.getId() + " -- EVENT:"
        + events.toString() +
        //" CONDITION: " + condition.toString() +
        " ACTIONS: " + actions.toString();
  }
}
