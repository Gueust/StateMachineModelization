package Graph;

import abstractGraph.AbstractState;
import abstractGraph.AbstractTransition;
import abstractGraph.GlobalState;
import abstractGraph.Conditions.Condition;
import abstractGraph.Events.AbstractActions;
import abstractGraph.Events.Event;

public class Transition extends AbstractTransition {

  public Transition(AbstractState from, AbstractState to, Event event,
      Condition condition, AbstractActions actions) {
    super(from, to, event, condition, actions);
  }

  @Override
  public boolean evalCondition(GlobalState env) {
    // TODO Auto-generated method stub
    return false;
  }
}
