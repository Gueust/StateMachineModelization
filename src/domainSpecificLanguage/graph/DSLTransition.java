package domainSpecificLanguage.graph;

import java.util.HashSet;

import utils.GenericToString;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.valuation.Valuation;
import abstractGraph.events.Actions;
import abstractGraph.events.ExternalEvent;

public class DSLTransition {

  protected HashSet<ExternalEvent> events = new HashSet<>();
  protected Formula condition;

  protected Actions actions = new Actions();

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

  public void addExternalEvent(ExternalEvent e) {
    if (e == null) {
      throw new IllegalArgumentException(
          "You cannot add a null event for a transition.");
    }
    events.add(e);
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
        + GenericToString.printCollection(events) + "\n" +
        " when "
        + ((condition != null) ? condition.toString() : " true") + "\n" +
        "              ACTIONS: " + actions.toString();

  }

}
