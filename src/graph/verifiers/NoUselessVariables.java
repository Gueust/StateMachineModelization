package graph.verifiers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import abstractGraph.conditions.Variable;
import graph.Model;
import graph.StateMachine;

/**
 * Check that the graphs don't contain useless variables. Which means that we
 * can't find a variable with no graph to write on it or a variable written but
 * never used.
 */
public class NoUselessVariables extends AbstractVerificationUnit {
  private Variable counter_example;

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
    boolean found = true;
    error_type = Error.NONE;
    HashMap<Variable, LinkedList<StateMachine>> writen_variables =
        m.getWritingStateMachines();
    Iterator<Variable> condition_variable = m.iteratorConditionVariables();

    /*
     * Test that all the variable that are used (i.e. appears in a Condition
     * field) are written.
     */
    while (condition_variable.hasNext()) {
      Variable variable = condition_variable.next();
      if (!writen_variables.containsKey(variable)) {
        counter_example = variable;
        found = false;
        if (verbose) {
          error_type = Error.NOT_WRITTEN;
          System.out.println(errorMessage());
        }
      }
    }

    /*
     * Test that all the variable that are written are used.
     */

    Iterator<Entry<Variable, LinkedList<StateMachine>>> writen_variables_iterator =
        m.writingRightsIterator();
    while (writen_variables_iterator.hasNext()) {
      Entry<Variable, LinkedList<StateMachine>> entry = writen_variables_iterator
          .next();
      if (!m.containsVariable(entry.getKey())) {
        counter_example = entry.getKey();
        found = false;
        if (verbose) {
          error_type = Error.NOT_WRITTEN;
          System.out.println(errorMessage());
        }
      }
    }

    if (!found) {
      return false;
    } else {
      if (verbose) {
        System.out.println(successMessage());
      }
      return true;
    }
  }

  @Override
  public boolean check(Model m, boolean verbose) {
    return checkAll(m, verbose);
  }

  @Override
  public String errorMessage() {
    switch (error_type) {
    case NOT_WRITTEN:
      return "[FAILURE] The variable " + counter_example.toString()
          + " is used but never written";
    case NOT_USED:
      return "[FAILURE] The variable " + counter_example.toString()
          + " is written but never used";
    default:
      return null;
    }

  }

  @Override
  public String successMessage() {
    return "[SUCCESS] Checking that all variables are written and used...OK";
  }

}
