package engine;

import engine.SplitProof.MyNode;
import genericLabeledGraph.Edge;
import graph.GlobalState;
import graph.Model;
import graph.StateMachine;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map.Entry;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.events.ExternalEvent;
import abstractGraph.events.SingleEvent;

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

  protected void setDiskBackUpMemory() {
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

    visited_states = db.getHashSet("visited_states");
    unvisited_states = db.getHashSet("unvisited_states");
    // DBMaker.newTempHashSet();

  }

  private boolean isVisited(GS state) {
    return visited_states.contains(state);
  }

  private void addVisited(GS state) {
    if (state.isLegal()) {
      visited_states.add(state);
    } else {
      System.err.println("You have added an ilegal initial state to the" +
          " model checker");
    }
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
      System.err.println("Total number of illegal states found:" +
          number_illegal_states);

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

    System.err.flush();
    System.out.flush();

    return null;
  }

  public GS verifyUsingSplitting(SequentialGraphSimulator simulator) {
    Model model = simulator.getModel();
    Model proof = simulator.proof;
    if (proof == null) {
      throw new IllegalArgumentException("The proof model must be not null");
    }

    SplitProof splitter = new SplitProof(model, proof);

    for (StateMachine state_machine_to_prove : proof) {
      Set<StateMachine> frontier = new LinkedHashSet<>();
      frontier.add(state_machine_to_prove);
      Set<StateMachine> visited = new LinkedHashSet<>();

      while (frontier.size() != 0) {
        StateMachine state_machine = frontier.iterator().next();
        visited.add(state_machine);

        for (Edge<MyNode, SingleEvent> edge : splitter.nodes.get(state_machine)) {

          StateMachine new_to_visit = edge.to.data;

          if (visited.contains(new_to_visit)) {
            frontier.add(new_to_visit);
          }
        }
      }
    }
    return null;
  }

  public Set<GS> getVisited_states() {
    return visited_states;
  }

  public Set<GS> getUnvisited_states() {
    return unvisited_states;
  }

  public void printOtherVisitedStatesWithSameFunctional(
      SequentialGraphSimulator simulator) {

    for (GS state : visited_states) {
      boolean found = false;

      for (GS visited_state : visited_states) {
        if (isIdenticalFunctionalDifferentProof((GlobalState) state,
            (GlobalState) visited_state, simulator)) {
          if (!found) {
            System.out.println("The given state is : \n" + state);
          }
          found = true;
          System.out
              .println("Other states with the same functional found. Differences are:\n");
          visited_state.compare(state);
          System.out.println("** First state: " + visited_state);
          System.out.println("** Second state: " + state);
        }
      }
      if (found) {
        return;
      }
    }
  }

  /**
   * 
   * @param s1
   * @param s2
   * @param simulator
   * @return true iif the functional valuation is identical and the proof
   *         valuation is different
   */
  private boolean isIdenticalFunctionalDifferentProof(GlobalState s1,
      GlobalState s2,
      SequentialGraphSimulator simulator) {
    Model func = simulator.model;
    Collection<BooleanVariable> existing_variables = func
        .getExistingVariables()
        .values();

    /* We first check that all the states of the functional are equals */
    for (StateMachine machine : func) {
      if (s1.getState(machine) != s2.getState(machine)) {
        return false;
      }
    }

    /* Then, we check if one state of the proof is different */
    for (StateMachine machine : simulator.proof) {
      if (s1.getState(machine) != s2.getState(machine)) {
        System.out.println("Different state for " + machine.getName() + " : "
            + s1.getState(machine).getId()
            + " versus " + s2.getState(machine).getId());
        return true;
      }
    }

    /* Then we compare the variables */
    for (Entry<BooleanVariable, Boolean> pair : s1.getValuation().getSetVariables()) {
      BooleanVariable var = pair.getKey();
      boolean identical_in_both_model =
          s1.getVariableValue(var) == s2.getVariableValue(var);
      // System.out.println("Variable " + var + " is "
      // + (s1.getVariableValue(var)) +
      // " and " + (s2.getVariableValue(var)) + " : "
      // + identical_in_both_model);
      if (identical_in_both_model) {
        continue;
      }
      if (existing_variables.contains(var)) {
        /* Both functional valuation are different */
        return false;
      } else {
        System.out.println("RETURNED TRUE 2");
        return true;
      }
    }
    return false;
  }
}
