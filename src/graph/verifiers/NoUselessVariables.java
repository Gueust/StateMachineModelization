package graph.verifiers;

import graph.Model;
import graph.StateMachine;
import graph.Transition;

import java.util.HashSet;
import java.util.Iterator;

import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.events.SingleEvent;
import abstractGraph.events.VariableChange;

/**
 * Check that the graphs does not contain useless variables.A variable is
 * useless if it is not used in any Event or Contion field.
 */
public class NoUselessVariables extends AbstractVerificationUnit {
  private HashSet<BooleanVariable> counter_example_not_used = new HashSet<BooleanVariable>();

  @Override
  public boolean checkAll(Model m, boolean verbose) {
    boolean error = false;

    counter_example_not_used.clear();

    /*
     * We create the hashMap of the variables that are contained ONLY in the
     * conditions fields and in the events fields.
     */
    HashSet<EnumeratedVariable> variables_in_conditions_and_events = new HashSet<>();
    Iterator<StateMachine> it_sm = m.iterator();
    /* For all state machines */
    while (it_sm.hasNext()) {
      StateMachine sm = it_sm.next();
      Iterator<Transition> it_trans = sm.iteratorTransitions();
      /* For all transitions */
      while (it_trans.hasNext()) {
        Transition transition = it_trans.next();

        /* We add the variables in the event field. */
        for (SingleEvent event : transition.getEvents()) {
          if (event instanceof VariableChange) {
            variables_in_conditions_and_events.add(
                ((VariableChange) event).getModifiedVariable());
          }
        }

        /* We add the variables within the condition field */
        Formula condition = transition.getCondition();
        if (condition != null) {
          condition.allVariables(variables_in_conditions_and_events);
        }
      }
    }

    Iterator<VariableChange> variable_change_iterator =
        m.iteratorVariableChange();

    while (variable_change_iterator.hasNext()) {
      BooleanVariable variable = variable_change_iterator
          .next()
          .getModifiedVariable();

      /* We check that the variable is found in a Condition or event field */
      if (!variables_in_conditions_and_events.contains(variable)) {
        counter_example_not_used.add(variable);
        error = true;
      }
    }

    if (verbose && error) {
      System.out.println(errorMessage());
    }

    if (verbose && !error) {
      System.out.println(successMessage());
    }

    return !error;
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
    return "[SUCCESS] Checking that all variables that are written are used...OK";
  }
}
