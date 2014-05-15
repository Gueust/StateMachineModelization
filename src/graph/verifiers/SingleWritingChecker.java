package graph.verifiers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import abstractGraph.conditions.Variable;
import graph.Model;
import graph.StateMachine;

/**
 * Check that all the variables in the graphs are wrote at least once and by
 * only one graph.
 */
public class SingleWritingChecker extends AbstractVerificationUnit {
  private Vector<Variable> counter_example_written_more_than_once =
      new Vector<Variable>();
  private HashMap<Variable, LinkedList<StateMachine>> counter_example_state_machine =
      new HashMap<Variable, LinkedList<StateMachine>>();
  private Vector<Variable> counter_example_not_writen;

  /**
   * The type of the last error encountered.
   * 
   */
  private enum Error {
    NONE, /* No error encountered */
    WRITTEN_MORE_THAN_ONCE, /* A variable is written more than once */
    NOT_WRITTEN, /* A variable is used but never written */
  };

  Error error_type;

  @Override
  public boolean checkAll(Model m, boolean verbose) {
    boolean found_not_written = true;
    boolean found_written_more_than_once = true;
    counter_example_not_writen = new Vector<Variable>();
    error_type = Error.NONE;
    HashMap<Variable, LinkedList<StateMachine>> written_variables =
        m.getWritingStateMachines();
    Iterator<Variable> condition_variable = m.iteratorExistingVariables();

    /*
     * Test that all the variable that are used (i.e. appears in a Condition
     * field) are written.
     */
    while (condition_variable.hasNext()) {
      Variable variable = condition_variable.next();
      LinkedList<StateMachine> writing_state_machine = written_variables
          .get(variable);
      if (writing_state_machine != null) {
        switch (writing_state_machine.size()) {
        case 0:
          counter_example_not_writen.add(variable);
          found_not_written = false;
          break;
        case 1:
          break;
        default:
          error_type = Error.WRITTEN_MORE_THAN_ONCE;
          counter_example_written_more_than_once.add(variable);
          counter_example_state_machine.put(variable, writing_state_machine);
          found_written_more_than_once = false;
          break;
        }
      } else {
        counter_example_not_writen.add(variable);
        found_not_written = false;
      }
    }
    if (verbose && !found_not_written) {
      error_type = Error.NOT_WRITTEN;
      System.out.println(errorMessage());
    }
    if (verbose && !found_written_more_than_once) {
      error_type = Error.WRITTEN_MORE_THAN_ONCE;
      System.out.println(errorMessage());
    }
    if (!found_not_written || !found_written_more_than_once) {
      return false;
    }

    if (verbose) {
      System.out.println(successMessage());
    }
    return true;

  }

  @Override
  public boolean check(Model m, boolean verbose) {
    return checkAll(m, verbose);
  }

  @Override
  public String errorMessage() {
    switch (error_type) {
    case NOT_WRITTEN:
      return "[FAILURE] The variables that follow are used but never written "
          + counter_example_not_writen.toString();
    case WRITTEN_MORE_THAN_ONCE:
      return "[FAILURE] The variables that folow are written more than once :\n"
          + myPrint(counter_example_state_machine);
    default:
      return null;
    }

  }

  @Override
  public String successMessage() {
    return "[SUCCESS] Checking that all variables are written and used...OK";
  }
  
  private String myPrint(HashMap<Variable, LinkedList<StateMachine>> input){
    String debug = "";
    Iterator<Variable> iterator = input.keySet().iterator();
    LinkedList<StateMachine> states_machines_list = new LinkedList<StateMachine>(); 
    while (iterator.hasNext()){
      Variable variable = iterator.next();
      debug = debug + "The variable " + variable + " in the states machine : ";
      states_machines_list = input.get(variable);
      for (StateMachine stateMachine : states_machines_list) {
        debug = debug + stateMachine.getName()+"  ";
      }
      debug = debug+ "\n";
    }
    return debug;
  }

}
