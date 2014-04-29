package abstractGraph;

import abstractGraph.Conditions.Condition;
import abstractGraph.Events.AbstractActions;
import abstractGraph.Events.Events;

public abstract class AbstractTransition<S extends AbstractState<? extends AbstractTransition<S>>> {
  protected S from, to;
  protected Events event;
  protected Condition condition;
  protected AbstractActions actions;

  public AbstractTransition(S from, S to, Events events,
      Condition condition, AbstractActions actions) {
    this.from = from;
    this.to = to;
    this.event = events;
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

  public Events getEvent() {
    return event;
  }

  public Condition getCondition() {
    return condition;
  }

  public AbstractActions getActions() {
    return actions;
  }
}
