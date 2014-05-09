package graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;

import javax.management.openmbean.KeyAlreadyExistsException;

import abstractGraph.AbstractStateMachine;
import abstractGraph.conditions.AbstractCondition;
import abstractGraph.conditions.Variable;
import abstractGraph.events.AbstractActions;
import abstractGraph.events.Events;
import abstractGraph.events.SingleEvent;

public class StateMachine extends AbstractStateMachine<State, Transition> {
  protected State initial_sate;
  protected HashMap<String, State> states;
  protected LinkedHashSet<Variable> read_variables;
  protected LinkedHashSet<Variable> write_variables;

  public StateMachine(String name) {
    super(name);
    states = new HashMap<String, State>(10);
  }

  @Override
  public Iterator<Transition> transitions() {
    /* We iterate over the states, and over the transitions within a state */
    /* TODO: the algorithm can be improved */
    class TransitionsIterator implements Iterator<Transition> {
      Iterator<State> states_iterator = states.values().iterator();
      Iterator<Transition> transitions_iterator = null;

      public TransitionsIterator() {
        if (states_iterator.hasNext()) {
          transitions_iterator = states_iterator.next().transitions();
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
          transitions_iterator = states_iterator.next().transitions();
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
          transitions_iterator = states_iterator.next().transitions();
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
  public Iterator<State> states() {
    return states.values().iterator();
  }

  @Override
  public Transition addTransition(State from, State to, Events events,
      AbstractCondition guard, AbstractActions actions) {

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
  public Iterator<Transition> getTransition(SingleEvent E) {
    // TODO Auto-generated method stub
    return null;
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
    Iterator<State> states_iterator = states();
    while (states_iterator.hasNext()) {
      State s = states_iterator.next();
      result += s.toString();
    }
    return result;
  }
}
