package abstractGraph.verifiers;

import engine.SequentialGraphSimulator;

import java.util.Iterator;
import java.util.LinkedList;

import org.sat4j.specs.TimeoutException;

import solver.SAT4JSolver;
import abstractGraph.AbstractModel;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.NotFormula;
import abstractGraph.conditions.OrFormula;
import abstractGraph.conditions.True;
import abstractGraph.conditions.cnf.CNFFormula;
import abstractGraph.events.Events;

/**
 * This verifier checks that for automata, the union of the guards of the
 * transitions leaving from state 0 is a tautology.
 * 
 * It will check that all the transitions leaving from 0 are labeled with
 * ACT_INIT.
 * 
 * It requires that the InitializationProperties checker is called first.
 */
public class TautologyFromStateZero<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>>
    extends AbstractVerificationUnit<M, S, T> {

  /*
   * The solver used to solve the SAT instances. It can be used multiple times
   * for different formulas
   */
  private static SAT4JSolver solver = new SAT4JSolver();

  /*
   * The counter examples:
   * - the state machine
   * - the concerned state
   * Because we use a single solver, we need to save the solution.
   */
  private LinkedList<M> counter_example_machines =
      new LinkedList<M>();
  private LinkedList<S> counter_example_states = new LinkedList<S>();
  private LinkedList<Formula> counter_example_formula =
      new LinkedList<Formula>();
  private LinkedList<String> solution_details =
      new LinkedList<String>();

  /**
   * Serves for both the {@link #check(AbstractModel, boolean)} and
   * {@link #checkAll(AbstractModel, boolean)} functions.
   */
  private boolean checkFunction(AbstractModel<M, S, T> m, boolean verbose,
      boolean check_all) {

    boolean result = true;

    counter_example_machines.clear();
    counter_example_states.clear();
    counter_example_formula.clear();
    solution_details.clear();

    /* For all StateMachines */
    Iterator<M> it = m.iterator();
    while (it.hasNext()) {
      M machine = it.next();

      /* We get the state 0 */
      S state = machine.getState("0");
      if (state == null) {
        throw new Error("There is no state 0 within " + machine.getName());
      }

      Formula union = True.FALSE;

      /* We build the union of the guards */
      for (T transition : state) {
        /* We check that the transition is labeled with ACT_INIT */
        Events events = transition.getEvents();
        if (!events.containsEvent(SequentialGraphSimulator.ACT_INIT)) {
          throw new Error("The following transition is not labeled with "
              + SequentialGraphSimulator.ACT_INIT + ":\n" + transition);
        }
        /* We enlarge the union */
        union = new OrFormula(union, transition.getCondition());
      }

      /* We check that the union is a tautology */
      CNFFormula formula = CNFFormula.ConvertToCNF(new NotFormula(union));

      try {
        if (solver.isSatisfiable(formula)) {

          counter_example_machines.add(machine);
          counter_example_states.add(state);
          counter_example_formula.add(formula);
          solution_details.add(solver.solution());

          result = false;
          if (!check_all && verbose) {
            System.out.println(errorMessage());
            return false;
          }
        }
      } catch (TimeoutException e) {
        e.printStackTrace();
        throw new RuntimeException(
            "[FAILURE]TIMEOUT during a SAT solving.");
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
  public boolean check(AbstractModel<M, S, T> m, boolean verbose) {
    return checkFunction(m, verbose, false);
  }

  @Override
  public boolean checkAll(AbstractModel<M, S, T> m, boolean verbose) {
    return checkFunction(m, verbose, true);
  }

  private String errorMessage(M counter_example_machine,
      S counter_example_t1, Formula formula,
      String solution_details) {
    return "In the state machine "
        + counter_example_machine.getName()
        + ", the transitions leaving from \n"
        + counter_example_t1
        + " \n does not form a tautology. The formula was: \n" +
        formula + "Here is the details of for the "
        + "initial values that will not trigger any transition:"
        + solution_details;
  }

  @Override
  public String errorMessage() {
    StringBuffer result = new StringBuffer();
    int n = counter_example_machines.size();
    result.append("[FAILURE] " + n + " errors have been found.\n");
    for (int i = 0; i < n; i++) {
      result.append(errorMessage(
          counter_example_machines.get(i),
          counter_example_states.get(i),
          counter_example_formula.get(i),
          solution_details.get(i)) + "\n");
    }

    return result.toString();
  }

  @Override
  public String successMessage() {
    if (counter_example_machines.isEmpty()) {
      return "[SUCCESS] Checking that after the initialization, all automata leave the state 0...OK";
    } else {
      throw new IllegalArgumentException(
          "The last call to check returned with errors."
              + "You should not be calling sucessMessage().");
    }
  }

}
