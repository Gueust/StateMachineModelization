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

  /**
   * Set the given `init` state as the single initial state to explore from.
   * 
   * @param init
   */
  public void configureInitialGlobalStates(GS init) {
    initial_states.clear();
    initial_states.add(init);
  }

  /**
   * Process an explored global state.
   * 
   * @param state
   * @return null is everything went fine. The error state (i.e. not safe or
   *         error) otherwise.
   */
  private GS processGS(GS state) {
    /* The state is already known. */
    if (visited_states.contains(state) || illegal_states.contains(state)) {
      return null;
    }

    /* The state is illegal */
    if (!state.isLegal()) {
      illegal_states.add(state);
      return null;
    }

    /* The state is unsafe ! We return it (i.e. mark it as an error) */
    if (!state.isSafe() || !state.isNotP7()) {
      return state;
    }

    /* If everything went fine, it is a new state to visit */
    unvisited_states.add(state);

    return null;
  }

  /**
   * @details
   *          Properties to verify:
   *          - unvisited_states and visited_states have never one item in
   *          common.
   *          - all states added in visited_states must be legal. To ensure this
   *          invariant, we verify it for the states added to unvisited_states.
   * @param simulator
   * @return A GlobalShate in which the safety properties are not verified.
   *         null if no such state exists.
   */
  public GS verify(
      GraphSimulatorInterface<GS, M, S, T> simulator) {
    assert (unvisited_states != null);
    assert (possible_events != null);

    unvisited_states.clear();
    /*
     * We need to check that all the initial states are legal before adding
     * them.
     */
    for (GS global_state : initial_states) {
      GS result = processGS(global_state);
      if (result == null) {
        return result;
      }
    }

    System.err.flush();
    System.out.flush();

    System.err.println("We are visiting at least " + unvisited_states.size()
        + " states");
    while (unvisited_states.size() != 0) {
      Iterator<GS> it = unvisited_states.iterator();
      GS state = it.next();
      it.remove();

      visited_states.add(state);
      System.err.println("Number of visited states: "
          + visited_states.size());

      for (ExternalEvent e : possible_events) {
        @SuppressWarnings("unchecked")
        GS next_state = simulator.execute(state, e);
        i++;
        System.err.flush();
        System.out.flush();
        System.err.println("Eploring N° " + i);

        if (processGS(next_state) != null) {
          return next_state;
        }
      }
    }
    System.err.println("No error");
    System.err.println("Total number of visited states: "
        + visited_states.size());
    System.err.println("Total number of illegal states found:" +
        illegal_states.size());
    System.err.println("Total number of explored node: " + i);
    return null;
  }

  private int i = 0;
}
