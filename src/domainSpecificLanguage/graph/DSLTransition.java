package domainSpecificLanguage.graph;

import utils.GenericToString;
import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractTransition;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.valuation.Valuation;
import abstractGraph.events.Actions;
import abstractGraph.events.Events;
import abstractGraph.events.SingleEvent;

public class DSLTransition extends AbstractTransition<DSLState> {

  public DSLTransition(DSLState from, DSLState to) {
    super(from, to, new Events(), null, new DSLActions());
    from.addTransition(this);
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
    DSLFormulaToString customizer = DSLFormulaToString.INSTANCE;

    return from + " -> " + to
        + " : on "
        + GenericToString.printCollection(events.getCollection())
        + " when "
        + ((condition != null) ? condition.toString(customizer) : " true") +
        " do " + actions + ";";
  }

  @Override
  public Actions getActions() {
    return actions;
  }

  @Override
  public boolean evalCondition(AbstractGlobalState<?, DSLState, ?, ?> env) {
    return getCondition().eval(env.getValuation());
  }
}
