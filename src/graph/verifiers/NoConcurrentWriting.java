package graph.verifiers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import abstractGraph.conditions.Variable;
import graph.Model;
import graph.StateMachine;

/**
 * This verifier checks that every global variable is modified by only one state
 * machine.
 */
public class NoConcurrentWriting extends AbstractVerificationUnit {

  private Entry<Variable, LinkedList<StateMachine>> counter_example;

  @Override
  public boolean check(Model m, boolean verbose) {
    Iterator<Entry<Variable, LinkedList<StateMachine>>> it =
        m.writingRightsIterator();
    while (it.hasNext()) {
      Entry<Variable, LinkedList<StateMachine>> entry = it.next();
      if (entry.getValue().size() != 1) {
        counter_example = entry;
        if (verbose) {
          System.out.println(errorMessage());
        }
        return false;
      }
    }
    if (verbose) {
      System.out.println(successMessage());
    }
    return true;
  }

  @Override
  public String errorMessage() {
    int nb_writers = counter_example.getValue().size();
    return "[FAILURE] The variable " + counter_example.getKey()
        + " is written by " + nb_writers + " state machines.\n"
        + "The concerned states machines are the followings:\n"
        + counter_example.getValue();
  }

  @Override
  public String successMessage() {
    return "[SUCCESS] Checking that all variable is modified by only 1 state machine...OK";
  }

}
