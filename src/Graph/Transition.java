package Graph;

import abstractGraph.AbstractTransition;
import abstractGraph.GlobalState;
import abstractGraph.Conditions.Condition;
import abstractGraph.Events.AbstractActions;
import abstractGraph.Events.Event;

public class Transition extends AbstractTransition<State> {

  public Transition(State from, State to, Event event,
      Condition condition, AbstractActions actions) {
    super(from, to, event, condition, actions);
  }

  @Override
  public boolean evalCondition(GlobalState env) {
    // TODO Auto-generated method stub
    return false;
  }
}
