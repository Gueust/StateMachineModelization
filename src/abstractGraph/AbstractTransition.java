package abstractGraph;

import abstractGraph.conditions.Formula;
import abstractGraph.events.AbstractActions;
import abstractGraph.events.Events;

public abstract class AbstractTransition<S extends AbstractState<? extends AbstractTransition<S>>> {
  protected S from, to;
  protected Events events;
  protected Formula condition;
  protected AbstractActions actions;

  public AbstractTransition(S from, S to, Events events,
      Formula condition, AbstractActions actions) {
    this.from = from;
    this.to = to;
    this.events = events;
    this.condition = condition;
    this.actions = actions;
  }

  public abstract boolean evalCondition(AbstractGlobalState<?, S, ?> env);

  public S getSource() {
    return from;
  }

  public S getDestination() {
    return to;
  }

  public Events getEvent() {
    return events;
  }

  public Formula getCondition() {
    return condition;
  }

  public AbstractActions getActions() {
    return actions;
  }
}
