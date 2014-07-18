package graph.verifiers;

import graph.Model;
import graph.StateMachine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import abstractGraph.conditions.EnumeratedVariable;

public class WrittenAtLeastOnceChecker extends AbstractVerificationUnit {

  private HashSet<EnumeratedVariable> counter_example_not_writen = new HashSet<>();

  @Override
  public boolean checkAll(Model m, boolean verbose) {

    boolean is_error = false;

    counter_example_not_writen.clear();

    HashMap<EnumeratedVariable, LinkedList<StateMachine>> written_variables =
        m.getWritingStateMachines();
    Iterator<EnumeratedVariable> variables = m.iteratorExistingVariables();

    while (variables.hasNext()) {
      EnumeratedVariable variable = variables.next();
      if (!variable.getVarname().startsWith("CTL")) {
        LinkedList<StateMachine> writing_state_machine =
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
  public boolean check(Model m, boolean verbose) {
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
