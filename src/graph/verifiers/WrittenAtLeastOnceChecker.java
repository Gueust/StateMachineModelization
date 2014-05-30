package graph.verifiers;

import graph.Model;
import graph.StateMachine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import abstractGraph.conditions.Variable;

public class WrittenAtLeastOnceChecker extends AbstractVerificationUnit {

  private HashSet<Variable> counter_example_not_writen = new HashSet<Variable>();

  @Override
  public boolean checkAll(Model m, boolean verbose) {

    boolean is_error = false;

    counter_example_not_writen.clear();

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
          is_error = true;
          counter_example_not_writen.add(variable);
          break;
        case 1:
          break;
        }
      } else {
        is_error = true;
        counter_example_not_writen.add(variable);
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
        "[WARNING] The variables that follow are never written :\n"
            + counter_example_not_writen.toString() + "\n");

    return result.toString();
  }

  @Override
  public String successMessage() {
    return "[SUCCESS] Checking that all variables are written exactly once...OK";
  }
}
