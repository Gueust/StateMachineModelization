package Graph;

import java.util.LinkedHashMap;

import abstractGraph.AbstractModel;
import abstractGraph.AbstractStateMachine;

public class Model extends AbstractModel {
  private LinkedHashMap<String, StateMachine> state_machines;

  public Model(String name) {
    super(name);
    state_machines = new LinkedHashMap<String, StateMachine>(100);
  }

  @Override
  public void addStateMachine(AbstractStateMachine state_machine) {
    state_machines.put(state_machine.getName(), (StateMachine) state_machine);
  }

  public StateMachine getStateMachine(String name) {
    return state_machines.get(name);
  }

}
