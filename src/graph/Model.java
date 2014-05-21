package graph;

import graph.verifiers.AbstractVerificationUnit;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import abstractGraph.AbstractModel;
import abstractGraph.conditions.FormulaFactory;
import abstractGraph.conditions.Variable;
import abstractGraph.events.CommandEvent;
import abstractGraph.events.ExternalEvent;
import abstractGraph.events.SynchronisationEvent;

/**
 * A set of state machines interacting with each other.
 * There is a set of external events to which the model can react
 * (external_events), a set of commands that the model can give to the external
 * environment and internal messages (both synchronization messages and global
 * variables).
 */
public class Model extends AbstractModel<StateMachine, State, Transition> {
  /* The order internal state machines */
  private LinkedHashMap<String, StateMachine> state_machines =
      new LinkedHashMap<String, StateMachine>(100);
  /**
   * The FormulaFactory used to generate new variables.
   * All the variables that are used within the state machines are stored in
   * this FormulaFactory. There is pointer uniqueness of variables (i.e. two
   * variables are equals if and only if they are the same object).
   */
  protected FormulaFactory formulaFactory;

  /* All the external events that can trigger the model */
  protected HashMap<String, ExternalEvent> external_events;
  /* All the commands that the model can generate */
  protected HashMap<String, CommandEvent> commands_events;
  protected HashMap<String, SynchronisationEvent> synchronisation_events;

  /** Store for every VariableChange the state machines that modifies it. */
  protected HashMap<Variable, LinkedList<StateMachine>> writing_state_machines;

  /** Store all the variables that are found ONLY in the conditions field. */
  protected HashSet<Variable> condition_variable = new HashSet<Variable>();

  /**
   * Create a new empty model named `name`.
   * 
   * @param name
   *          The name of the model.
   */
  public Model(String name) {
    super(name);
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
  public Iterator<StateMachine> iteratorStatesMachines() {
    return state_machines.values().iterator();
  }

  /**
   * @return An iterator over the couple (VariableChange => List of state
   *         machines writing on it)
   */
  public Iterator<Entry<Variable, LinkedList<StateMachine>>> writingRightsIterator() {
    return writing_state_machines.entrySet().iterator();
  }

  /**
   * This function is reserved to specific uses that require to access internal
   * data of the model. In particular, it can be useful to write new
   * {@link AbstractVerificationUnit}.
   * 
   * @return The HashMap linking for every VariableChange, the list of the state
   *         machines that are modifying its value.
   */
  public HashMap<Variable, LinkedList<StateMachine>> getWritingStateMachines() {
    return writing_state_machines;
  }

  /**
   * Allow to check the existence of a variable in a Condition field.
   * 
   * @param variable
   *          The variable to look for.
   * @return true if the variable exists in a Condition field.
   */
  public boolean containsVariable(Variable variable) {
    return formulaFactory.contains(variable);
  }

  /**
   * {@inheritDoc #contains(Variable)}
   */
  public boolean containsVariable(String variable_name) {
    return formulaFactory.contains(variable_name);
  }

  /**
   * Iterate the variables that are contained in the conditions.
   * 
   * @return
   */
  public Iterator<Variable> iteratorExistingVariables() {
    return formulaFactory.iteratorExistingVariables();
  }

  /**
   * 
   * @return A set of the variables found ONLY in the condition fields.
   */
  public HashSet<Variable> getConditionVariable() {
    return condition_variable;
  }

}
