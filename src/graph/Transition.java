package graph;

import java.util.Iterator;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import abstractGraph.AbstractTransition;
import abstractGraph.AbstractGlobalState;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.Valuation;
import abstractGraph.conditions.Variable;
import abstractGraph.events.Actions;
import abstractGraph.events.Events;

public class Transition extends AbstractTransition<State> {

  public Transition(State from, State to, Events event,
      Formula condition, Actions actions) {
    super(from, to, event, condition, actions);
  }

  //TODO verify that it works and add the tests.
  @Override
  public boolean evalCondition(AbstractGlobalState<?, State, ?> global_state) {
    Valuation valuation = new Valuation();
    Iterator<Variable> variable_iterator = global_state.iteratorVariables();
    while (variable_iterator.hasNext()) {
      Variable variable = variable_iterator.next();
      valuation.setValue(variable, global_state.getVariableValue(variable));
    }
    return condition.eval(valuation);
  }

  @Override
  public String toString() {

    return from.getId() + " --> " + to.getId() + " -- EVENT: "
        + events.toString() + "\n" +
        "              CONDITION: "
        + ((condition != null) ? condition.toString() : " Empty") + "\n" +
        "              ACTIONS: " + actions.toString();

  }

}
