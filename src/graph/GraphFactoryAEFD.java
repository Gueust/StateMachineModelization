package graph;

import graph.conditions.aefdParser.AEFDFormulaFactory;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import parserAEFDFormat.Fichier6lignes;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.Variable;
import abstractGraph.events.Actions;
import abstractGraph.events.CommandEvent;
import abstractGraph.events.Events;
import abstractGraph.events.ExternalEvent;
import abstractGraph.events.SingleEvent;
import abstractGraph.events.SynchronisationEvent;
import abstractGraph.events.VariableChange;

/**
 * Build models from an AEFD formatted file.
 * 
 * To use this class :
 * 
 * <pre>
 * {
 * GraphFactoryAEFD factory = new GraphFactory(&quot;file_name.txt&quot;);
 * Model m = factory.buildModel();
 * }
 * </pre>
 */
public class GraphFactoryAEFD {

  /** The formula factory to parse the condition formulas */
  private AEFDFormulaFactory factory = new AEFDFormulaFactory(true);

  /** List all the state machines of the model */
  private HashMap<String, StateMachine> state_machines =
      new HashMap<String, StateMachine>();
  /** List all the external events present in the state machines */
  private HashMap<String, ExternalEvent> external_events =
      new HashMap<String, ExternalEvent>();
  /** List all the commands present in the state machines */
  private HashMap<String, CommandEvent> commands_events =
      new HashMap<String, CommandEvent>();
  /** List all the synchronization messages */
  private HashMap<String, SynchronisationEvent> synchronisation_events =
      new HashMap<String, SynchronisationEvent>();
  /** List all the global variable modification messages */
  private HashMap<String, VariableChange> variable_modification_events =
      new HashMap<String, VariableChange>();

  /** Store for every VariableChange the state machines that modifies it. */
  private HashMap<Variable, LinkedList<StateMachine>> writting_state_machines =
      new HashMap<Variable, LinkedList<StateMachine>>();

  /** Store all the variables that are found ONLY in the conditions field. */
  private HashSet<Variable> condition_variable = new HashSet<Variable>();

  /**
   * Used to remember the order of the transition within the parsed file in
   * order to be able to write the file back and compare with the initial file
   */
  class InitialTransition {
    public StateMachine state_machine;
    public Transition t;

    public InitialTransition(StateMachine sm, Transition t) {
      this.state_machine = sm;
      this.t = t;
    }
  }

  /** Keep the order of the transition within the parsed file */
  LinkedHashMap<Transition, InitialTransition> initial_transition_order =
      new LinkedHashMap<Transition, InitialTransition>();

  /**
   * Create the factory that loads the given file. The natural function called
   * next is {@link #buildModel(String)}
   * 
   * @details The operations done to load a file are the following:
   *          <ol>
   *          <li>
   *          Parse a first time the file to detect all the ACT_NOT_FCI (i.e.
   *          the synchronization messages sent by one state machine to an
   *          other). They all are in at least one action field. For every
   *          ACT_NOT_FCI name, only one synchronization message is created.
   *          From now, we will call these events SYNs (for synchronization).
   * 
   *          </li>
   *          <li>
   *          Then, we do a second parsing that will load all the transitions.
   *          We ensure than every external event, command, syn, and IND (for
   *          indicator, that indicates the modification of the value of a
   *          global variable) is unique. This is done using
   *          {@link #getEvents(String)}, {@link #getCondition(String)}, and
   *          {@link #getActions(String)}.
   * 
   *          </li>
   *          </ol>
   * 
   * @param file
   *          The file containing the states machines of the model. From now, we
   *          can only load one file.
   * @throws IOException
   */
  public GraphFactoryAEFD(String file) throws IOException {
    /*
     * We do two parsings:
     * - the first one to identify the ACT not FCI in the action fields
     * - the second one to load the file
     */
    parsingInternalEvents(file);

    Fichier6lignes parser = new Fichier6lignes(file);

    while (parser.get6Lines()) {
      String sm_name = parser.getGraphName();
      String from_name = parser.getSourceState();
      String to_name = parser.getDestinationState();

      StateMachine state_machine = retrieveStateMachine(sm_name);
      State from = retrieveState(state_machine.getName(), from_name);
      State to = retrieveState(state_machine.getName(), to_name);

      /* Create the new transition in the associated state machine */
      Transition transition =
          state_machine.addTransition(from, to,
              getEvents(parser.getEvent(), state_machine),
              getCondition(parser.getCondition(), state_machine),
              getActions(parser.getAction(), state_machine));

      /*
       * Adding the transition in the linked list to be able to right the file
       * in the same order
       */
      initial_transition_order
          .put(transition, new InitialTransition(state_machine, transition));

      /*
       * Store all the variables found in the field condition and put it in the
       * HashSet condition_variables.
       */

      Formula condition = getCondition(parser.getCondition(), state_machine);
      if (condition != null) {
        condition.allVariables(condition_variable);
      }
    }

    /**
     * We check that we can generate the same file as the input file.
     * TODO: the loaded format is too complex now to be able to compare simply.
     * We have postponed (give up ?) this checking.
     */
    // saveInFile("temporary_file");
    // boolean is_build_coherent = compareFiles("temporary_file", file);
    // if (is_build_coherent) {
    // System.out
    // .println("Comparing the initial model with the built model... OK");
    // } else {
    // System.out.println("File generated from the loaded model is different" +
    // " from the initial model. Aborting");
    // System.exit(-1);
    // }

  }

