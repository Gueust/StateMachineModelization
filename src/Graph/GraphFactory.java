package Graph;

import java.io.IOException;
import java.util.HashMap;

import Parser_Fichier_6lignes.Fichier6lignes;

public class GraphFactory {

  private HashMap<String, StateMachine> state_machines;

  public GraphFactory(String file) throws IOException {

    state_machines = new HashMap<String, StateMachine>();

    Fichier6lignes parser = new Fichier6lignes(file);

    while (parser.get6Lines()) {
      StateMachine state_machine = getStateMachine(parser.getGraphName());

    }
  }

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
    
    state_machine.getState(state_name);
    
    return null;
  }
}
