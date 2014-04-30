package Graph;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.HashMap;

import Graph.Events.UnknownEvent;
import abstractGraph.AbstractModel;
import abstractGraph.Conditions.AbstractVariable;
import abstractGraph.Events.CommandEvent;
import abstractGraph.Events.ExternalEvent;
import abstractGraph.Events.InternalEvent;

public class Model extends AbstractModel<StateMachine, State, Transition> {
  private LinkedHashMap<String, StateMachine> state_machines;
  private LinkedHashSet<AbstractVariable> variables;

  protected HashMap<String, ExternalEvent> external_events;
  protected HashMap<String, CommandEvent> commands_events;
  protected HashMap<String, InternalEvent> internal_events;
  protected HashMap<String, UnknownEvent> unknown_events;

  /*
   * Every variable should be written by only state machine. This keeps the
   * record
   */
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

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("MODEL: " + getModelName() + "\n");
    Iterator<StateMachine> iterator = state_machines.values().iterator();
    while (iterator.hasNext()) {
      StateMachine sm = iterator.next();
      sb.append(sm.toString() + "\n");
    }
    sb.append("\n");
    return sb.toString();
  }

  @Override
  public Iterator<StateMachine> statesMachines() {
    return state_machines.values().iterator();
  }
}
