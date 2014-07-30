package abstractGraph.verifiers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import abstractGraph.AbstractModel;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import abstractGraph.conditions.EnumeratedVariable;

/**
 * Check that all the variables in the graphs are written at most once
 */
public class SingleWritingChecker<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>>
    extends AbstractVerificationUnit<M, S, T> {
  private HashMap<EnumeratedVariable, Collection<M>> counter_example_written_more_than_once =
      new HashMap<>();

  @Override
  public boolean checkAll(AbstractModel<M, S, T> m, boolean verbose) {

    boolean is_error = false;

    counter_example_written_more_than_once.clear();

    HashMap<EnumeratedVariable, Collection<M>> written_variables =
        m.getWritingStateMachines();

    assert (written_variables != null);
    for (EnumeratedVariable variable : m.getExistingVariables()) {

      Collection<M> writing_state_machine = written_variables.get(variable);

      if (writing_state_machine != null) {
        if (writing_state_machine.size() > 1) {
          is_error = true;
          counter_example_written_more_than_once.put(variable,
              writing_state_machine);
        }
      }
    }

    if (is_error && verbose) {
      System.out.println(errorMessage());
    }

    if (!is_error && verbose) {
      System.out.println(successMessage());
    }

    return !is_error;
  }

  @Override
  public boolean check(AbstractModel<M, S, T> m, boolean verbose) {
    return checkAll(m, verbose);
  }

  @Override
  public String errorMessage() {
    StringBuffer result = new StringBuffer();

    result.append(
        "[FAILURE] The variables that follow are written more than once :\n"
            + myPrint(counter_example_written_more_than_once) + "\n");
    return result.toString();
  }

  @Override
  public String successMessage() {
    return "[SUCCESS] Checking that all variables are written at most once...OK";
  }

  private String myPrint(
      HashMap<EnumeratedVariable, Collection<M>> input) {
    StringBuffer result = new StringBuffer();

    Iterator<Entry<EnumeratedVariable, Collection<M>>> iterator = input
        .entrySet()
        .iterator();
    Collection<M> states_machines_list;

    while (iterator.hasNext()) {
      Entry<EnumeratedVariable, Collection<M>> entry = iterator
          .next();
      EnumeratedVariable variable = entry.getKey();
      states_machines_list = entry.getValue();

      result.append(
          "The variable " + variable + " in the states machine: \n");
      for (M stateMachine : states_machines_list) {
        result.append(stateMachine.getName() + " ; ");
      }
      result.append("\n");
    }
    return result.toString();
  }
}
