package engine;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import abstractGraph.events.ExternalEvent;

public class ModelChecker<GS extends AbstractGlobalState<M, S, T, ?>, M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> {

  /** The already explored states */
  private Set<GS> visited_states = new LinkedHashSet<GS>();

  /** The states to explore */
  private Set<GS> unvisited_states = new LinkedHashSet<GS>();

  /**
   * The states that are excluded from the exploration by the postulate states
   * machines. When a state is not to explore, isP6() of a simulator is true.
   */
  // private HashSet<GS> illegal_states = new HashSet<GS>();
  private int number_illegal_states = 0;

  private int i = 0;

  /**
   * Initialize the initial states as the ones contained in `init`.
   * It does not take the given collection but creates and underlying HashMap
   * containing the elements of `init`.
   * 
   * @param init
   */
  public void model_checker(Collection<GS> init) {
    unvisited_states.clear();
    unvisited_states.addAll(init);
  }

  /**
   * Add `init` in the set of initial states to visit.
   * 
   * @param init
   *          An initial state to visit.
   */
  public void addInitialState(GS init) {
    unvisited_states.add(init);
  }

  public void setDiskBackUpMemory() {
    visited_states = null;
    unvisited_states = null;

    DB db = DBMaker.newMemoryDirectDB() // newMemoryDB(). // newMemoryDirectDB
        /*
         * Disable transactions make writes but you may lose data if store
         * crashes
         */
        .transactionDisable()
        // .mmapFileEnable()
        .cacheLRUEnable()
        .cacheSoftRefEnable()
        .cacheSize(100000)
        .make();

    visited_states = DBMaker.newTempHashSet();
    unvisited_states = DBMaker.newTempHashSet();

  }

  private boolean isVisited(GS state) {
    return visited_states.contains(state);
  }

  private void addVisited(GS state) {
    visited_states.add(state);
  }

  private void clearVisited() {
    visited_states.clear();
  }

  /**
   * Process an explored global state.
   * 
   * @param state
   * @return null is everything went fine. The error state (i.e. not safe or
   *         error) otherwise.
   */
  private GS processGS(GS state) {
    assert state != null;

    /* The state is illegal */
    if (!state.isLegal()) {
      number_illegal_states++;
      return null;
    }

    /* The state is already known. */
    if (isVisited(state)) { // || illegal_states.contains(state)){

      return null;
    }

    /* The state is already known. */
    if (visited_states.contains(state)) { // || illegal_states.contains(state)){
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
   * Prepare the model checker for a new verification (i.e. receive new initial
   * state and a call to verify).
   */
  public void reset() {
    /* We reset all the data to empty data */
    unvisited_states.clear();
    visited_states.clear();
    number_illegal_states = 0;
    i = 0;
  }

  /**
   * @details
   *          Properties to verify:
   *          - unvisited_states and visited_states have never one item in
   *          common.
   *          - all states added in visited_states must be legal. To ensure this
   *          invariant, we verify it for the states added to unvisited_states,
   *          except for the initialization that is checked before beginning the
   *          proof.
   * @param simulator
   * @return A GlobalShate in which the safety properties are not verified.
   *         null if no such state exists.
   */
  public GS verify(GraphSimulatorInterface<GS, M, S, T> simulator) {
    assert (unvisited_states != null);

    // /illegal_states.clear();
    number_illegal_states = 0;
    clearVisited();
    i = 0;

    /*
     * We need to check that all the initial states are legal before adding
     * them.
     */
    for (GS global_state : unvisited_states) {
      GS result = processGS(global_state);
      if (result != null) {
        return result;
      }
    }

    System.err.flush();
    System.out.flush();
    System.err.println("Initial states size : " + unvisited_states.size());
    System.err.println("We are visiting at least " + unvisited_states.size()
        + " states");

    while (unvisited_states.size() != 0) {
      Iterator<GS> it = unvisited_states.iterator();
      GS state = it.next();
      it.remove();

      addVisited(state);
      System.err.println("Number of visited states: "
          + visited_states.size());
      System.err.println("Number of unvisited states "
          + unvisited_states.size());

      LinkedHashSet<ExternalEvent> possible_external_events =
          simulator.getPossibleEvent(state);
      System.err.println("Eploring N° " + i + ".\nCreating "
          + possible_external_events.size());

      for (ExternalEvent e : possible_external_events) {
        GS next_state = simulator.execute(state, e);
        i++;
        System.err.flush();
        System.out.flush();

        if (processGS(next_state) != null) {
          System.out.print("FAILURE from state \n" + state + "\n Event : " + e
              + "\n" + next_state);
          return next_state;
        }
      }
    }
    System.err.println("Total number of visited states: "
        + visited_states.size());
    System.err.println("Total number of illegal states found:" +
        number_illegal_states);
    System.err.println("Total number of explored node: " + i);
    return null;
  }

  public Set<GS> getVisited_states() {
    return visited_states;
  }

  public Set<GS> getUnvisited_states() {
    return unvisited_states;
  }

  /*
   * public HashSet<GS> getIllegal_states() {
   * return illegal_states;
   * }
   */
}
