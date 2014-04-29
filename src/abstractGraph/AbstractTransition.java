package abstractGraph;

import abstractGraph.Conditions.Condition;
import abstractGraph.Events.AbstractActions;
import abstractGraph.Events.SingleEvent;

public abstract class AbstractTransition<S extends AbstractState<? extends AbstractTransition<S>>> {
  protected S from, to;
  protected SingleEvent event;
  protected Condition condition;
  protected AbstractActions actions;

  public AbstractTransition(S from, S to, SingleEvent event,
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

  public SingleEvent getEvent() {
    return event;
  }

  public Condition getCondition() {
    return condition;
  }

  public AbstractActions getActions() {
    return actions;
  }
}
