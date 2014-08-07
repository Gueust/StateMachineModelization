package graph;

import graph.conditions.aefdParser.GenerateFormulaAEFD;
import graph.templates.FonctionCommandeInformatique;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import utils.Pair;
import abstractGraph.AbstractModel;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.FormulaFactory;
import abstractGraph.events.CommandEvent;
import abstractGraph.events.ComputerCommandFunction;
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
  public HashMap<String, ExternalEvent> external_events;
  /* All the commands that the model can generate */
  private HashMap<String, CommandEvent> commands_events;

  private HashMap<String, SynchronisationEvent> synchronisation_events;
  private HashMap<String, VariableChange> variable_modification_events;
  private HashMap<String, EnumeratedVariable> existingVariables;

  /** Store for every VariableChange the state machines that modifies it. */
  private HashMap<EnumeratedVariable, Collection<StateMachine>> writing_state_machines;

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
    super.build();
    /* Internal data should all be null or initialized together */
    if (external_events == null) {
      external_events = new HashMap<>();
      commands_events = new HashMap<>();
      synchronisation_events = new HashMap<>();
      variable_modification_events = new HashMap<>();
      existingVariables = new HashMap<>();
      writing_state_machines = new HashMap<>();
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
          HashSet<EnumeratedVariable> this_formula_variables = new HashSet<>();
          formula.allVariables(this_formula_variables);
          for (EnumeratedVariable variable : this_formula_variables) {
            existingVariables.put(variable.toString(),
                (BooleanVariable) variable);
          }
        }

        for (SingleEvent event : transition.getActions()) {
          addEvent(event);
          if (event instanceof VariableChange) {
            BooleanVariable modified_var =
                ((VariableChange) event).getModifiedVariable();

            LinkedList<StateMachine> list =
                (LinkedList<StateMachine>) writing_state_machines
                    .get(modified_var);
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

  private void setNotBuild() {
    external_events = null;
    commands_events = null;
    synchronisation_events = null;
    variable_modification_events = null;
    existingVariables = null;
    writing_state_machines = null;

  }

  /**
   * @return
   */
  private boolean isBuild() {
    return external_events != null;
  }

  private void buildIfNecessary() {
    if (!isBuild()) {
      build();
    }
  }

  public LinkedList<ExternalEvent> loadScenario(String file_name)
      throws IOException {
    buildIfNecessary();

    BufferedReader buff = new BufferedReader(new FileReader(file_name));

    LinkedList<ExternalEvent> result = new LinkedList<>();

    String line;
    while ((line = buff.readLine()) != null) {

      ExternalEvent event = external_events.get(line.trim());
      if (event == null) {
        buff.close();
        throw new Error("The event " + line + " does not exist in the model");
      }
      result.add(event);
    }

    buff.close();

    return result;
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
      BooleanVariable v = ((VariableChange) event).getModifiedVariable();
      existingVariables.put(v.toString(), v);
    }
  }

  @Override
  public void addStateMachine(StateMachine state_machine) {
    setNotBuild();
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
   * Require to have previously called {@link #build()}.
   * 
   * @return An iterator over the couple (VariableChange => List of state
   *         machines writing on it)
   */
  public Iterator<Entry<EnumeratedVariable, Collection<StateMachine>>> writingRightsIterator() {
    return writing_state_machines.entrySet().iterator();
  }

  @Override
  public HashMap<EnumeratedVariable, Collection<StateMachine>> getWritingStateMachines() {
    return writing_state_machines;
  }

  @Override
  public Collection<EnumeratedVariable> getExistingVariables() {
    return existingVariables.values();
  }

  public HashMap<String, CommandEvent> getCommands_events() {
    return commands_events;
  }

  public HashMap<String, SynchronisationEvent> getSynchronisation_events() {
    return synchronisation_events;
  }

  /**
   * Allow to check the existence of a variable in a Condition field.
   * 
   * Require to have previously called {@link #build()}.
   * 
   * @param variable
   *          The variable to look for.
   * @return true if the variable exists in a Condition field.
   */
  @Override
  public boolean containsVariable(EnumeratedVariable variable) {
    return containsVariable(variable.toString());
  }

  /**
   * Require to have previously called {@link #build()}. {@inheritDoc
   * #contains(Variable)}
   */
  public boolean containsVariable(String variable_name) {
    return existingVariables.containsKey(variable_name);
  }

  @Override
  public boolean containsSynchronousEvent(String synchronous_event) {
    return synchronisation_events.containsKey(synchronous_event);
  }

  @Override
  public boolean containsExternalEvent(ExternalEvent external_event) {
    return external_events.containsKey(external_event.getName());
  }

  /**
   * Require to have previously called {@link #build()}.
   * 
   * @return An iterator over the variables of the model (contained in a
   *         event field, condition field or written in a action field).
   */
  public Iterator<EnumeratedVariable> iteratorExistingVariables() {
    return existingVariables.values().iterator();
  }

  /**
   * Require to have previously called {@link #build()}.
   * 
   * @return An iterator over the commands generated by the model.
   */
  public Iterator<CommandEvent> iteratorCommands() {
    return commands_events.values().iterator();
  }

  /**
   * Require to have previously called {@link #build()}.
   * 
   * @return An iterator of the synchronization events of the model.
   */
  public Iterator<SynchronisationEvent> iteratorSyns() {
    return synchronisation_events.values().iterator();
  }

  /**
   * Require to have previously called {@link #build()}.
   * 
   * @return An iterator of the variable change in the model.
   */
  public Iterator<VariableChange> iteratorVariableChange() {
    return variable_modification_events.values().iterator();
  }

  /**
   * Require to have previously called {@link #build()}.
   * 
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
  @Override
  public BooleanVariable getVariable(String variable_name) {
    return formulaFactory.getVariable(variable_name);
  }

  /**
   * Require to have previously called {@link #build()}.
   * 
   * @return True if the model writes on or listens to the argument event.
   */
  @Override
  public boolean containsSynchronisationEvent(SynchronisationEvent event) {
    return synchronisation_events.containsKey(event.getName());
  }

  /**
   * Search all the CTL in the model (Event and Condition field) and put them in
   * a HashMap where each CTL is linked to it opposite.
   * Note that the key isn't probably the positive one.
   * 
   * Do not required to have previously called {@link #build()}.
   * 
   * @return A HashMap of the pair (positive_CTL, negative_CTL).
   */
  public HashMap<String, String> regroupCTL() {
    buildIfNecessary();

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
  private void searchExistingNameInHashSet(Iterable<String> target,
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

  /**
   * Some commands (called FCI) generate external event on execution.
   * The list of the generated external events produced by every FCI command can
   * be loaded from a file.
   * 
   * @param file_name
   *          The YAML file from which to load the data.
   * @throws IOException
   */
  public void loadFCI(String file_name) throws IOException {
    buildIfNecessary();

    FonctionCommandeInformatique loaded_list =
        FonctionCommandeInformatique.load(file_name);
    // CommandEvent, LinkedList<Pair<Formula, LinkedList<ExternalEvent>>>
    for (Entry<String, List<HashMap<String, List<String>>>> entry : loaded_list
        .getFCI_list()
        .entrySet()) {
      String command_name = entry.getKey();
      ComputerCommandFunction command = new ComputerCommandFunction(
          command_name);
      LinkedList<Pair<Formula, LinkedList<ExternalEvent>>> associated_events = new LinkedList<Pair<Formula, LinkedList<ExternalEvent>>>();
      for (HashMap<String, List<String>> condition_and_external_event : entry
          .getValue()) {
        for (Entry<String, List<String>> pair_condition_external_event : condition_and_external_event
            .entrySet()) {

          Formula condition = formulaFactory
              .parse(pair_condition_external_event.getKey());
          LinkedList<ExternalEvent> ACT_list = new LinkedList<ExternalEvent>();
          for (String act_event : pair_condition_external_event.getValue()) {
            ExternalEvent event = external_events.get(act_event);
            if (event == null) {
              /*
               * throw new Error("The event " + act_event
               * + " loaded from the file "
               * + file_name + " does not exist in the model");
               * TODO put back this exception. It was removed for a test
               */
              event = new ExternalEvent(act_event);
            }
            ACT_list.add(event);
          }
          associated_events.add(new Pair<Formula, LinkedList<ExternalEvent>>(
              condition, ACT_list));
        }

      }
      FCI_generate_ACT.put(command, associated_events);
    }
  }

  @Override
  public boolean containsStateMachine(
      StateMachine state_machine) {
    return state_machines.containsKey(state_machine);
  }

  @Override
  public Model newInstance() {
    return new Model("default");
  }

}
