package Graph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import abstractGraph.Conditions.Condition;
import abstractGraph.Events.CommandEvent;
import abstractGraph.Events.Events;
import abstractGraph.Events.ExternalEvent;
import abstractGraph.Events.InternalEvent;
import abstractGraph.Events.SingleEvent;
import Graph.Events.Actions;
import Graph.Events.UnknownEvent;
import Parser_Fichier_6lignes.Fichier6lignes;

public class GraphFactoryAEFD {

  private HashMap<String, StateMachine> state_machines;
  private HashMap<String, ExternalEvent> external_events;
  private HashMap<String, CommandEvent> commands_events;
  private HashMap<String, InternalEvent> internal_events;
  private HashMap<String, UnknownEvent> unknown_events;

  public GraphFactoryAEFD(String file) throws IOException {

    state_machines = new HashMap<String, StateMachine>();
    external_events = new HashMap<String, ExternalEvent>();
    commands_events = new HashMap<String, CommandEvent>();
    internal_events = new HashMap<String, InternalEvent>();
    unknown_events = new HashMap<String, UnknownEvent>();

    Fichier6lignes parser = new Fichier6lignes(file);

    while (parser.get6Lines()) {
      StateMachine state_machine = getStateMachine(parser.getGraphName());
      // Get the source state
      State from = retrieveState(state_machine.getName(), parser
          .getSourceState());
      // Get the destination state
      State to = retrieveState(state_machine.getName(), parser
          .getDestinationState());

      state_machine.addTransition(from, to, getEvents(parser.getEvent()),
          getCondition(parser.getCondition()), getActions(parser.getAction()));
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

  private StateMachine getStateMachine(String name) {
    StateMachine result = state_machines.get(name);
    if (result != null) {
      return result;
    } else {
      result = new StateMachine(name);
      state_machines.put(name, result);
      return result;
    }
  }

  private State retrieveState(String machine_name, String state_name) {
    StateMachine state_machine = getStateMachine(machine_name);

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
   */
  private Actions getActions(String actions) {
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

  private Condition getCondition(String condition) {
    return null;
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
    result = unknown_events.get(event_name);
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
      new_event = new UnknownEvent(event_name);
      unknown_events.put(new_event.getName(), (UnknownEvent) new_event);
      break;
    default:
      // throw new UnsupportedOperationException(
      // "When parsing the events field : " + event_name);
      new_event = new UnknownEvent("CONDITION_" + event_name);
    }
    return new_event;

  }

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
    result = internal_events.get(event_name);
    if (result != null) {
      return result;
    }
    result = unknown_events.get(event_name);
    if (result != null) {
      /*
       * If the ACT is in the action field, the it means that it's an internal
       * event
       */
      unknown_events.remove(event_name);
      result = new InternalEvent(event_name);
      internal_events.put(event_name, (InternalEvent) result);

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
      new_event = new InternalEvent(event_name);
      internal_events.put(new_event.getName(), (InternalEvent) new_event);
      break;
    default:
      // throw new UnsupportedOperationException(
      // "When parsing the events field : " + event_name);
      new_event = new UnknownEvent(event_name);
    }
    return new_event;

  }

  public void saveInFile(File selected_file) throws IOException {
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

  public String concatenateEventsWithOU(Events events) {
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
}
