package abstractGraph;

import abstractGraph.Conditions.Condition;
import abstractGraph.Events.AbstractActions;
import abstractGraph.Events.Event;

public abstract class AbstractTransition<S extends AbstractState> {
  protected S from, to;
  protected Event event;
  protected Condition condition;
  protected AbstractActions actions;

  public AbstractTransition(S from, S to, Event event,
      Condition condition, AbstractActions actions) {
    this.from = from;
    this.to = to;
    this.event = event;
    this.condition = condition;
    this.actions = actions;
  }

  public abstract boolean evalCondition(GlobalState env);

  public S getSource() {
    return from;
  }

  public S getDestination() {
    return to;
  }

  public Event getEvent() {
    return event;
  }

  public Condition getCondition() {
    return condition;
  }

  public AbstractActions getActions() {
    return actions;
  }
}
