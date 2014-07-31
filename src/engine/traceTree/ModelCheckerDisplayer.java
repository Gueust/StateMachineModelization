package engine.traceTree;

import java.util.Iterator;
import java.util.LinkedHashSet;

import utils.Pair;
import engine.GraphSimulatorInterface;
import engine.ModelChecker;
import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import abstractGraph.events.ExternalEvent;

/**
 * 
 * The same as {@link ModelChecker} but will display the trace tree at the end
 * of the proof.
 * 
 * WARNING: This consumes a LOT of memory and will be possible only for models
 * with few states (i.e. some thousands at most).
 */
public class ModelCheckerDisplayer<GS extends AbstractGlobalState<M, S, T, ?>, M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>>
    extends ModelChecker<GS, M, S, T> {

  /** The first states that are visited */
  private GS initial_state;

  public ModelCheckerDisplayer() {
    super();
  }

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
  public GS verify(GraphSimulatorInterface<GS, M, S, T> simulator) {
    assert (unvisited_states != null);

    visited_states.clear();
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

    /** Added Code **/
    assert (unvisited_states.iterator().hasNext());
    initial_state = unvisited_states.iterator().next();
    assert (initial_state != null);
    /** End added code **/

    System.err.flush();
    System.out.flush();
    System.err.println("Initial states size : " + unvisited_states.size());
    System.err.println("We are visiting at least " + unvisited_states.size()
        + " states");

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
      }

      LinkedHashSet<ExternalEvent> possible_external_events =
          simulator.getPossibleEvent(state);

      for (ExternalEvent e : possible_external_events) {
        GS next_state = simulator.execute(state, e);

        next_state.last_processed_external_event = e;
        next_state.previous_global_state = state;
        if (state.children_states == null) {
          state.children_states = new LinkedHashSet<>();
        }
        state.children_states
            .add(new Pair<AbstractGlobalState<M, S, T, ?>, ExternalEvent>(
                next_state, e));

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
            return next_state;
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

    new DisplayExecutionTree<>(initial_state);

    return null;
  }

}
