package abstractEngine;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import abstractGraph.GlobalState;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import abstractGraph.events.ExternalEvent;

public abstract class AbstractModelChecker<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> {

  /** The already explored states */
  private HashMap<GlobalState<S, T>, GlobalState<S, T>> visited_states =
      new LinkedHashMap<GlobalState<S, T>, GlobalState<S, T>>();

  /** The states to explore */
  private LinkedHashMap<GlobalState<S, T>, GlobalState<S, T>> unvisited_states;

  private LinkedList<ExternalEvent> possible_events;

  /**
   * The states that are excluded from the exploration by the postulate states
   * machines. When a state is not to explore, isP6() of a simulator is true.
   */
  private HashSet<GlobalState<S, T>> illegal_states;

  /**
   * Initialize the external events that will be tested.
   * It does not take the given collection but creates an underlying Linkedlist
   * containing the elements of `events`.
   * 
   * @param events
   *          A collection from which to retrieve the external events.
   */
  public void configureExternalEvents(Collection<ExternalEvent> events) {
    possible_events = new LinkedList<ExternalEvent>(events);
  }

  /**
   * Initialize the initial states as the ones contained in `init`.
   * It does not take the given collection but creates and underlying HashMap
   * containing the elements of `init`.
   * 
   * @param init
   */
  public void configureInitialGlobalStates(Collection<GlobalState<S, T>> init) {
    unvisited_states =
        new LinkedHashMap<GlobalState<S, T>, GlobalState<S, T>>();
    for (GlobalState<S, T> s : init) {
      unvisited_states.put(s, s);
    }
  }

  /**
   * 
   * @param simulator
   * @return A GlobalShate in which the safety properties are not verified.
   *         null if no such state exists.
   */
  public GlobalState<S, T> verify(AbstractGraphSimulator<M, S, T> simulator) {
    assert (unvisited_states != null);
    assert (possible_events != null);

    while (unvisited_states.size() != 0) {
      Iterator<GlobalState<S, T>> it = unvisited_states.values().iterator();

      GlobalState<S, T> state = it.next();
      it.remove();
      visited_states.put(state, state);

      for (ExternalEvent e : possible_events) {
        GlobalState<S, T> next_state = simulator.execute(state, e);

        /* Illegal state */
        if (simulator.isP6()) {
          /* If we reach this code, the state is not in illegal_sates */
          if (!illegal_states.contains(next_state)) {
            illegal_states.add(next_state);
          }
          continue;
        }

        /* Safety property not respected */
        if (simulator.isP5()) {
          unvisited_states.put(next_state, next_state);
          return next_state;
        }

        if (!visited_states.containsKey(next_state)) {
          unvisited_states.put(next_state, next_state);
        }
      }
    }
    return null;
  }
}