  private void addWrittingStateMachine(VariableChange event, StateMachine m) {
    Variable modified_var = event
        .getModifiedVariable();
    LinkedList<StateMachine> list = writting_state_machines.get(modified_var);
    if (list == null) {
      list = new LinkedList<StateMachine>();
      writting_state_machines.put(modified_var, list);
    }
    if (!list.contains(m)) {
      list.add(m);
    }
  }

  /**
   * Produce a model.
   * 
   * @param model_name
   *          The model name.
   * @return A new model representing the file given when building the
   *         {@link GraphFactoryAEFD}
   */
  public Model buildModel(String model_name) {
    Model result = new Model(model_name);
    Iterator<StateMachine> sm_iterator = state_machines.values().iterator();
    while (sm_iterator.hasNext()) {
      StateMachine sm = sm_iterator.next();
      result.addStateMachine(sm);
    }
    result.external_events = external_events;
    result.commands_events = commands_events;
    result.synchronisation_events = synchronisation_events;
    result.writing_state_machines = writting_state_machines;
    result.formulaFactory = factory;
    result.condition_variable = condition_variable;

    /**
     * We check that the list of added transitions is exactly the transitions
     * added in the model
     */
    sm_iterator = result.iteratorStatesMachines();
    int number_of_transitions = 0;
    while (sm_iterator.hasNext()) {
      StateMachine sm = sm_iterator.next();
      Iterator<Transition> transitions_iterator = sm.iteratorTransitions();
      while (transitions_iterator.hasNext()) {
        Transition t = transitions_iterator.next();
        number_of_transitions++;
        /* We check that the transition is in initial_transition_order */
        if (initial_transition_order.get(t) == null) {
          System.out.println("Error 1 in the building of the model");
          System.exit(-1);
        }
      }
    }

    /*
     * We check that the number of transition added is equal to the number of
     * transitions in initial_transition_order
     */
    if (initial_transition_order.size() != number_of_transitions) {
      System.out.println("Error 2 in the building of the model");
      System.exit(-1);
    }

    return result;
  }

  /**
   * This functions retrieves all the internal ACT events
   * 
   * @throws IOException
   */
  private void parsingInternalEvents(String file) throws IOException {
    Fichier6lignes parser = new Fichier6lignes(file);

    /* We parse all the actions of all the transitions */
    while (parser.get6Lines()) {
      String actions = parser.getAction();
      String[] array_of_actions = actions.split(";");

      for (int i = 0; i < array_of_actions.length; i++) {
        String event_string = array_of_actions[i].trim();

        if (!event_string.equals("")) {
          if (event_string
              .substring(0, event_string.indexOf('_'))
              .equals("ACT") ||
              event_string
                  .substring(0, event_string.indexOf('_'))
                  .equals("SYN")) {
            SynchronisationEvent new_event = new SynchronisationEvent(
                event_string);
            synchronisation_events.put(new_event.getName(), new_event);
          }
        }
      }
    }
  }

