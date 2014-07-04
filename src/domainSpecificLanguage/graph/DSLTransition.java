package domainSpecificLanguage.graph;

import java.util.HashSet;

import utils.GenericToString;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.valuation.Valuation;
import abstractGraph.events.SingleEvent;

public class DSLTransition {

  protected HashSet<SingleEvent> events = new HashSet<>();
  protected Formula condition;

  protected DSLActions actions = new DSLActions();

  /*
   * public DSLTransition(Events events,
   * Formula condition, Actions actions) {
   * this.events = events;
   * this.condition = condition;
   * this.actions = actions;
   * }
   */

  public DSLTransition() {
  }

  public void addSingleEvent(SingleEvent e) {
    if (e == null) {
      throw new IllegalArgumentException(
          "You cannot add a null event for a transition.");
    }
    events.add(e);
  }

  public void addAction(SingleEvent e) {
    if (e == null) {
      throw new IllegalArgumentException(
          "You cannot add a null action for a transition.");
    }
    actions.add(e);
  }

  public void setCondition(Formula condition) {
    this.condition = condition;
  }

  // TODO verify that it works and add the tests.
  public boolean evalCondition(Valuation valuation) {
    if (condition == null) {
      return true;
    }
    return condition.eval(valuation);
  }

  @Override
  public String toString() {

    return "on "
        + GenericToString.printCollection(events) +
        " when "
        + ((condition != null) ? condition.toString() : " true") +
        " do" + actions + ";";

  }
}
