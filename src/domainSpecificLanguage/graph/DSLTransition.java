package domainSpecificLanguage.graph;

import graph.State;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utils.GenericToString;
import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractTransition;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.valuation.Valuation;
import abstractGraph.events.Actions;
import abstractGraph.events.Events;
import abstractGraph.events.SingleEvent;

public class DSLTransition extends AbstractTransition<State> {

  static final State identical_state = new State("0");

  public DSLTransition() {
    super(identical_state, identical_state, new Events(), null,
        new DSLActions());
  }

  public void addSingleEvent(SingleEvent e) {
    if (e == null) {
      throw new IllegalArgumentException(
          "You cannot add a null event for a transition.");
    }
    events.addEvent(e);
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
        + GenericToString.printCollection(events.getEvents()) +
        " when "
        + ((condition != null) ? condition.toString() : " true") +
        " do" + actions + ";";
  }

  @Override
  public Actions getActions() {
    return actions;
  }

  @Override
  public boolean evalCondition(AbstractGlobalState<?, State, ?, ?> env) {
    // TODO Auto-generated method stub
    throw new NotImplementedException();
  }
}