  /**
   * Retrieve a state machine from its name. it is created if it does not
   * already exist.
   * 
   * @param machine_name
   *          The name of the machine.
   * @return The associated state machine.
   */
  private StateMachine retrieveStateMachine(String name) {
    StateMachine result = state_machines.get(name);
    if (result != null) {
      return result;
    } else {
      result = new StateMachine(name);
      state_machines.put(name, result);
      return result;
    }
  }

  /**
   * Retrieve a state from a machine name and the state name. It is created if
   * it does not already exist.
   * 
   * @param machine_name
   *          The name of the machine containing the state.
   * @param state_name
   *          The name of the state.
   * @return The associated state.
   */
  private State retrieveState(String machine_name, String state_name) {
    StateMachine state_machine = retrieveStateMachine(machine_name);

    State s = state_machine.getState(state_name);
    if (s == null) {
      return state_machine.addState(state_name);
    } else {
      return s;
    }
  }

  /**
   * Return an Events object representing the events listed in the `events`
   * String
   * 
   * @param events
   *          The input string to be parsed
   * @param m
   *          The state machine where the action field is parsed.
   * @return The equivalent Events object
   */
  private Events getEvents(String events, StateMachine m) {
    String[] array_of_events = events.split("OU");
    Events result = new Events();

    for (int i = 0; i < array_of_events.length; i++) {
      String event_string = array_of_events[i].trim();
      if (event_string.lastIndexOf(' ') != -1) {
        throw new UnsupportedOperationException(
            "When parsing the single event : " + event_string);
      }
      if (!event_string.equals("")) {
        SingleEvent new_event = eventFactory(event_string);
        result.addEvent(new_event);
      }
    }
    return result;
  }

  /**
   * Return an Actions object representing the events listed in the `actions`
   * String
   * 
   * @param actions
   *          The input string to be parsed
   * @param m
   *          The state machine where the action field is parsed.
   * @return The equivalent Actions object
   * @throws Exception
   */
  private Actions getActions(String actions, StateMachine m) {
    /* TODO : split around "/" and take the left part */
    if (!actions.endsWith(";") && !actions.equals("")) {
      throw new UnsupportedOperationException(
          "The action list does not end with a ';' : " + actions);
    }
    String[] array_of_actions = actions.split(";");
    Actions result = new Actions();

    for (int i = 0; i < array_of_actions.length; i++) {
      String event_string = array_of_actions[i].trim();

      if (event_string.lastIndexOf(' ') != -1) {
        throw new UnsupportedOperationException(
            "When parsing the action : " + event_string);
      }
      if (!event_string.equals("")) {
        SingleEvent new_action = actionFactory(event_string);
        if (new_action instanceof VariableChange) {
          addWrittingStateMachine((VariableChange) new_action, m);
        }
        result.add(new_action);
      }

    }
    return result;
  }

  /**
   * 
   * @param condition
   * @param m
   *          The state machine where the action field is parsed.
   * @return
   */
  private Formula getCondition(String condition, StateMachine m) {
    return factory.parse(condition);
  }

  /**
   * Return the SingleEvent object associated to `event_name`.
   * 
   * @param event_name
   *          The name of the event (including the prefix).
   * @return The SingleEvent associated with it.
   */
  private SingleEvent eventFactory(String event_name) {
    SingleEvent result;

    result = external_events.get(event_name);
    if (result != null) {
      return result;
    }
    result = synchronisation_events.get(event_name);
    if (result != null) {
      return result;
    }

    SingleEvent new_event;
    switch (event_name.substring(0, event_name.indexOf('_'))) {
    case "MSG":
    case "CTL":
    case "FTP":
      new_event = new ExternalEvent(event_name);
      external_events.put(new_event.getName(), (ExternalEvent) new_event);
      break;
    case "IND":
      new_event = new VariableChange(factory.getLiteral(event_name));

      variable_modification_events
          .put(new_event.getName(), (VariableChange) new_event);
      break;
    case "ACT":
      /*
       * It is necessarily an external event since it has not been identified as
       * an internal event during the first parsing
       */
      new_event = new ExternalEvent(event_name);
      external_events.put(new_event.getName(), (ExternalEvent) new_event);
      break;
    case "SYN":
      new_event = new SynchronisationEvent(event_name);
      synchronisation_events.put(new_event.getName(),
          (SynchronisationEvent) new_event);
      break;
    default:
      System.out.println(toString());
      throw new UnsupportedOperationException(
          "When parsing the events field : " + event_name);
    }
    return new_event;

  }

