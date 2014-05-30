package graph;

import graph.conditions.aefdParser.AEFDFormulaFactory;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import parserAEFDFormat.Fichier6lignes;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.NotFormula;
import abstractGraph.events.Actions;
import abstractGraph.events.CommandEvent;
import abstractGraph.events.Events;
import abstractGraph.events.ExternalEvent;
import abstractGraph.events.ModelCheckerEvent;
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
  private HashMap<String, StateMachine> state_machines;
  /** List all the events present in the state machines. To prevent duplicates. */
  private HashMap<String, SingleEvent> single_events;

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
  LinkedHashMap<Transition, InitialTransition> initial_transition_order;

  /**
   * Create the factory that loads the given file. The natural function called
   * next is {@link #buildModel(String, String)}
   */
  public GraphFactoryAEFD() {
  }

  /*
   * @details The operations done to load a file are the following:
   * <ol>
   * <li>
   * Parse a first time the file to detect all the ACT_NOT_FCI (i.e.
   * the synchronization messages sent by one state machine to an
   * other). They all are in at least one action field. For every
   * ACT_NOT_FCI name, only one synchronization message is created.
   * From now, we will call these events SYNs (for synchronization).
   * 
   * </li>
   * <li>
   * Then, we do a second parsing that will load all the transitions.
   * We ensure than every external event, command, syn, and IND (for
   * indicator, that indicates the modification of the value of a
   * global variable) is unique. This is done using
   * {@link #getEvents(String)}, {@link #getCondition(String)}, and
   * {@link #getActions(String)}.
   * 
   * </li>
   * </ol>
   */
  private void parse(String file) throws IOException {

    state_machines =
        new HashMap<String, StateMachine>();

    single_events = new HashMap<String, SingleEvent>();

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

      /* Create the new transition in the associated state machine. */
      /* We deal with the case of alarms by creating 2 transitions if needed */
      Pair<Actions> actions = getActions(parser.getAction(), state_machine);

      Formula condition = getCondition(parser.getCondition(), state_machine);
      Events events = getEvents(parser.getEvent(), state_machine);
      Actions action = actions.first;
      Transition transition =
          state_machine.addTransition(from, to, events, condition, action);

      /*
       * Adding the transition in the linked list to be able to right the file
       * in the same order
       */
      initial_transition_order
          .put(transition, new InitialTransition(state_machine, transition));

      action = actions.second;
      if (action != null) {
        transition =
            state_machine.addTransition(from, from, events, new NotFormula(
                condition), action);

        /*
         * Adding the transition in the linked list to be able to right the file
         * in the same order
         */
        initial_transition_order
            .put(transition, new InitialTransition(state_machine, transition));
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

  /**
   * Produce a model.
   * 
   * @param file
   *          The file to load.
   * @param model_name
   *          The model name.
   * @return A new model representing the file given when building the
   *         {@link GraphFactoryAEFD}
   * @throws IOException
   */
  public Model buildModel(String file, String model_name) throws IOException {

    initial_transition_order =
        new LinkedHashMap<Transition, InitialTransition>();

    parse(file);

    Model result = new Model(model_name);
    Iterator<StateMachine> sm_iterator = state_machines.values().iterator();
    while (sm_iterator.hasNext()) {
      StateMachine sm = sm_iterator.next();
      result.addStateMachine(sm);
    }
    result.formulaFactory = factory;
    /* Required to generated internal data */
    result.build();
    /**
     * We check that the list of added transitions is exactly the transitions
     * added in the model
     */
    sm_iterator = result.iterator();
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
            single_events.put(new_event.getName(), new_event);
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

  private class Pair<E> {
    public E first, second;

    public Pair(E first, E second) {
      this.first = first;
      this.second = second;
    }
  }

  /**
   * Return a pair of Actions object representing the events listed in the
   * `actions`
   * String.
   * The second element of the pair is always null, except when the actions
   * contains an alarm (introduced by '/'). In that case the second element of
   * the pair is the list of actions of the alarm.
   * 
   * @param actions
   *          The input string to be parsed
   * @param m
   *          The state machine where the action field is parsed
   */
  private Pair<Actions> getActions(String actions, StateMachine m) {
    /* We check it respects the syntax */
    if (!actions.endsWith(";") && !actions.equals("")) {
      throw new UnsupportedOperationException(
          "The action list does not end with a ';' : " + actions);
    }

    /* In case of an alarm, we need to build 2 different actions list */
    Actions result = new Actions();

    if (actions.contains("/")) {
      String[] action_alarm = actions.split("/");

      if (action_alarm.length > 2) {
        throw new IllegalArgumentException(
            "There is 2 '/' character in the action field :" + actions);
      }

      /* In case of an alarm we do create 2 lists of actions. */
      addActions(result, action_alarm[0], m);

      Actions alarm = new Actions();
      addActions(alarm, action_alarm[1], m);
      return new Pair<Actions>(result, alarm);
    } else {
      addActions(result, actions, m);
      return new Pair<Actions>(result, null);
    }
  }

  /*
   * TODO: it is not the role of the Factory to build the writtingStateMachine
   * hashmap.
   */
  private void addActions(Actions actions, String string_actions, StateMachine m) {
    String[] array_of_actions = string_actions.split(";");

    for (int i = 0; i < array_of_actions.length; i++) {
      String event_string = array_of_actions[i].trim();

      if (event_string.lastIndexOf(' ') != -1) {
        throw new UnsupportedOperationException(
            "When parsing the action : " + event_string);
      }
      if (!event_string.equals("")) {
        SingleEvent new_action = actionFactory(event_string);
        actions.add(new_action);
      }

    }
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
    SingleEvent result = single_events.get(event_name);

    if (result != null) {
      if (!(result instanceof ExternalEvent) &&
          !(result instanceof SynchronisationEvent) &&
          !(result instanceof VariableChange)) {
        throw new Error("Impossible scenario");
      }
      return result;
    }

    SingleEvent new_event;
    String prefix;
    try {
      prefix = event_name.substring(0, event_name.indexOf('_'));
    } catch (IndexOutOfBoundsException e) {
      throw new InvalidParameterException(
          "The following string has been found in an event field: "
              + event_name + ". It is invalid because no '_' appears in it.");
    }
    switch (prefix) {
    case "MSG":
    case "CTL":
    case "FTP":
      new_event = new ExternalEvent(event_name);
      break;
    case "IND":
      new_event = new VariableChange(factory.getLiteral(event_name));
      break;
    case "ACT":
      /*
       * It is necessarily an external event since it has not been identified as
       * an internal event during the first parsing
       */
      new_event = new ExternalEvent(event_name);
      break;
    case "SYN":
      new_event = new SynchronisationEvent(event_name);
      break;
    default:
      System.out.println(toString());
      throw new UnsupportedOperationException(
          "When parsing the events field : " + event_name);
    }
    single_events.put(new_event.getName(), new_event);

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
    SingleEvent result = single_events.get(event_name);

    if (result != null) {
      if (!(result instanceof CommandEvent) &&
          !(result instanceof SynchronisationEvent) &&
          !(result instanceof VariableChange) &&
          !(result instanceof ModelCheckerEvent)) {
        throw new Error("Impossible scenario. The event " + result.getName()
            + " is a " + result.getClass());
      }

      return result;
    }

    SingleEvent new_event;
    String prefix;
    try {
      prefix = event_name.substring(0, event_name.indexOf('_'));
    } catch (IndexOutOfBoundsException e) {
      throw new InvalidParameterException(
          "The following string has been found in an action field: "
              + event_name + ". It is invalid because no '_' appears in it.");
    }
    switch (prefix) {
    case "DTP":
    case "ATP":
    case "CMD":
    case "FCI":
      new_event = new CommandEvent(event_name);
      break;
    case "P":
      new_event = new ModelCheckerEvent(event_name);
      break;
    case "IND":
      new_event = new VariableChange(factory.getLiteral(event_name));
      break;
    case "ACT":
      /*
       * It is necessarily an external event since it has not been identified as
       * an internal event during the first parsing
       */
      new_event = new ExternalEvent(event_name);
      break;
    case "SYN":
      new_event = new SynchronisationEvent(event_name);
      break;
    default:
      throw new UnsupportedOperationException(
          "When parsing the events field : " + event_name);
    }
    single_events.put(new_event.getName(), new_event);
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
      String temp = concatenateEventsWithOU(init_transition.t.getEvents());
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
