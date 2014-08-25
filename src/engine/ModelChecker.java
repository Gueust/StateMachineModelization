package engine;

import genericLabeledGraph.Edge;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractModel;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.conditions.valuation.Valuation;
import abstractGraph.events.ExternalEvent;
import abstractGraph.events.SingleEvent;

/**
 * 
 * The model checker works on a simulator implementing the
 * GraphSimulatorInterface.
 * 
 * The actual implementation executed models extending the AbstractModel class.
 * Thus, the model checker is parameterized with the classes used in this actual
 * model.
 * 
 * A single model checker instance can be used several times if the
 * {@link #reset()} function is called within different verifications.
 * 
 * @details
 * 
 * @param <GS>
 *          The global states used by the graph simulator that will
 *          be given to {@link #verify(GraphSimulatorInterface)}.
 * @param <M>
 *          The Machines used in the model used by the graph simulator that will
 *          be given to {@link #verify(GraphSimulatorInterface)}.
 * @param <S>
 *          The states used in the model.
 * @param <T>
 *          The transitions used in the model.
 */
public class ModelChecker<GS extends AbstractGlobalState<M, S, T, ?>, M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>>
    implements ModelCheckerInterface<GS, M, S, T> {

  /**
   * Prints the number of visited, unvisited and unsafe states at when exploring
   * every states. Otherwise, it is done every hundred turns.
   */
  protected final static boolean VERY_VERBOSE = false;
  /**
   * When finding an unsafe state, prints the initial state, the unsafe state
   * and the trace leading to it
   */
  protected final static boolean PRINT_TRACE_UNSAFE = true;

  /** The already explored states */
  protected Set<GS> visited_states = new HashSet<GS>();

  /** The states to explore */
  protected Set<GS> unvisited_states = new HashSet<GS>();

  /**
   * The states that are excluded from the exploration by the postulate states
   * machines. When a state is not to explore, isP6() of a simulator is true.
   */
  protected HashSet<GS> unsafe_states = new HashSet<GS>();
  protected int number_illegal_states = 0;
  protected int number_of_functional_warning = 0;
  protected int number_explored_nodes = 0;

  public ModelChecker() {
    /**
     * Not used. To be able to use a database, the GlobalState class must be
     * serializable.
     */
    /*
     * DB db = DBMaker
     * .newMemoryDirectDB()
     * .transactionDisable()
     * .asyncWriteEnable()
     * .asyncWriteFlushDelay(100)
     * // .compressionEnable()
     * .make();
     * 
     * visited_states = db.getHashSet("Visited states");
     * unvisited_states = db.getHashSet("Unvisited states");
     */
  }

  /**
   * Initialize the initial states as the ones contained in `init`.
   * It does not take the given collection but creates and underlying HashMap
   * containing the elements of `init`.
   * 
   * All previously added initial states are removed.
   * 
   * @param init
   */
  public void addAllInitialStates(Collection<GS> init) {
    unvisited_states.clear();
    for (GS state : init) {
      addInitialState(state);
    }
  }

  /**
   * Add `init` in the set of initial states to visit.
   * If the given state is not safe, the verification will necessarily fail when
   * visiting it.
   * If the given state is not physical, it will not modify the behavior of the
   * verification.
   * 
   * @param init
   *          An initial state to visit.
   */
  public void addInitialState(GS init) {
    if (init.isLegal()) {
      unvisited_states.add(init);
    }
  }

  /**
   * Not used. To be able to use a database, the GlobalState class must be
   * serializable.
   */
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

  protected boolean isVisited(GS state) {
    return visited_states.contains(state);
  }

  protected void addVisited(GS state) {
    if (state.isLegal()) {
      visited_states.add(state);
    } else {
      System.err.println("You have added an ilegal initial state to the" +
          " model checker");
    }
  }

  /**
   * Process an explored global state.
   * 
   * @param state
   * @return null is everything went fine. The error state (i.e. not safe or
   *         error) otherwise.
   */
  protected GS processGS(GS state) {
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
    if (!state.isSafe()) {
      unsafe_states.add(state);
      return state;
    }

    if (!state.isNotP7()) {
      number_of_functional_warning++;
      return state;
    }

    /* If everything went fine, it is a new state to visit */
    unvisited_states.add(state);

    return null;
  }

  /**
   * Prepare the model checker for a new verification (i.e. receive new initial
   * states and a new call to verify).
   */
  public void reset() {
    /* We reset all the data to empty data */
    unvisited_states.clear();
    visited_states.clear();
    unsafe_states.clear();

    number_illegal_states = 0;
    number_of_functional_warning = 0;
    number_explored_nodes = 0;
  }

  protected GraphSimulatorInterface<GS, M, S, T> simulator;

  /**
   * @details
   *          Properties to verify:
   *          - unvisited_states and visited_states have never one item in
   *          common.
   *          - all states added in visited_states must be legal. To ensure this
   *          invariant, we verify it for the states added to unvisited_states
   *          are legal.
   * @param simulator
   * @param build_tree
   *          Setting it to true will make the model checker build the full
   *          trace tree for display ({@link #displayTree()}. This will consume
   *          more memory, so it should not be used if not needed.
   * @return A GlobalShate in which the safety properties are not verified.
   *         null if no such state exists.
   */
  @Override
  public GS verify(GraphSimulatorInterface<GS, M, S, T> simulator) {
    assert (unvisited_states != null);

    this.simulator = simulator;

    visited_states.clear();
    unsafe_states.clear();
    number_illegal_states = 0;
    number_of_functional_warning = 0;
    number_explored_nodes = 0;

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

    long startTime = System.nanoTime();
    GS error_state = null;
    int c = 0;
    while (unvisited_states.size() != 0) {
      c++;

      Iterator<GS> it = unvisited_states.iterator();
      GS state = it.next();
      assert (state.isLegal());

      it.remove();

      addVisited(state);

      if (VERY_VERBOSE || c % 100 == 0) {
        System.err.println("Number of visited states: "
            + visited_states.size());
        System.err.println("Number of unvisited states "
            + unvisited_states.size());
        System.err.println("Total number of illegal nodes found:" +
            number_illegal_states);
        System.err.println("Total number of unsafe nodes found:" +
            unsafe_states.size());

        long estimatedTime = System.nanoTime() - startTime;
        System.err.println("Time to visit the last 100 states " +
            estimatedTime / 1000000000.0 + "s");
        startTime = System.nanoTime();
      }

      LinkedHashSet<ExternalEvent> possible_external_events =
          simulator.getPossibleEvent(state);

      for (ExternalEvent e : possible_external_events) {
        GS next_state = simulator.execute(state, e);

        next_state.last_processed_external_event = e;
        next_state.previous_global_state = state;

        number_explored_nodes++;
        System.err.flush();
        System.out.flush();

        if (processGS(next_state) != null) {
          if (PRINT_TRACE_UNSAFE) {
            System.out
                .println("The model checker detected a dangerous state !");
            System.out.println("***********************************");
            System.out.println("A FULL trace of external event is: ");
            System.out.println("***********************************");
            System.out.println(printFullTrace(next_state));
          }

          if (!next_state.isSafe()) {
            error_state = next_state;
            // return next_state;
          }
        }
      }
    }
    System.err.println("Total number of distinct visited states: "
        + visited_states.size());
    System.err.println("Total number of illegal nodes found:" +
        number_illegal_states);
    System.err.println("Total number of explored node: "
        + number_explored_nodes);
    System.err.println("Total number of unsafe node: " + unsafe_states.size());
    System.err.println("Total number of functional warnings (P7) nodes: "
        + number_of_functional_warning);

    return error_state;
  }

  @SuppressWarnings("unchecked")
  public String printFullTrace(GS state) {
    StringBuilder string_builder = new StringBuilder();

    assert (simulator != null);
    GS current = state;
    string_builder.insert(0, "\nTo the final state:\n\n"
        + simulator.globalStateToString(current) + "\n");
    do {
      if (current.last_processed_external_event != null) {
        string_builder.insert(0, current.last_processed_external_event + "\n");
      } else {
        string_builder
            .insert(0, "\nFrom the initial state:\n"
                + simulator.globalStateToString(current) + "\n");
      }
      current = (GS) current.previous_global_state;
    } while (current != null);

    return string_builder.toString();
  }

  GS verifyUsingSplitting(GraphSimulatorInterface<GS, M, S, T> simulator) {
    AbstractModel<M, S, T> model = simulator.getModel();
    AbstractModel<M, S, T> proof = simulator.getProof();
    if (proof == null) {
      throw new IllegalArgumentException("The proof model must be not null");
    }

    BuildActivationGraph<M, S, T> splitter = new BuildActivationGraph<M, S, T>(
        model, proof);

    for (M state_machine_to_prove : proof) {
      Set<M> frontier = new LinkedHashSet<>();
      frontier.add(state_machine_to_prove);
      Set<M> visited = new LinkedHashSet<>();

      while (frontier.size() != 0) {
        M state_machine = frontier.iterator().next();
        visited.add(state_machine);

        for (Edge<BuildActivationGraph<M, S, T>.MyNode, SingleEvent> edge : splitter.nodes
            .get(state_machine)) {

          M new_to_visit = edge.to.data;

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
      GraphSimulatorInterface<GS, M, S, T> simulator) {

    for (GS state : visited_states) {
      boolean found = false;

      for (GS visited_state : visited_states) {
        if (isIdenticalFunctionalDifferentProof(state, visited_state, simulator)) {
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
   * Works ONLY with GlobalState using a Valuation object.
   * 
   * @param s1
   * @param s2
   * @param simulator
   * @return true iif the functional valuation is identical and the proof
   *         valuation is different
   */
  private boolean isIdenticalFunctionalDifferentProof(GS s1,
      GS s2,
      GraphSimulatorInterface<GS, M, S, T> simulator) {
    AbstractModel<M, S, T> func = simulator.getModel();
    Collection<EnumeratedVariable> existing_variables = func
        .getExistingVariables();

    /* We first check that all the states of the functional are equals */
    for (M machine : func) {
      if (s1.getState(machine) != s2.getState(machine)) {
        return false;
      }
    }

    /* Then, we check if one state of the proof is different */
    for (M machine : simulator.getProof()) {
      if (s1.getState(machine) != s2.getState(machine)) {
        System.out.println("Different state for " + machine.getName() + " : "
            + s1.getState(machine).getId()
            + " versus " + s2.getState(machine).getId());
        return true;
      }
    }

    /* Then we compare the variables */
    Valuation valuation = (Valuation) s1.getValuation();

    for (Entry<BooleanVariable, Boolean> pair : valuation.getSetVariables()) {
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