  /**
   * Return the action associated to the event. It is to be called for every
   * action in the Action field.
   * 
   * @param event_name
   * @return
   */
  private SingleEvent actionFactory(String event_name) {
    SingleEvent result;

    result = external_events.get(event_name);
    if (result != null) {
      return result;
    }
    result = commands_events.get(event_name);
    if (result != null) {
      return result;
    }
    result = synchronisation_events.get(event_name);
    if (result != null) {
      return result;
    }

    SingleEvent new_event;
    switch (event_name.substring(0, event_name.indexOf('_'))) {
    case "DTP":
    case "ATP":
    case "CMD":
    case "MSG":
    case "FCI":
    case "P":
      new_event = new CommandEvent(event_name);
      commands_events.put(new_event.getName(), (CommandEvent) new_event);
      break;
    case "IND":
      new_event = new VariableChange(factory.getLiteral(event_name));
      variable_modification_events
          .put(new_event.getName(), (VariableChange) new_event);
      break;
    case "ACT":
      /*
       * It is necessarily an external event since it has not been identified as
       * an internal event during the first parsing
       */
      new_event = new ExternalEvent(event_name);
      external_events.put(new_event.getName(), (ExternalEvent) new_event);
      break;
    case "SYN":
      new_event = new SynchronisationEvent(event_name);
      synchronisation_events.put(new_event.getName(),
          (SynchronisationEvent) new_event);
      break;
    default:
      throw new UnsupportedOperationException(
          "When parsing the events field : " + event_name);
    }
    return new_event;
  }

  /**
   * Save the loaded data into a file using the AEDF format.
   * 
   * @param file
   * @throws IOException
   */
  private void saveInFile(String file) throws IOException {
    File selected_file = new File(file);
    BufferedWriter writer = new BufferedWriter(new FileWriter(selected_file));
    Iterator<InitialTransition> iterator =
        initial_transition_order.values().iterator();
    while (iterator.hasNext()) {
      InitialTransition init_transition = iterator.next();

      writer.write(init_transition.state_machine.getName());
      writer.write("\r\n");
      writer.write(init_transition.t.getSource().getId());
      writer.write("\r\n");
      writer.write(init_transition.t.getDestination().getId());
      writer.write("\r\n");
      String temp = concatenateEventsWithOU(init_transition.t.getEvent());
      if (temp != null) {
        writer.write(temp + " Evenement");
      } else {
        writer.write("Evenement");
      }
      writer.write("\r\n");
      temp = init_transition.t.getCondition().toString();
      if (!temp.equals("")) {
        writer.write(temp + " Condition");
      } else {
        writer.write("Condition");
      }
      writer.write("\r\n");
      temp = init_transition.t.getActions().toString();
      if (temp != null) {
        writer.write(temp + " Action");
      } else {
        writer.write(" Action");
      }
      if (iterator.hasNext())
        writer.write("\r\n");
    }

    writer.close();
  }

  private String concatenateEventsWithOU(Events events) {
    StringBuilder sb = new StringBuilder();
    Iterator<SingleEvent> single_event_iterator = events.singleEvent();
    boolean first = true;
    while (single_event_iterator.hasNext()) {

      SingleEvent single_event = single_event_iterator.next();
      if (first) {
        sb.append(single_event.toString());
      } else {
        sb.append(" OU " + single_event.toString());
      }

      first = false;
    }
    return sb.toString();
  }

  /**
   * Compares 2 files byte by byte
   * 
   * @param file1
   *          The name of the first file
   * @param file2
   *          The name of the second file
   * @return true if their are identical, false otherwise
   * @throws IOException
   */
  private boolean compareFiles(String file1, String file2) throws IOException {
    InputStream in1 = null, in2 = null;
    try {
      in1 = new BufferedInputStream(new FileInputStream(file1));
      in2 = new BufferedInputStream(new FileInputStream(file2));

      int Byte = in1.read();
      /* While we do not reach the end of the first file */
      while (Byte != -1) {
        if (Byte != in2.read()) {
          return false;
        }
        Byte = in1.read();
      }
      /* We check that the other file is also at the end */
      if (in2.read() != -1) {
        return false;
      }
      return true;
    } finally {
      in1.close();
      in2.close();
    }
  }

  @Override
  public String toString() {
    return state_machines.toString();
  }
}
