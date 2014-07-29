package abstractGraph.verifiers;

import java.util.HashSet;
import java.util.Iterator;

import abstractGraph.AbstractModel;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.events.Assignment;
import abstractGraph.events.SingleEvent;
import abstractGraph.events.VariableChange;

/**
 * Check that the graphs does not contain useless variables.A variable is
 * useless if it is not used in any Event or Contion field.
 */
public class NoUselessVariables<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>>
    extends AbstractVerificationUnit<M, S, T> {
  private HashSet<EnumeratedVariable> counter_example_not_used = new HashSet<>();

  @Override
  public boolean checkAll(AbstractModel<M, S, T> m, boolean verbose) {
    boolean error = false;

    counter_example_not_used.clear();

    /*
     * We create the hashMap of the variables that are contained ONLY in the
     * conditions fields and in the events fields.
     */
    HashSet<EnumeratedVariable> variables_in_conditions_and_events = new HashSet<>();
    Iterator<M> it_sm = m.iterator();
    /* For all state machines */
    while (it_sm.hasNext()) {
      M sm = it_sm.next();
      Iterator<T> it_trans = sm.iteratorTransitions();
      /* For all transitions */
      while (it_trans.hasNext()) {
        T transition = it_trans.next();

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

    for (M machine : m) {
      for (S state : machine) {
        for (T transition : state) {
          for (SingleEvent single_event : transition.getActions()) {

            if (single_event instanceof VariableChange) {
              BooleanVariable variable =
                  ((VariableChange) single_event).getModifiedVariable();

              /*
               * We check that the variable is found in a Condition or event
               * field
               */
              if (!variables_in_conditions_and_events.contains(variable)) {
                counter_example_not_used.add(variable);
                error = true;
              }
            } else if (single_event instanceof Assignment) {
              EnumeratedVariable variable =
                  ((Assignment) single_event).getVariable();

              if (!variables_in_conditions_and_events.contains(variable)) {
                counter_example_not_used.add(variable);
                error = true;
              }
            }

          }
        }
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
  public boolean check(AbstractModel<M, S, T> m, boolean verbose) {
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
