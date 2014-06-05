package graph.verifiers;

import graph.Model;
import graph.StateMachine;
import graph.Transition;

import java.util.HashSet;
import java.util.Iterator;

import abstractGraph.conditions.Variable;
import abstractGraph.events.SingleEvent;

/**
 * Check that for each state machine, we find an initial state, state "0",
 * that the only event in the transition of that state is "ACT_Init",
 * that we can only find CTL as variable in the condition of that transition,
 * and that the action field doesn't contain any synchronization event i.e. ACT
 * or SYN.
 */
public class InitializationProperty extends AbstractVerificationUnit {
  HashSet<StateMachine> state_machine_without_state_0 = new HashSet<StateMachine>();
  HashSet<StateMachine> state_machine_with_act_init_error = new HashSet<StateMachine>();
  HashSet<StateMachine> state_machine_with_ctl_error = new HashSet<StateMachine>();
  HashSet<StateMachine> state_machine_with_syn_error = new HashSet<StateMachine>();

  @Override
  public boolean check(Model m, boolean verbose) {
    if (checkProperty(m, true)) {
      if (verbose) {
        System.out.print(successMessage());
      }
      return true;
    } else {
      if (verbose) {
        System.out.print(errorMessage());
      }
      return false;
    }
  }

  @Override
  public boolean checkAll(Model m, boolean verbose) {
    if (checkProperty(m, false)) {
      if (verbose) {
        System.out.print(successMessage());
      }
      return true;
    } else {
      if (verbose) {
        System.out.print(errorMessage());
      }
      return false;
    }
  }

  private boolean checkProperty(Model m, boolean check) {
    Iterator<StateMachine> state_machine_iterator = m.iterator();
    while (state_machine_iterator.hasNext()) {
      StateMachine state_machine = state_machine_iterator.next();
      if (checkInitialStateExistence(state_machine)) {
        Iterator<Transition> transition_iterator = state_machine
            .getState("0")
            .iteratorTransitions();
        while (transition_iterator.hasNext()) {
          checkTransition(state_machine, transition_iterator.next(),
              check);
        }
      }
    }
    if (state_machine_without_state_0.size() == 0
        && state_machine_with_act_init_error.size() == 0
        && state_machine_with_ctl_error.size() == 0
        && state_machine_with_syn_error.size() == 0) {
      return true;
    } else {
      return false;
    }
  }

  private boolean checkInitialStateExistence(StateMachine state_machine) {
    if (state_machine.getState("0") == null) {
      state_machine_without_state_0.add(state_machine);
      return false;
    }
    return true;
  }

  private boolean checkTransition(StateMachine state_machine,
      Transition transition, boolean check) {
    Iterator<SingleEvent> single_event_iterator = transition
        .getEvents()
        .singleEvent();
    while (single_event_iterator.hasNext()) {
      SingleEvent event = single_event_iterator.next();
      if (!event.getName().equals("ACT_Init")) {
        state_machine_with_act_init_error.add(state_machine);
        if (check) {
          return false;
        }
      }
    }
    HashSet<Variable> variable_list = new HashSet<Variable>();
    Iterator<Variable> variable_iterator = transition
        .getCondition()
        .allVariables(variable_list)
        .iterator();
    while (variable_iterator.hasNext()) {
      Variable variable = variable_iterator.next();
      if (!variable.getVarname().startsWith("CTL_")) {
        state_machine_with_ctl_error.add(state_machine);
        if (check) {
          return false;
        }
      }
    }
    Iterator<SingleEvent> action_iterator = transition.getActions().iterator();
    while (action_iterator.hasNext()) {
      SingleEvent action = action_iterator.next();
      if (action.getName().startsWith("ACT_")
          || action.getName().startsWith("SYN_")) {
        state_machine_with_syn_error.add(state_machine);
        if (check) {
          return false;
        }
      }
    }
    return true;
  }

  private String extractStateMachineName(
      Iterator<StateMachine> state_machine_iterator) {
    String result = "";
    while (state_machine_iterator.hasNext()) {
      String state_machine_name = state_machine_iterator.next().getName();
      if (state_machine_iterator.hasNext()) {
        result = result + state_machine_name + ", ";
      } else {
        result = result + state_machine_name;
      }
    }
    return result;
  }

  @Override
  public String errorMessage() {
    String error = "";
    if (state_machine_without_state_0.size() != 0) {
      error = "[FAILURE]These state machines don't have a state 0 {"
          + extractStateMachineName(state_machine_without_state_0.iterator())
          + "}\n";
    }
    if (state_machine_with_act_init_error.size() != 0) {
      error = "[FAILURE]These state machines have an event different than ACT_Init in their transition {"
          + extractStateMachineName(state_machine_with_act_init_error
              .iterator())
          + "}\n";
    }
    if (state_machine_with_ctl_error.size() != 0) {
      error = "[FAILURE]These state machines have other variable than CTL in the condition field of their initial state {"
          + extractStateMachineName(state_machine_with_ctl_error.iterator())
          + "}\n";
    }
    if (state_machine_with_syn_error.size() != 0) {
      error = "[FAILURE]These state machines have synchronisation event in the action field of their initial state {"
          + extractStateMachineName(state_machine_with_syn_error
              .iterator())
          + "}\n";
    }
    return error;
  }

  @Override
  public String successMessage() {
    return "[SUCCESS] Checking that all the state machines have a state"
        + " 0 with ACT_init as only event and just CTL in conditions...OK";
  }

}
