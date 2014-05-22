package graph.verifiers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Vector;

import org.antlr.runtime.EarlyExitException;

import abstractGraph.conditions.Variable;
import abstractGraph.events.VariableChange;
import graph.Model;
import graph.StateMachine;
import graph.Transition;

/**
 * Check that the graphs don't contain useless variables. Which means that we
 * can't find a variable with no graph to write on it or a variable written but
 * never used.
 */
public class NoUselessVariables extends AbstractVerificationUnit {
  private Vector<Variable> counter_example_not_used;

  /**
   * The type of the last error encountered.
   * 
   */
  private enum Error {
    NONE, /* No error encountered */
    NOT_USED, /* A variable is written but never used */
  };

  Error error_type;

  @Override
  public boolean checkAll(Model m, boolean verbose) {
    boolean found_not_used = true;
    boolean found_tmp = false;
    counter_example_not_used = new Vector<Variable>();
    error_type = Error.NONE;

    /*
     * Test that all the variable that are written are used.
     */

    Iterator<VariableChange> variable_chenge_iterator = m
        .iteratorVariableChange();
    while (variable_chenge_iterator.hasNext()) {
      found_tmp = false;
      VariableChange variable_change = variable_chenge_iterator.next();
      Variable variable = variable_change.getModifiedVariable();
      /* We check that the variable is found in the Condition fields */
      if (!m.getConditionVariable().contains(variable)) {
        /*
         * If the variable isn't in a condition field, we search for it in the
         * event field
         */
        Iterator<StateMachine> state_machine_iterator = m
            .iteratorStatesMachines();
        while (state_machine_iterator.hasNext()) {
          StateMachine state_machine = state_machine_iterator.next();
          LinkedList<Transition> transitions = state_machine
              .getTransition(variable_change);
          if (transitions.size() != 0) {
            found_tmp = true;
          }
        }
        if (!found_tmp) {
          counter_example_not_used.add(variable);
          found_not_used = false;
      }
      }
    }

    if (verbose && !found_not_used) {
      error_type = Error.NOT_USED;
      System.out.println(errorMessage());
    }

    if (!found_not_used) {
      return false;
    }

    if (verbose) {
      System.out.println(successMessage());
    }
    return true;

  }

  @Override
  public boolean check(Model m, boolean verbose) {
    return checkAll(m, verbose);
  }

  @Override
  public String errorMessage() {
    return "[FAILURE] The variables that folow are written but never used"
        + counter_example_not_used.toString();
  }

  @Override
  public String successMessage() {
    return "[SUCCESS] Checking that all variables are written and used...OK";
  }

}
