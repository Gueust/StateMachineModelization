package Graph;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import abstractGraph.Conditions.AbstractCondition;
import abstractGraph.Events.CommandEvent;
import abstractGraph.Events.Events;
import abstractGraph.Events.ExternalEvent;
import abstractGraph.Events.InternalEvent;
import abstractGraph.Events.SingleEvent;
import Graph.Conditions.Condition;
import Graph.Events.Actions;
import Graph.Events.UnknownEvent;
import Parser_Fichier_6lignes.Fichier6lignes;

public class GraphFactoryAEFD {

  private HashMap<String, StateMachine> state_machines;
  private HashMap<String, ExternalEvent> external_events;
  private HashMap<String, CommandEvent> commands_events;
  private HashMap<String, InternalEvent> internal_events;

  public GraphFactoryAEFD(String file) throws Exception {

    state_machines = new HashMap<String, StateMachine>();
    external_events = new HashMap<String, ExternalEvent>();
    commands_events = new HashMap<String, CommandEvent>();
    internal_events = new HashMap<String, InternalEvent>();

    /*
     * We do two parsings
     * - the first one to identify the ACT not FCI in the action fields
     * - the second one to load the file
     */
    parsingInternalEvents(file);

    Fichier6lignes parser = new Fichier6lignes(file);

    while (parser.get6Lines()) {
      StateMachine state_machine = retrieveStateMachine(parser.getGraphName());
      // Get the source state
      State from = retrieveState(state_machine.getName(), parser
          .getSourceState());
      // Get the destination state
      State to = retrieveState(state_machine.getName(), parser
          .getDestinationState());

      state_machine.addTransition(from, to, getEvents(parser.getEvent()),
          getCondition(parser.getCondition()), getActions(parser.getAction()));
    }

    saveInFile("temporary_file");
    boolean is_build_coherent = compareFiles("temporary_file", file);
    if (is_build_coherent) {
      System.out
          .println("Comparing the initial model with the built model... OK");
    } else {
      System.out
          .println("File generated from the loaded model is different from the"
              + "initial model. Aborting");
      System.exit(-1);
    }
  }

  public Model buildModel(String model_name) {
    Model result = new Model(model_name);
    Iterator<StateMachine> sm_iterator = state_machines.values().iterator();
    while (sm_iterator.hasNext()) {
      StateMachine sm = sm_iterator.next();
      result.addStateMachine(sm);
    }
    result.external_events = external_events;
    result.commands_events = commands_events;
    result.internal_events = internal_events;

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
              .equals("ACT")) {
            InternalEvent new_event = new InternalEvent(event_string);
            internal_events.put(new_event.getName(), new_event);
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
   * Return an Events object reprenseting the events listed in the `events`
   * String
   * 
   * @param events
   *          The input string to be parsed
   * @return The equivalent Events object
   */
  private Events getEvents(String events) {
    String[] array_of_events = events.split("OU");
    Events result = new Events();

    for (int i = 0; i < array_of_events.length; i++) {
      String event_string = array_of_events[i].trim();

      if (!event_string.equals("")) {
        SingleEvent new_event = eventFactory(event_string);
        result.addEvent(new_event);
      }
    }
    return result;
  }

  /**
   * Return an Actions object reprenseting the events listed in the `actions`
   * String
   * 
   * @param actions
   *          The input string to be parsed
   * @return The equivalent Actions object
   * @throws Exception
   */
  private Actions getActions(String actions) throws Exception {
    /* TODO : split around "/" and take the left part */
    String[] array_of_actions = actions.split(";");
    Actions result = new Actions();

    for (int i = 0; i < array_of_actions.length; i++) {
      String event_string = array_of_actions[i].trim();

      if (!event_string.equals("")) {
        SingleEvent new_action = actionFactory(event_string);
        result.add(new_action);
      }

    }
    return result;
  }

  private AbstractCondition getCondition(String condition) {
    return new Condition(condition);
  }

  /**
   * Return the SingleEvent object associated to `event_name`
   * 
   * @param event_name
   *          The name of the event (including the prefix)
   * @return The SingleEvent associated with it
   */
  private SingleEvent eventFactory(String event_name) {
    SingleEvent result;

    result = external_events.get(event_name);
    if (result != null) {
      return result;
    }
    result = internal_events.get(event_name);
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
      new_event = new InternalEvent(event_name);
      internal_events.put(new_event.getName(), (InternalEvent) new_event);
      break;
    case "ACT":
      /*
       * It is necessarily an external event since it has not been identified as
       * an internal event during the first parsing
       */
      new_event = new ExternalEvent(event_name);
      external_events.put(new_event.getName(), (ExternalEvent) new_event);
      break;
    default:
      // throw new UnsupportedOperationException(
      // "When parsing the events field : " + event_name);
      new_event = new UnknownEvent("CONDITION_" + event_name);
    }
    return new_event;

  }

  private SingleEvent actionFactory(String event_name) throws Exception {
    SingleEvent result;

    result = external_events.get(event_name);
    if (result != null) {
      return result;
    }
    result = commands_events.get(event_name);
    if (result != null) {
      return result;
    }
    result = internal_events.get(event_name);
    if (result != null) {
      return result;
    }

    SingleEvent new_event;
    switch (event_name.substring(0, event_name.indexOf('_'))) {
    case "DTP":
    case "ATP":
    case "CMD":
    case "FCI":
      new_event = new CommandEvent(event_name);
      commands_events.put(new_event.getName(), (CommandEvent) new_event);
      break;
    case "IND":
      new_event = new InternalEvent(event_name);
      internal_events.put(new_event.getName(), (InternalEvent) new_event);
      break;
    case "ACT":
      throw new Exception("The ACT " + event_name
          + " is not already an internal event");
    default:
      // throw new UnsupportedOperationException(
      // "When parsing the events field : " + event_name);
      new_event = new UnknownEvent(event_name);
    }
    return new_event;
  }

  public void saveInFile(String file) throws IOException {
    File selected_file = new File(file);
    BufferedWriter writer = new BufferedWriter(new FileWriter(selected_file));
    Iterator<StateMachine> state_machine_iterator = state_machines
        .values()
        .iterator();
    while (state_machine_iterator.hasNext()) {
      StateMachine state_machine_reader = state_machine_iterator.next();
      Iterator<State> state_iterator = state_machine_reader.states();
      while (state_iterator.hasNext()) {
        State state_reader = state_iterator.next();
        Iterator<Transition> transition_iterator = state_reader.transitions();
        while (transition_iterator.hasNext()) {
          Transition transition_reader = transition_iterator.next();
          writer.write(state_machine_reader.getName().trim());
          writer.newLine();
          writer.write(transition_reader.getSource().getId().trim());
          writer.newLine();
          writer.write(transition_reader.getDestination().getId().trim());
          writer.newLine();
          Events events_to_write = transition_reader.getEvent();
          writer.write(concatenateEventsWithOU(events_to_write));
          writer.newLine();
          writer.write(transition_reader.getCondition().toString().trim());
          writer.newLine();
          writer.write(transition_reader.getActions().toString().trim());
          writer.newLine();
        }
      }
    }

    writer.close();
  }

  private String concatenateEventsWithOU(Events events) {
    StringBuilder sb = new StringBuilder();
    Iterator<SingleEvent> single_event_iterator = events.singleEvent();
    while (single_event_iterator.hasNext()) {
      SingleEvent single_event = single_event_iterator.next();
      sb.append(single_event.toString() + " OU ");
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return state_machines.toString();
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
}
