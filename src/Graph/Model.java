package Graph;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.HashMap;

import abstractGraph.AbstractModel;
import abstractGraph.Conditions.AbstractVariable;

public class Model extends AbstractModel<StateMachine, State, Transition> {
  private LinkedHashMap<String, StateMachine> state_machines;
  private LinkedHashSet<AbstractVariable> variables;
  /* Every variable should be written by only state machine. This keeps the record */
  private HashMap<AbstractVariable, StateMachine> writting_rights;

  public Model(String name) {
    super(name);
    state_machines = new LinkedHashMap<String, StateMachine>(100);
    variables = new LinkedHashSet<AbstractVariable>();
  }

  @Override
  public void addStateMachine(StateMachine state_machine) {
    state_machines.put(state_machine.getName(), (StateMachine) state_machine);
  }

  public StateMachine getStateMachine(String name) {
    return state_machines.get(name);
  }

}
