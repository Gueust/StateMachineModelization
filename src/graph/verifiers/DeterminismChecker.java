package graph.verifiers;

import java.util.Iterator;
import java.util.LinkedList;

import org.sat4j.specs.TimeoutException;

import solver.SAT4JSolver;
import abstractGraph.conditions.AndFormula;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.cnf.CNFFormula;
import abstractGraph.events.Events;
import graph.Model;
import graph.State;
import graph.StateMachine;
import graph.Transition;

/**
 * This verifier checks that for all states, all transitions leaving from that
 * state and having a common event have an exclusive Condition field.
 * 
 * It will not consider 2 not exclusive transitions going to the same state and
 * labeled with the same action as an error.
 */
public class DeterminismChecker extends AbstractVerificationUnit {

  /*
   * The solver used to solve the SAT instances. It can be used multiple times
   * for different formulas
   */
  private static SAT4JSolver solver = new SAT4JSolver();

  /*
   * The 2 transitions that offer a counter example if needed (i.e. that are not
   * exclusive while beginning at the same state and being activated by a common
   * Event).
   * Then the machines where the counter example if found.
   * Because we use a single solver, we need to save the solution.
   */
  private LinkedList<Transition>
      list_counter_example_t1 = new LinkedList<Transition>(),
      list_counter_example_t2 = new LinkedList<Transition>();
  private LinkedList<StateMachine> list_counter_example_machine =
      new LinkedList<StateMachine>();
  private LinkedList<String> solution_details =
      new LinkedList<String>();

  private boolean identicalActionFields(Transition t1, Transition t2) {
    return t1.getActions().equals(t2.getActions());
  }

  private boolean identicalTarget(Transition t1, Transition t2) {
    return t1.getDestination().equals(t2.getDestination());
  }

  /**
   * Serves for both the {@link #check(Model, boolean)} and
   * {@link #checkAll(Model, boolean)} functions.
   */
  private boolean checkFunction(Model m, boolean verbose, boolean check_all) {

    boolean result = true;

    list_counter_example_t1.clear();
    list_counter_example_t2.clear();
    list_counter_example_machine.clear();

    /* For all StateMachines */
    Iterator<StateMachine> it = m.iterator();
    while (it.hasNext()) {
      StateMachine machine = it.next();

      /* For all states within the given state machine */
      Iterator<State> it_states = machine.iteratorStates();
      while (it_states.hasNext()) {
        State state = it_states.next();
        Transition[] transitions = state.toArray();

        /* For all couple of different transitions */
        for (int i = 0; i < transitions.length; i++) {
          Transition t1 = transitions[i];
          Formula t1_formula = t1.getCondition();
          Events t1_events = t1.getEvents();

          for (int j = i + 1; j < transitions.length; j++) {
            Transition t2 = transitions[j];
            Formula t2_formula = t2.getCondition();
            Events t2_events = t2.getEvents();

            /* If both transitions are labeled with a common event */
            if (t1_events.notEmptyIntersection(t2_events)) {

              CNFFormula formula = CNFFormula.ConvertToCNF(
                  new AndFormula(t1_formula, t2_formula));

              try {
                if (solver.isSatisfiable(formula)) {
                  if (identicalActionFields(t1, t2) &&
                      identicalTarget(t1, t2)) {

                    System.out.println("[Notice]Non exclusive transitions " +
                        "not considered as an error since they have the same" +
                        " target and actions.");
                    System.out.println(errorMessage(machine, t1, t2, solver
                        .solution()));
                    continue;
                  }
                  list_counter_example_machine.add(machine);
                  list_counter_example_t1.add(t1);
                  list_counter_example_t2.add(t2);
                  solution_details.add(solver.solution());

                  result = false;
                  if (!check_all) {
                    if (verbose) {
                      System.out
                          .println("[FAILURE] Transitions exclusion not verified.\n");
                      System.out.println(errorMessage(machine, t1, t2,
                          solver.solution()));
                    }
                    return false;
                  }
                }
              } catch (TimeoutException e) {
                e.printStackTrace();
                throw new RuntimeException(
                    "[FAILURE]TIMEOUT during a SAT solving.");
              }
            }
          }
        }
      }
    }

    if (verbose && result) {
      System.out.println(successMessage());
    } else if (verbose && !result) {
      System.out.println(errorMessage());
    }
    return result;
  }

  @Override
  public boolean check(Model m, boolean verbose) {
    return checkFunction(m, verbose, false);
  }

  @Override
  public boolean checkAll(Model m, boolean verbose) {
    return checkFunction(m, verbose, true);
  }

  private String errorMessage(StateMachine counter_example_machine,
      Transition counter_example_t1, Transition counter_example_t2,
      String solution_details) {
    return "In the state machine "
        + counter_example_machine.getName() +
        ", the transitions \n" +
        counter_example_t1 + " \n" +
        "and \n" +
        counter_example_t2 + "\n" +
        " are not exclusive. \n" +
        " Here is the details of the not exlusivity: " +
        solution_details;
  }

  @Override
  public String errorMessage() {
    StringBuffer result = new StringBuffer();
    int n = list_counter_example_machine.size();
    result.append("[FAILURE] " + n + " errors have been found.\n");
    for (int i = 0; i < n; i++) {
      result.append(errorMessage(list_counter_example_machine.get(i),
          list_counter_example_t1.get(i), list_counter_example_t2.get(i),
          solution_details.get(i)) + "\n");
    }

    return result.toString();
  }

  @Override
  public String successMessage() {
    if (list_counter_example_machine.isEmpty()) {
      return "[SUCCESS] Checking that transitions are exlusives, ensuring determinism...OK";
    } else {
      throw new IllegalArgumentException(
          "The last call to check returned with errors."
              + "You should not be calling sucessMessage().");
    }
  }

}
