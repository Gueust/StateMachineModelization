package graph.verifiers;

import java.util.Iterator;

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
 */
public class DeterminismChecker extends AbstractVerificationUnit {

  /*
   * The solver used to solve the SAT instances. It can be used multiple times
   * for different formulas
   */
  private static SAT4JSolver solver = new SAT4JSolver();

  /*
   * The 2 transitions that offer a counter example if needed (i.e. that are not
   * exlusive while beginning at the same state and being activated by a common
   * Event)
   */
  private Transition counter_example_t1, counter_example_t2;

  @Override
  public boolean check(Model m, boolean verbose) {

    /* For all StateMachines */
    Iterator<StateMachine> it = m.iteratorStatesMachines();
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
          Events t1_events = t1.getEvent();

          for (int j = i + 1; j < transitions.length; j++) {
            Transition t2 = transitions[j];
            Formula t2_formula = t2.getCondition();
            Events t2_events = t2.getEvent();

            if (t1_events.notEmptyIntersection(t2_events)) {

              CNFFormula formula = CNFFormula.ConvertToCNF(
                  new AndFormula(t1_formula, t2_formula));

              try {
                if (solver.isSatisfiable(formula)) {
                  counter_example_t1 = t1;
                  counter_example_t2 = t2;
                  if (verbose) {
                    System.out.println(errorMessage());
                  }
                  return false;
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
    if (verbose) {
      System.out.println(successMessage());
    }
    return true;
  }

  @Override
  public String errorMessage() {
    return "[FAILURE] The transitions " + counter_example_t1 + " and "
        + counter_example_t2 +
        " are not exclusive. \n" +
        " Here is the details of the not exlusivity: " +
        solver.solution();
  }

  @Override
  public String successMessage() {
    return "[SUCCESS] Checking that transitions are exlusives, ensuring determinism...OK";
  }

}
