package graph.verifiers;

import graph.Model;
import graph.StateMachine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import abstractGraph.conditions.Variable;

/**
 * Check that all the variables in the graphs are written exactly once.
 */
public class SingleWritingChecker extends AbstractVerificationUnit {
  private HashMap<Variable, LinkedList<StateMachine>> counter_example_written_more_than_once =
      new HashMap<Variable, LinkedList<StateMachine>>();

  @Override
  public boolean checkAll(Model m, boolean verbose) {

    boolean is_error = false;

    counter_example_written_more_than_once.clear();

    HashMap<Variable, LinkedList<StateMachine>> written_variables =
        m.getWritingStateMachines();
    Iterator<Variable> variables = m.iteratorExistingVariables();

    while (variables.hasNext()) {
      Variable variable = variables.next();
      LinkedList<StateMachine> writing_state_machine =
          written_variables.get(variable);

      if (writing_state_machine != null) {
        switch (writing_state_machine.size()) {
        case 0:
          break;
        case 1:
          break;
        default:
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
    return "[SUCCESS] Checking that all variables are written exactly once...OK";
  }

  private String myPrint(HashMap<Variable, LinkedList<StateMachine>> input) {
    StringBuffer result = new StringBuffer();

    Iterator<Entry<Variable, LinkedList<StateMachine>>> iterator = input
        .entrySet()
        .iterator();
    LinkedList<StateMachine> states_machines_list;

    while (iterator.hasNext()) {
      Entry<Variable, LinkedList<StateMachine>> entry = iterator.next();
      Variable variable = entry.getKey();
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
