package graph;

import graph.conditions.aefdParser.GenerateFormulaAEFD;
import graph.verifiers.AbstractVerificationUnit;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import abstractGraph.AbstractModel;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.FormulaFactory;
import abstractGraph.conditions.Variable;
import abstractGraph.events.CommandEvent;
import abstractGraph.events.ExternalEvent;
import abstractGraph.events.SingleEvent;
import abstractGraph.events.SynchronisationEvent;
import abstractGraph.events.VariableChange;

/**
 * A set of state machines interacting with each other.
 * After after having created an object, one needs to call the #build()
 * function.
 * 
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
  private HashMap<String, ExternalEvent> external_events;
  /* All the commands that the model can generate */
  private HashMap<String, CommandEvent> commands_events;
  private HashMap<String, SynchronisationEvent> synchronisation_events;
  private HashMap<String, VariableChange> variable_modification_events;
  private HashMap<String, Variable> existingVariables;

  /** Store for every VariableChange the state machines that modifies it. */
  private HashMap<Variable, LinkedList<StateMachine>> writing_state_machines;

  /**
   * Create a new empty model named `name`.
   * 
   * @param name
   *          The name of the model.
   * 
   */
  public Model(String name) {
    super(name);
  }

  /**
   * This function is required to ensure the coherence of all the internal data
   * of the model. It has to be called after every modification of the structure
   * of the model ( {@link #addStateMachine(StateMachine)}.
   * 
   * @details Initialize all internal fields except state_machines. In
   *          particular, it does fill in writing_state_machines.
   */
  @Override
  public void build() {
    /* Internal data should all be null or initialized together */
    if (external_events == null) {
      external_events = new HashMap<String, ExternalEvent>();
      commands_events = new HashMap<String, CommandEvent>();
      synchronisation_events = new HashMap<String, SynchronisationEvent>();
      variable_modification_events = new HashMap<String, VariableChange>();
      existingVariables = new HashMap<String, Variable>();
      writing_state_machines = new HashMap<Variable, LinkedList<StateMachine>>();
    } else {
      external_events.clear();
      commands_events.clear();
      synchronisation_events.clear();
      variable_modification_events.clear();
      writing_state_machines.clear();
    }

    for (StateMachine machine : state_machines.values()) {
      Iterator<Transition> transition_iterator = machine.iteratorTransitions();
      while (transition_iterator.hasNext()) {
        Transition transition = transition_iterator.next();

        for (SingleEvent event : transition.getEvents()) {
          addEvent(event);
        }

        Formula formula = transition.getCondition();

        if (formula != null) {
          HashSet<Variable> this_formula_variables = new HashSet<Variable>();
          formula.allVariables(this_formula_variables);
          for (Variable variable : this_formula_variables) {

            existingVariables.put(variable.toString(), variable);
          }
        }

        for (SingleEvent event : transition.getActions()) {
          addEvent(event);
          if (event instanceof VariableChange) {
            Variable modified_var =
                ((VariableChange) event).getModifiedVariable();

            LinkedList<StateMachine> list =
                writing_state_machines.get(modified_var);
            if (list == null) {
              list = new LinkedList<StateMachine>();
              writing_state_machines.put(modified_var, list);
            }
            if (!list.contains(machine)) {
              list.add(machine);
            }
          }
        }
      }
    }
  }

  /**
   * Used only in build. It adds the event in the according hashmap.
   */
  private void addEvent(SingleEvent event) {
    if (event instanceof ExternalEvent) {
      external_events.put(event.getName(), (ExternalEvent) event);
    } else if (event instanceof SynchronisationEvent) {
      synchronisation_events.put(event.getName(),
          (SynchronisationEvent) event);
    } else if (event instanceof CommandEvent) {
      commands_events.put(event.getName(), (CommandEvent) event);
    } else if (event instanceof VariableChange) {
      variable_modification_events.put(event.getName(),
          (VariableChange) event);
      Variable v = ((VariableChange) event).getModifiedVariable();
      existingVariables.put(v.toString(), v);
    }
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
  public Iterator<StateMachine> iterator() {
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
    return containsVariable(variable.toString());
  }

  /**
   * {@inheritDoc #contains(Variable)}
   */
  public boolean containsVariable(String variable_name) {
    return existingVariables.containsKey(variable_name);
  }

  public boolean containsSynchronousEvents(String synchronous_event) {
    return synchronous_event.contains(synchronous_event);
  }

  /**
   * @return An iterator over the variables of the model (contained in a
   *         event field, condition field or written in a action field).
   */
  public Iterator<Variable> iteratorExistingVariables() {
    return existingVariables.values().iterator();
  }

  /**
   * @return An iterator over the commands generated by the model.
   */
  public Iterator<CommandEvent> iteratorCommands() {
    return commands_events.values().iterator();
  }

  /**
   * @return An iterator of the synchronization events of the model.
   */
  public Iterator<SynchronisationEvent> iteratorSyns() {
    return synchronisation_events.values().iterator();
  }

  /**
   * @return An iterator of the variable change in the model.
   */
  public Iterator<VariableChange> iteratorVariableChange() {
    return variable_modification_events.values().iterator();
  }

  /**
   * @return An iterator over the external events that can trigger the model.
   */
  public Iterator<ExternalEvent> iteratorExternalEvents() {
    return external_events.values().iterator();
  }

  /**
   * Search a variable by its name. It creates it if it does not exist.
   * 
   * @param variable_name
   * @return the variable associated to the variable_name.
   * @see FormulaFactory#getVariable(String)
   */
  public Variable getVariable(String variable_name) {
    return formulaFactory.getVariable(variable_name);
  }

  /**
   * @return True if the model writes on or listens to the argument event.
   */
  public boolean containsSynchronisationEvent(SynchronisationEvent event) {
    return synchronisation_events.containsKey(event.getName());
  }

  /**
   * Search all the CTL in the model (Event and Condition field) and put them in
   * a HashMap where each CTL is linked to it opposite.
   * Note that the key isn't probably the positive one.
   * 
   * @return A HashMap of the pair (positive_CTL, negative_CTL).
   */
  public HashMap<String, String> regroupCTL() {
    boolean has_error = false;
    LinkedList<String> list_of_ctl_without_opposite = new LinkedList<String>();
    HashMap<String, String> pairs_of_ctl = new HashMap<String, String>();
    HashSet<String> list_of_ctl_names = new HashSet<String>();

    searchExistingNameInHashSet(existingVariables.keySet(), "CTL_",
        list_of_ctl_names);
    searchExistingNameInHashSet(external_events.keySet(), "CTL_",
        list_of_ctl_names);

    while (!list_of_ctl_names.isEmpty()) {
      String ctl_name = list_of_ctl_names.iterator().next();
      String ctl_opposite_name = GenerateFormulaAEFD.getOppositeName(ctl_name);

      if (list_of_ctl_names.contains(ctl_opposite_name)) {
        list_of_ctl_names.remove(ctl_opposite_name);
      } else {
        has_error = true;
        list_of_ctl_without_opposite.add(ctl_name);
      }
      if (GenerateFormulaAEFD.isPositive(ctl_name)) {
        pairs_of_ctl.put(ctl_name, ctl_opposite_name);
      } else if (GenerateFormulaAEFD.isNegative(ctl_name)) {
        pairs_of_ctl.put(ctl_opposite_name, ctl_name);
      } else {
        throw new Error("The CTL " + ctl_name
            + " does not contain a correct variable suffix.");
      }
      list_of_ctl_names.remove(ctl_name);
    }
    if (has_error) {
      System.err
          .print("This list of CTL didn't have an opposite and we had to generate their opposites :  "
              + list_of_ctl_without_opposite.toString() + "\n");
    }
    return pairs_of_ctl;
  }

  /**
   * Search the Strings in `target` that begins with `to_search` and puts it in
   * `result`.
   * 
   * @param target
   *          The strings to test.
   * @param prefix
   *          The prefix to search for.
   * @param result
   */
  public void searchExistingNameInHashSet(Iterable<String> target,
      String prefix, Collection<String> result) {

    for (String variable_name : target) {
      if (variable_name.startsWith(prefix)) {
        result.add(variable_name);
      }
    }
  }

  /**
   * Add `model` to the current model. The user MUST build the resulting model
   * to have coherent internal data.
   * 
   * @param model
   * @return true if the addition of the given model has been done successfully.
   *         false if the addition failed. In that case, the result of the
   *         addition is not specified (it may have been partially modified).
   */
  public boolean add(Model model) {
    for (StateMachine machine : model) {
      if (state_machines.containsKey(machine.getName())) {
        return false;
      }
      addStateMachine(machine);
    }
    return true;
  }

}
