package Graph;

import java.io.IOException;
import java.util.HashMap;

import abstractGraph.Conditions.Condition;
import abstractGraph.Events.SingleEvent;
import Graph.Events.Actions;
import Parser_Fichier_6lignes.Fichier6lignes;

public class GraphFactory {

  private HashMap<String, StateMachine> state_machines;

  public GraphFactory(String file) throws IOException {

    state_machines = new HashMap<String, StateMachine>();

    Fichier6lignes parser = new Fichier6lignes(file);

    while (parser.get6Lines()) {
      // Get the name of the state_machine
      StateMachine state_machine = getStateMachine(parser.getGraphName());
      // Get the source state
      State from = state_machine.getState(parser.getSourceState());
      // Get the destination state
      State to = state_machine.getState(parser.getDestinationState());

      state_machine.addTransition(from, to, getEvent(parser.getEvent()),
          getCondition(parser.getCondition()), getAction(parser.getAction()));
    }
  }

  /**
   * Get the state machine with the name. And if it doesn't exist, it creates
   * one.
   * 
   * @param name
   * @return
   */
  private StateMachine getStateMachine(String name) {
    StateMachine result = state_machines.get(name);
    if (result != null) {
      return result;
    } else {
      result = new StateMachine(name);
      state_machines.put(name, result);
      return result;
    }
  }

  private State getState(String machine_name, String state_name) {
    StateMachine state_machine = getStateMachine(machine_name);

    State s = state_machine.getState(state_name);
    if (s == null) {
      return state_machine.addState(state_name);
    } else {
      return s;
    }
  }

  private SingleEvent getEvent(String event) {
    return null;
  }

  private Actions getAction(String action) {
    return null;
  }

  private Condition getCondition(String condition) {
    return null;
  }
}
