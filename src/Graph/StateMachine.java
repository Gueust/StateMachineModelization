package Graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractState;
import abstractGraph.AbstractTransition;
import abstractGraph.Actions;
import abstractGraph.Conditions.Condition;
import abstractGraph.Events.Event;

public class StateMachine extends AbstractStateMachine {
  protected AbstractState initial_sate;
  protected HashMap<Integer, AbstractState> states;

  public StateMachine(String name) {
    super(name);
    states = new HashMap<Integer, AbstractState>(10);
  }

  @Override
  public Iterator<AbstractTransition> transitions() {
    /* We iterate over the states, and over the transitions within a state */
    /* TODO: the algorithm can be improved */
    class TransitionsIterator implements Iterator<AbstractTransition> {
      Iterator<AbstractState> states_iterator = states.values().iterator();
      Iterator<AbstractTransition> transitions_iterator = null;

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
      public AbstractTransition next() {
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
  public Iterator<AbstractState> states() {
    return states.values().iterator();
  }

  @Override
  public void addTransition(AbstractState from, AbstractState to, Event event,
      Condition guard, Actions actions) {
    State s1 = (State) states.get(from.getId());
    if (s1 == null) {
      states.put(from.getId(), from);
    }
    State s2 = (State) states.get(to.getId());
    if (s2 == null) {
      states.put(to.getId(), to);
    }

    Transition t = new Transition(from, to, event, guard, actions);
    s1.addTransition(t);
  }

  @Override
  public Iterator<AbstractTransition> get_transition(Event E) {
    // TODO Auto-generated method stub
    return null;
  }
}
