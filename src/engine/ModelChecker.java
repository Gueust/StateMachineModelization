package engine;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import abstractGraph.events.ExternalEvent;

public class ModelChecker<GS extends AbstractGlobalState<M, S, T>, M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> {

  /** The already explored states */
  private LinkedHashSet<GS> visited_states = new LinkedHashSet<GS>();

  /** The states to explore */
  private LinkedHashSet<GS> unvisited_states = new LinkedHashSet<GS>();

  private LinkedList<ExternalEvent> possible_events;
  private LinkedList<GS> initial_states;

  /**
   * The states that are excluded from the exploration by the postulate states
   * machines. When a state is not to explore, isP6() of a simulator is true.
   */
  private HashSet<GS> illegal_states;

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

  public void configureExternalEvents(Iterator<ExternalEvent> events) {
    possible_events = new LinkedList<ExternalEvent>();
    while (events.hasNext()) {
      possible_events.add(events.next());
    }

  }

  /**
   * Initialize the initial states as the ones contained in `init`.
   * It does not take the given collection but creates and underlying HashMap
   * containing the elements of `init`.
   * 
   * @param init
   */
  public void configureInitialGlobalStates(Collection<GS> init) {
    initial_states = new LinkedList<GS>(init);
  }

  public void configureInitialGlobalStates(GS init) {
    initial_states.clear();
    initial_states.add(init);
  }

  /**
   * @details
   *          Properties to verify:
   *          - unvisited_states and visited_states have never one item in
   *          common.
   * @param simulator
   * @return A GlobalShate in which the safety properties are not verified.
   *         null if no such state exists.
   */
  public GS verify(
      GraphSimulatorInterface<GS, M, S, T> simulator) {
    assert (unvisited_states != null);
    assert (possible_events != null);

    unvisited_states.clear();
    unvisited_states.addAll(initial_states);

    while (unvisited_states.size() != 0) {
      Iterator<GS> it = unvisited_states.iterator();

      GS state = it.next();
      it.remove();
      visited_states.add(state);

      for (ExternalEvent e : possible_events) {
        @SuppressWarnings("unchecked")
        GS next_state = simulator.execute((GS) state.clone(), e);
        i++;
        System.err.println("Eploring N° " + i);

        /* Illegal state */
        if (!next_state.isLegal()) {
          /* If we reach this code, the state is not in illegal_sates */
          if (!illegal_states.contains(next_state)) {
            illegal_states.add(next_state);
          }
          continue;
        }

        if (visited_states.contains(next_state)) {
          continue;
        }
        unvisited_states.add(next_state);

        /* Safety property not respected */
        if (!next_state.isSafe()) {
          return next_state;
        }
      }
    }

    return null;
  }

  private int i = 0;
}
