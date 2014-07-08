package graph.verifiers;

import graph.Model;
import graph.StateMachine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import abstractGraph.conditions.BooleanVariable;

/**
 * Check that all the variables in the graphs are written at most once
 */
public class SingleWritingChecker extends AbstractVerificationUnit {
  private HashMap<BooleanVariable, LinkedList<StateMachine>> counter_example_written_more_than_once =
      new HashMap<BooleanVariable, LinkedList<StateMachine>>();

  @Override
  public boolean checkAll(Model m, boolean verbose) {

    boolean is_error = false;

    counter_example_written_more_than_once.clear();

    HashMap<BooleanVariable, LinkedList<StateMachine>> written_variables =
        m.getWritingStateMachines();
    Iterator<BooleanVariable> variables = m.iteratorExistingVariables();

    while (variables.hasNext()) {
      BooleanVariable variable = variables.next();
      LinkedList<StateMachine> writing_state_machine =
          written_variables.get(variable);

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
  public boolean check(Model m, boolean verbose) {
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

  private String myPrint(HashMap<BooleanVariable, LinkedList<StateMachine>> input) {
    StringBuffer result = new StringBuffer();

    Iterator<Entry<BooleanVariable, LinkedList<StateMachine>>> iterator = input
        .entrySet()
        .iterator();
    LinkedList<StateMachine> states_machines_list;

    while (iterator.hasNext()) {
      Entry<BooleanVariable, LinkedList<StateMachine>> entry = iterator.next();
      BooleanVariable variable = entry.getKey();
      states_machines_list = entry.getValue();

      result.append(
          "The variable " + variable + " in the states machine: \n");
      for (StateMachine stateMachine : states_machines_list) {
        result.append(stateMachine.getName() + " ; ");
      }
    }
    return result.toString();
  }
}
