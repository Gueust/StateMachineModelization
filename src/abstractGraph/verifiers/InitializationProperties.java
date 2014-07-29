package abstractGraph.verifiers;

import engine.SequentialGraphSimulator;

import java.util.HashSet;

import abstractGraph.AbstractModel;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.events.SingleEvent;

/**
 * Check that for each state machine:
 * <ol>
 * <li>there is a state "0"</li>
 * <li>the event field of the transitions of that state is ACT_INIT</li>
 * <li>there are only CTLs as variables in the condition of these
 * transitions</li>
 */
public class InitializationProperties<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>>
    extends AbstractVerificationUnit<M, S, T> {
  private HashSet<M> state_machine_without_state_0 =
      new HashSet<M>();
  private HashSet<M> state_machine_with_act_init_error =
      new HashSet<M>();
  private HashSet<M> state_machine_with_ctl_error =
      new HashSet<M>();

  @Override
  public boolean check(AbstractModel<M, S, T> m, boolean verbose) {
    boolean result = checkProperty(m, false);
    if (result && verbose) {
      System.out.print(successMessage());
    } else if (!result && verbose) {
      System.out.print(errorMessage());
    }
    return result;
  }

  @Override
  public boolean checkAll(AbstractModel<M, S, T> m, boolean verbose) {
    boolean result = checkProperty(m, true);
    if (result && verbose) {
      System.out.print(successMessage());
    } else if (!result && verbose) {
      System.out.print(errorMessage());
    }
    return result;
  }

  private boolean checkProperty(AbstractModel<M, S, T> m, boolean check_all) {

    state_machine_with_act_init_error.clear();
    state_machine_with_ctl_error.clear();
    state_machine_without_state_0.clear();

    for (M state_machine : m) {
      if (checkInitialStateExistence(state_machine)) {
        S state = state_machine.getState("0");
        for (T transition : state) {
          checkTransition(state_machine, transition, !check_all);
        }
      }
    }
    return (state_machine_without_state_0.size() == 0
        && state_machine_with_act_init_error.size() == 0
        && state_machine_with_ctl_error.size() == 0);

  }

  private boolean checkInitialStateExistence(M state_machine) {
    if (state_machine.getState("0") == null) {
      state_machine_without_state_0.add(state_machine);
      return false;
    }
    return true;
  }

  private boolean checkTransition(M state_machine,
      T transition, boolean stop_at_first_error) {
    transition.getEvents()
        .containsEvent(SequentialGraphSimulator.ACT_INIT);
    for (SingleEvent event : transition.getEvents()) {
      if (!event.getName().equals(SequentialGraphSimulator.ACT_INIT.getName())) {
        state_machine_with_act_init_error.add(state_machine);
        if (stop_at_first_error) {
          return false;
        }
      }
    }
    HashSet<EnumeratedVariable> variable_list = new HashSet<>();
    if (transition.getCondition() != null) {
      for (EnumeratedVariable variable : transition
          .getCondition()
          .allVariables(variable_list)) {
        if (!variable.getVarname().startsWith("CTL_")) {
          state_machine_with_ctl_error.add(state_machine);
          if (stop_at_first_error) {
            return false;
          }
        }
      }
    }
    return true;
  }

  private String extractStateMachineName(Iterable<M> machines) {
    String result = "";
    boolean first = true;
    for (M state_machine : machines) {
      String state_machine_name = state_machine.getName();
      if (first) {
        result += state_machine_name;
        first = false;
      } else {
        result += ", " + state_machine_name;
      }
    }
    return result;
  }

  @Override
  public String errorMessage() {
    StringBuffer error = new StringBuffer();
    if (!state_machine_without_state_0.isEmpty()) {
      error.append("[FAILURE]These state machines don't have a state 0: \n"
          + extractStateMachineName(state_machine_without_state_0) + "\n");
    }
    if (!state_machine_with_act_init_error.isEmpty()) {
      error.append("[FAILURE]These state machines have an event different"
          + " than " + SequentialGraphSimulator.ACT_INIT
          + " in their transition: \n"
          + extractStateMachineName(state_machine_with_act_init_error)
          + "\n");
    }
    if (!state_machine_with_ctl_error.isEmpty()) {
      error.append("[FAILURE]These state machines have other variable than "
          + "CTL in the condition field of their initial state: \n"
          + extractStateMachineName(state_machine_with_ctl_error) + "\n");
    }
    return error.toString();
  }

  @Override
  public String successMessage() {
    return "[SUCCESS] Checking the initialization constraints (i.e. all the "
        + "state machines have a state 0, ACT_init is the only event in the "
        + "event field of the transitions from a 0 state and they have only "
        + "CTLs in the condition fields)...OK\n";
  }

}
