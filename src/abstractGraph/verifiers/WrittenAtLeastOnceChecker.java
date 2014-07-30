package abstractGraph.verifiers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import abstractGraph.AbstractModel;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import abstractGraph.conditions.EnumeratedVariable;

public class WrittenAtLeastOnceChecker<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>>
    extends AbstractVerificationUnit<M, S, T> {

  private HashSet<EnumeratedVariable> counter_example_not_writen = new HashSet<>();

  @Override
  public boolean checkAll(AbstractModel<M, S, T> m, boolean verbose) {

    boolean is_error = false;

    counter_example_not_writen.clear();

    HashMap<EnumeratedVariable, Collection<M>> written_variables =
        m.getWritingStateMachines();

    for (EnumeratedVariable variable : m.getExistingVariables()) {

      if (!variable.getVarname().startsWith("CTL")) {
        Collection<M> writing_state_machine =
            written_variables.get(variable);

        if (writing_state_machine != null) {
          if (writing_state_machine.size() == 0) {
            is_error = true;
            counter_example_not_writen.add(variable);
          }
        } else {
          is_error = true;
          counter_example_not_writen.add(variable);
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
    int number_not_written = counter_example_not_writen.size();

    result.append("[WARNING] " + number_not_written +
        " variables are never written. The list is following :\n"
        + counter_example_not_writen.toString() + "\n");

    return result.toString();
  }

  @Override
  public String successMessage() {
    return "[SUCCESS] Checking that all variables are written at least once...OK";
  }
}
