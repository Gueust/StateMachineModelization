package abstractGraph;

import abstractGraph.conditions.Formula;
import abstractGraph.events.Actions;
import abstractGraph.events.Events;

public abstract class AbstractTransition<S extends AbstractState<? extends AbstractTransition<S>>> {
  protected S from, to;
  protected Events events;
  protected Formula condition;
  protected Actions actions;

  /**
   * Construct a new formula. Only from and to MUST be not null.
   * 
   * @param from
   *          The starting state
   * @param to
   *          The destination state
   * @param events
   *          The events object (can be null, it that case it is considered as
   *          empty).
   * @param condition
   *          The formula (can be null, in that case, it is considered as TRUE)
   * @param actions
   *          The list of actions (can be null, in that case it is considered as
   *          empty).
   */
  public AbstractTransition(S from, S to, Events events,
      Formula condition, Actions actions) {
    this.from = from;
    this.to = to;
    this.events = events;
    this.condition = condition;
    this.actions = actions;
  }

  public abstract boolean evalCondition(AbstractGlobalState<?, S, ?> env);

  /**
   * @return The source state.
   */
  public S getSource() {
    assert (from != null);
    return from;
  }

  /**
   * @return The destination state.
   */
  public S getDestination() {
    assert (to != null);
    return to;
  }

  /**
   * @return The events field if not null. Events.NONE otherwise.
   */
  public Events getEvents() {
    if (events == null) {
      return Events.NONE;
    } else {
      return events;
    }
  }

  /**
   * @return The condition field if not null. Formula.TRUE otherwise.
   */
  public Formula getCondition() {
    if (condition == null) {
      return Formula.TRUE;
    } else {
      return condition;
    }
  }

  /**
   * @return The actions field if not null. Actions.NONE otherwise.
   */
  public Actions getActions() {
    if (actions == null) {
      return Actions.NONE;
    } else {
      return actions;
    }
  }
}
