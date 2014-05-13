package graph;

import graph.events.SynchronisationEvent;
import graph.events.VariableChange;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import abstractGraph.AbstractModel;
import abstractGraph.conditions.Variable;
import abstractGraph.events.CommandEvent;
import abstractGraph.events.ExternalEvent;

/**
 * A set of state machines interacting with each other.
 * There is a set of external events to which the model can react
 * (external_events), a set of commands that the model can give to the external
 * environment and internal messages (both synchronization messages and global
 * variables).
 */
public class Model extends AbstractModel<StateMachine, State, Transition> {
  /* The order internal state machines */
  private LinkedHashMap<String, StateMachine> state_machines;
  /*
   * All the variables that are used within the state machines. There is pointer
   * uniqueness of variables (i.e. two variables are equals if and only if they
   * are the same object).
   */
  private LinkedHashSet<Variable> variables;

  /* All the external events that can trigger the model */
  protected HashMap<String, ExternalEvent> external_events;
  /* All the commands that the model can generate */
  protected HashMap<String, CommandEvent> commands_events;
  protected HashMap<String, SynchronisationEvent> synchronisation_events;

  /** Store for every VariableChange the state machines that modifies it. */
  protected HashMap<Variable, LinkedList<StateMachine>> writting_state_machines;

  /**
   * Create a new empty model named `name`.
   * 
   * @param name
   *          The name of the model.
   */
  public Model(String name) {
    super(name);
    state_machines = new LinkedHashMap<String, StateMachine>(100);
    variables = new LinkedHashSet<Variable>();
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

  /**
   * @return An iterator over the couple (VariableChange => List of state
   *         machines writing on it)
   */
  public Iterator<Entry<Variable, LinkedList<StateMachine>>> writingRightsIterator() {
    System.out.println(writting_state_machines);
    return writting_state_machines.entrySet().iterator();
  }
}
