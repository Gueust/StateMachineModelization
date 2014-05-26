package graph.verifiers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import abstractGraph.conditions.Variable;
import abstractGraph.events.VariableChange;
import graph.Model;
import graph.StateMachine;
import graph.Transition;

/**
 * Check that the graphs don't contain useless variables. Which means that a
 * variable found in the action field must be found in the field condition or
 * event.
 */
public class NoUselessVariables extends AbstractVerificationUnit {
  private Vector<Variable> counter_example_not_used = new Vector<Variable>();

  @Override
  public boolean checkAll(Model m, boolean verbose) {
    boolean no_error = true;

    counter_example_not_used.clear();

    Iterator<VariableChange> variable_chenge_iterator =
        m.iteratorVariableChange();

    while (variable_chenge_iterator.hasNext()) {
      boolean found_variable = false;
      VariableChange variable_change = variable_chenge_iterator.next();
      Variable variable = variable_change.getModifiedVariable();

      /* We check that the variable is found in a Condition field */
      if (!m.getConditionVariable().contains(variable)) {
        /*
         * If the variable isn't in a condition field, we search for it in the
         * event field
         */
        Iterator<StateMachine> state_machine_iterator =
            m.iteratorStatesMachines();

        while (state_machine_iterator.hasNext()) {
          StateMachine state_machine = state_machine_iterator.next();
          LinkedList<Transition> transitions =
              state_machine.getTransitions(variable_change);
          if (transitions.size() != 0) {
            found_variable = true;
            break;
          }
        }
        if (!found_variable) {
          counter_example_not_used.add(variable);
          no_error = false;
        }
      }
    }

    if (verbose && !no_error) {
      System.out.println(errorMessage());
    }

    if (verbose && no_error) {
      System.out.println(successMessage());
    }

    return no_error;
  }

  @Override
  public boolean check(Model m, boolean verbose) {
    return checkAll(m, verbose);
  }

  @Override
  public String errorMessage() {
    return "[FAILURE] The variables that follow are written but never used: \n"
        + counter_example_not_used.toString();
  }

  @Override
  public String successMessage() {
    return "[SUCCESS] Checking that all variables are written and used...OK";
  }
}
