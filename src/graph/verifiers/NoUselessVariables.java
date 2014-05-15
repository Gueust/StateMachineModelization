package graph.verifiers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Vector;

import abstractGraph.conditions.Variable;
import graph.Model;
import graph.StateMachine;

/**
 * Check that the graphs don't contain useless variables. Which means that we
 * can't find a variable with no graph to write on it or a variable written but
 * never used.
 */
public class NoUselessVariables extends AbstractVerificationUnit {
  private Vector<Variable> counter_example_not_used;
  private Vector<Variable> counter_example_not_writen;

  /**
   * The type of the last error encountered.
   * 
   */
  private enum Error {
    NONE, /* No error encountered */
    NOT_USED, /* A variable is written but never used */
    NOT_WRITTEN, /* A variable is used but never written */
  };

  Error error_type;

  @Override
  public boolean checkAll(Model m, boolean verbose) {
    boolean found_not_written = true;
    boolean found_not_used = true;
    counter_example_not_used = new Vector<Variable>();
    counter_example_not_writen = new Vector<Variable>();
    error_type = Error.NONE;
    HashMap<Variable, LinkedList<StateMachine>> writen_variables =
        m.getWritingStateMachines();
    Iterator<Variable> condition_variable = m.iteratorExistingVariables();

    /*
     * Test that all the variable that are used (i.e. appears in a Condition
     * field) are written.
     */
    while (condition_variable.hasNext()) {
      Variable variable = condition_variable.next();
      if (!writen_variables.containsKey(variable)) {
        counter_example_not_writen.add(variable);
        found_not_written = false;
      }
    }
    if (verbose && !found_not_written) {
      error_type = Error.NOT_WRITTEN;
      System.out.println(errorMessage());
    }

    /*
     * Test that all the variable that are written are used.
     */

    Iterator<Entry<Variable, LinkedList<StateMachine>>> written_variables_iterator =
        m.writingRightsIterator();
    while (written_variables_iterator.hasNext()) {
      Entry<Variable, LinkedList<StateMachine>> entry = written_variables_iterator
          .next();
      if (!m.getConditionVariable().contains(entry.getKey())) {
        counter_example_not_used.add(entry.getKey());
        found_not_used = false;
      }
    }
    if (verbose && !found_not_used) {
      error_type = Error.NOT_USED;
      System.out.println(errorMessage());
    }

    if (!found_not_written || !found_not_used) {
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
    switch (error_type) {
    case NOT_WRITTEN:
      return "[FAILURE] The variables that follow are used but never written "
          + counter_example_not_writen.toString();
    case NOT_USED:
      return "[FAILURE] The variables that folow are written but never used"
          + counter_example_not_used.toString();
    default:
      return null;
    }

  }

  @Override
  public String successMessage() {
    return "[SUCCESS] Checking that all variables are written and used...OK";
  }

}
