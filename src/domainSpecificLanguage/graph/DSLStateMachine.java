package domainSpecificLanguage.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import javax.management.openmbean.KeyAlreadyExistsException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import abstractGraph.AbstractStateMachine;
import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.conditions.Formula;
import abstractGraph.events.Actions;
import abstractGraph.events.Events;
import abstractGraph.events.SingleEvent;

public class DSLStateMachine extends
    AbstractStateMachine<DSLState, DSLTransition> {

  protected HashMap<String, DSLState> states = new HashMap<>();
  protected LinkedHashSet<EnumeratedVariable> read_variables;
  protected LinkedHashSet<EnumeratedVariable> write_variables;
  protected DSLState initial_state;

  public DSLStateMachine(String name) {
    super(name);
  }

  public DSLState getInitial_state() {
    return initial_state;
  }

  public void setInitial_state(DSLState initial_state) {
    this.initial_state = initial_state;
  }

  @Override
  public Iterator<DSLState> iterator() {
    return states.values().iterator();
  }

  @Override
  public Iterator<DSLTransition> iteratorTransitions() {
    LinkedList<DSLTransition> transitions = new LinkedList<>();

    for (DSLState state : states.values()) {
      for (DSLTransition transition : state) {
        transitions.add(transition);
      }
    }
    return transitions.iterator();
  }

  @Override
  public LinkedList<DSLTransition> getTransitions(SingleEvent E) {
    LinkedList<DSLTransition> transitions = new LinkedList<>();

    for (DSLState state : states.values()) {
      Iterator<DSLTransition> transition_iterator = state
          .iteratorTransitions(E);
      while (transition_iterator.hasNext()) {
        transitions.add(transition_iterator.next());
      }
    }
    return transitions;
  }

  @Override
  public DSLTransition addTransition(DSLState from, DSLState to, Events events,
      Formula guard, Actions actions) {
    throw new NotImplementedException();
  }

  @Override
  public DSLState addState(String state_name) throws KeyAlreadyExistsException {
    if (states.get(state_name) != null) {
      throw new KeyAlreadyExistsException();
    }
    DSLState state = new DSLState(state_name);
    states.put(state_name, state);
    return state;
  }

  @Override
  public void addState(DSLState state) throws KeyAlreadyExistsException {
    if (states.get(state.getId()) != null) {
      throw new KeyAlreadyExistsException();
    }
    states.put(state.getId(), state);
  }

  @Override
  public DSLState getState(String state_name) {
    return states.get(state_name);
  }

  @Override
  public String toString() {
    StringBuilder string_builder = new StringBuilder();
    string_builder.append("machine " + getName() +
        "(" + initial_state.getId() + ")\n");
    for (DSLState state : states.values()) {
      for (DSLTransition transition : state) {
        string_builder.append("  " + transition.toString() + "\n");
      }
    }
    string_builder.append("end");

    return string_builder.toString();
  }

}
