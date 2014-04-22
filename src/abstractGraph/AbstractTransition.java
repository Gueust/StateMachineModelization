package abstractGraph;

import abstractGraph.Conditions.Condition;
import abstractGraph.Events.Event;

public abstract class AbstractTransition {
  protected AbstractState from, to;
  protected Event event;
  protected Condition condition;
  protected Actions actions;

  public AbstractTransition(AbstractState from, AbstractState to, Event event,
      Condition condition, Actions actions) {
    this.from = from;
    this.to = to;
    this.event = event;
    this.condition = condition;
    this.actions = actions;
  }

  public abstract boolean evalCondition(GlobalState env);

  public AbstractState getSource() {
    return from;
  }

  public AbstractState getDestination() {
    return to;
  }

  public Event getEvent() {
    return event;
  }

  public Condition getCondition() {
    return condition;
  }

  public Actions getActions() {
    return actions;
  }
}
