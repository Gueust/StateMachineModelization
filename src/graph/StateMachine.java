package graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.management.openmbean.KeyAlreadyExistsException;

import abstractGraph.AbstractStateMachine;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.events.Actions;
import abstractGraph.events.Events;
import abstractGraph.events.SingleEvent;

public class StateMachine extends AbstractStateMachine<State, Transition> {
  protected State initial_sate;
  protected HashMap<String, State> states;
  protected LinkedHashSet<BooleanVariable> read_variables;
  protected LinkedHashSet<BooleanVariable> write_variables;

  public StateMachine(String name) {
    super(name);
    states = new HashMap<String, State>(10);
  }

  @Override
  public Iterator<Transition> iteratorTransitions() {
    /* We iterate over the states, and over the transitions within a state */
    /* TODO: the algorithm can be improved */
    class TransitionsIterator implements Iterator<Transition> {
      Iterator<State> states_iterator = states.values().iterator();
      Iterator<Transition> transitions_iterator = null;

      public TransitionsIterator() {
        if (states_iterator.hasNext()) {
          transitions_iterator = states_iterator.next().iterator();
        }
      }

      @Override
      public boolean hasNext() {
        if (transitions_iterator == null) {
          return false;
        }

        if (transitions_iterator.hasNext()) {
          return true;
        } else if (states_iterator.hasNext()) {
          transitions_iterator = states_iterator.next().iterator();
          return hasNext();
        } else {
          return false;
        }
      }

      @Override
      public Transition next() {
        if (transitions_iterator == null) {
          throw new NoSuchElementException();
        }

        if (transitions_iterator.hasNext()) {
          return transitions_iterator.next();
        } else if (states_iterator.hasNext()) {
          transitions_iterator = states_iterator.next().iterator();
          return next();
        } else {
          throw new NoSuchElementException();
        }
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }

    }
    ;
    return new TransitionsIterator();
  }

  @Override
  public Iterator<State> iterator() {
    return states.values().iterator();
  }

  @Override
  public Transition addTransition(State from, State to, Events events,
      Formula guard, Actions actions) {

    State s1 = states.get(from.getId());
    if (s1 == null) {
      states.put(from.getId(), from);
      s1 = from;
    }
    State s2 = (State) states.get(to.getId());
    if (s2 == null) {
      states.put(to.getId(), to);
      s2 = from;
    }

    Transition t = new Transition(from, to, events, guard, actions);
    s1.addTransition(t);
    return t;
  }

  @Override
  public LinkedList<Transition> getTransitions(SingleEvent E) {
    LinkedList<Transition> transition = new LinkedList<Transition>();
    Iterator<State> state_iterator = states.values().iterator();

    while (state_iterator.hasNext()) {
      State state = state_iterator.next();
      Iterator<Transition> transition_iterator = state.iteratorTransitions(E);
      while (transition_iterator.hasNext()) {
        transition.add(transition_iterator.next());
      }
    }
    return transition;
  }

  @Override
  public State getState(String name) {
    return states.get(name);
  }

  @Override
  public State addState(String state_name) throws KeyAlreadyExistsException {
    if (states.get(state_name) != null) {
      throw new KeyAlreadyExistsException("The state " + state_name
          + " already exists");
    }
    State new_state = new State(state_name);
    states.put(state_name, new_state);
    return new_state;
  }

  @Override
  public String toString() {
    String result = "# STATE MACHINE: " + getName() + " \n";
    Iterator<State> states_iterator = iterator();
    while (states_iterator.hasNext()) {
      State s = states_iterator.next();
      result += s.toString();
    }
    return result;
  }
}
