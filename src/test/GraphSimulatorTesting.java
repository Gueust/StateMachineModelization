package test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import engine.GraphSimulator;
import graph.GlobalState;
import graph.GraphFactoryAEFD;
import graph.Model;
import graph.State;
import graph.StateMachine;
import graph.Transition;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Test;

import abstractGraph.AbstractGlobalState;
import abstractGraph.conditions.Variable;
import abstractGraph.events.ExternalEvent;

public class GraphSimulatorTesting {

  /**
   * Load a file and build the model associated to that file.
   * 
   * @param name
   *          the name of the file located in
   *          src/test/resources/GraphSimulatorTesting
   * @return a model built from the file.
   * @throws IOException
   */
  private Model loadFile(String name) throws IOException {

    String class_name = this.getClass().getSimpleName();

    GraphFactoryAEFD test =
        new GraphFactoryAEFD();
    Model model = test.buildModel("src/test/resources/" + class_name + "/"
        + name, "Testing model");
    return model;
  }

  /**
   * Test the graph simulator by simulating different graphs with external
   * events and ensure that the result is correct.
   * 
   * @throws IOException
   * 
   * @details This test uses different files representing simple graphs:
   *          <ol>
   *          <li>
   *          "Graph_with_no_external_events.txt" is a file containing a graph
   *          without external events.</li>
   *          <li>
   *          "Graph_testing_different_external_event.txt" is a file containing
   *          the different external events found in the model</li>
   *          <li>
   *          "Graph_with_propagation.txt" is a graph containing different
   *          internal events triggered by an external event</li>
   *          <li>
   *          "Graph_with_condition.txt" a graph containing a two transitions
   *          with the same event</li>
   *          <li>
   *          "Graph_testing_variables_value.txt" a graph with different change
   *          of the variable</li>
   *          <li>
   *          "Graph_P5.txt" a graph with a P5 in an action field</li>
   *          <li>
   *          "Graph_P6.txt" a graph with a P6 in an action field</li>
   *          <li>
   *          "Graph_P7.txt" a graph with a P7 in an action field</li>
   *          <li>
   *          "Graph_with_alarm.txt" a graph with an alarm set in the first
   *          transition (Action written in the form
   *          "[Liste of actions] / [Liste of Alarms] Action"</li>
   *          </ol>
   */
  @Test
  public void TraceTesting() throws IOException {

    String[] files = {
        "Graph_with_no_external_events.txt",
        "Graph_testing_different_external_event.txt",
        "Graph_with_propagation.txt",
        "Graph_with_condition.txt",
        "Graph_testing_variables_value.txt",
        "Graph_P5.txt",
        "Graph_P6.txt",
        "Graph_P7.txt",
        "Graph_with_alarm.txt",

    };

    String[] files_for_proof_testing = {
        "Graph_with_safety_error.txt",
        "AP_of_Graph_with_safety_error.txt"
    };

    LinkedList<LinkedList<ExternalEvent>> liste_of_list_external_event = new LinkedList<LinkedList<ExternalEvent>>();
    LinkedList<GraphSimulator> liste_simulator = initListOfSimulator(files);
    LinkedList<LinkedList<ExternalEvent>> liste_of_list_external_event_proof = new LinkedList<LinkedList<ExternalEvent>>();
    LinkedList<GraphSimulator> liste_simulator_proof = initListOfSimulatorWithProofModel(files_for_proof_testing);
    // TODO fill in external event

    String[] events = { "CTL_1", "MSG_1" };
    addListeExternalEvent(liste_of_list_external_event, events);
    events = new String[] { "CTL_1", "MSG_1", "ACT_1", "FTP_1" };
    addListeExternalEvent(liste_of_list_external_event, events);
    events = new String[] { "CTL_1" };
    addListeExternalEvent(liste_of_list_external_event, events);
    events = new String[] { "CTL_1" };
    addListeExternalEvent(liste_of_list_external_event, events);
    events = new String[] { "CTL_1" };
    addListeExternalEvent(liste_of_list_external_event, events);
    events = new String[] { "CTL_1" };
    addListeExternalEvent(liste_of_list_external_event, events);
    events = new String[] { "CTL_1" };
    addListeExternalEvent(liste_of_list_external_event, events);
    events = new String[] { "CTL_1" };
    addListeExternalEvent(liste_of_list_external_event, events);
    events = new String[] { "CTL_1" };
    addListeExternalEvent(liste_of_list_external_event, events);

    events = new String[] { "CTL_1" };
    addListeExternalEvent(liste_of_list_external_event_proof, events);

    GraphSimulator simulator = liste_simulator.removeFirst();
    // Test the file "Graph_with_no_external_events.txt"
    simulator.executeAll(liste_of_list_external_event.removeFirst());
    // Verify that the current state of the state machine is "0".
    assertTrue("Error on " + files[0], simulator.getGlobalState().getState(
        "Graph_with_no_external_event").getId().equals("0"));

    simulator = liste_simulator.removeFirst();
    // Test the file "Graph_testing_different_external_event.txt"
    simulator.executeAll(liste_of_list_external_event.removeFirst());
    // Verify that the current state of the state machine is "4".
    assertTrue("Error on " + files[1], simulator.getGlobalState().getState(
        "Page 1").getId().equals("4"));

    simulator = liste_simulator.removeFirst();
    // Test the file "Graph_with_propagation.txt"
    simulator.executeAll(liste_of_list_external_event.removeFirst());
    // Verify that the current state of the state machine "Page 1" is "1".
    assertTrue("Error on " + files[2], simulator.getGlobalState().getState(
        "Page 1").getId().equals("1"));
    // Verify that the current state of the state machine "Page 2" is "1".
    assertTrue("Error on " + files[2], simulator.getGlobalState().getState(
        "Page 2").getId().equals("1"));
    // Verify that the current state of the state machine "Page 3" is "1".
    assertTrue("Error on " + files[2], simulator.getGlobalState().getState(
        "Page 3").getId().equals("1"));

    simulator = liste_simulator.removeFirst();
    // Test the file "Graph_with_condition.txt"
    simulator.executeAll(liste_of_list_external_event.removeFirst());
    // Verify that the current state of the state machine is "2".
    assertTrue("Error on " + files[3], simulator.getGlobalState().getState(
        "Page 1").getId().equals("2"));

    simulator = liste_simulator.removeFirst();
    // Test the file "Graph_testing_variables_value.txt"
    simulator.executeAll(liste_of_list_external_event.removeFirst());
    // Verify that the current state of the state machine "Page 1" is "1".
    assertTrue("Error on " + files[4], simulator.getGlobalState().getState(
        "Page 1").getId().equals("1"));
    // Verify that the current state of the state machine "Page 2" is "1".
    assertTrue("Error on " + files[4], simulator.getGlobalState().getState(
        "Page 2").getId().equals("1"));
    // Verify that the current state of the state machine "Page 3" is "1".
    assertTrue("Error on " + files[4], simulator.getGlobalState().getState(
        "Page 3").getId().equals("1"));
    // Verify that the value of the variables are corrects.
    // Retrieve the variable from the name
    Model model_tmp = simulator.getModel();
    assertTrue("Error on " + files[4], simulator
        .getGlobalState()
        .getVariableValue(model_tmp.getVariable("IND_A_Actif")) == false);
    assertTrue("Error on " + files[4], simulator
        .getGlobalState()
        .getVariableValue(model_tmp.getVariable("IND_B_Actif")) == false);
    assertTrue("Error on " + files[4], simulator
        .getGlobalState()
        .getVariableValue(model_tmp.getVariable("IND_C_Actif")) == true);

    simulator = liste_simulator.removeFirst();
    // Test the file "Graph_P5.txt"
    simulator.executeAll(liste_of_list_external_event.removeFirst());
    // Verify that the current state of the state machine is "4".
    assertTrue("Error on " + files[5], !simulator.getGlobalState().isSafe());

    simulator = liste_simulator.removeFirst();
    // Test the file "Graph_P6.txt"
    simulator.executeAll(liste_of_list_external_event.removeFirst());
    // Verify that the current state of the state machine is "4".
    assertTrue("Error on " + files[5], !simulator.getGlobalState().isLegal());

    simulator = liste_simulator.removeFirst();
    // Test the file "Graph_P7.txt"
    simulator.executeAll(liste_of_list_external_event.removeFirst());
    // Verify that the current state of the state machine is "4".
    assertTrue("Error on " + files[5], !simulator.getGlobalState().isNotP7());

    simulator = liste_simulator.removeFirst();
    // Test the file "Graph_with_alarm.txt"
    simulator.executeAll(liste_of_list_external_event.removeFirst());
    // Verify that the current state of the state machine "Page 1" is "1".
    assertTrue("Error on " + files[6], simulator.getGlobalState().getState(
        "Page 1").getId().equals("0"));
    // Verify that the current state of the state machine "Page 2" is "1".
    assertTrue("Error on " + files[6], simulator.getGlobalState().getState(
        "Page 2").getId().equals("0"));
    // Verify that the current state of the state machine "Page 3" is "1".
    assertTrue("Error on " + files[6], simulator.getGlobalState().getState(
        "Page 3").getId().equals("1"));

    simulator = liste_simulator_proof.removeFirst();
    simulator.executeAll(liste_of_list_external_event_proof.removeFirst());
    assertTrue("Error on " + files_for_proof_testing[0],
        !simulator
            .getGlobalState()
            .isSafe());

  }

  /**
   * Add the list of external event to the list of list of external event.
   * 
   * @param liste_of_list_external_event
   * @param list_name_event
   */
  private void addListeExternalEvent(
      LinkedList<LinkedList<ExternalEvent>> liste_of_list_external_event,
      String[] list_name_event) {
    LinkedList<ExternalEvent> liste_external_events = new LinkedList<ExternalEvent>();
    for (String name_event : list_name_event) {
      ExternalEvent external_event = new ExternalEvent(name_event);
      liste_external_events.add(external_event);
    }
    liste_of_list_external_event.add(liste_external_events);

  }

  /**
   * Create the simulators from the text files by generating the models and
   * initializing the global state.
   * 
   * @param files
   *          an array of files to test.
   * @return a list of simulator.
   */

  private LinkedList<GraphSimulator> initListOfSimulator(String[] files) {

    GlobalState global_state;
    LinkedList<GraphSimulator> liste_simulator = new LinkedList<GraphSimulator>();
    try {
      for (int i = 0; i < files.length; i++) {
        Model model = new Model(files[i]);
        model = loadFile(files[i]);
        global_state = new GlobalState();
        Iterator<Variable> variable_iterator = model
            .iteratorExistingVariables();
        while (variable_iterator.hasNext()) {
          Variable variable = variable_iterator.next();
          global_state.setVariableValue(variable, true);
        }
        Iterator<StateMachine> state_machine_iterator = model
            .iterator();
        while (state_machine_iterator.hasNext()) {
          StateMachine state_machine = state_machine_iterator.next();
          global_state.setState(state_machine, state_machine.getState("0"));
        }
        GraphSimulator simulator = new GraphSimulator(model, global_state);
        liste_simulator.add(simulator);

      }

      return liste_simulator;

    } catch (IOException e) {
      e.printStackTrace();
      fail("Unexpected exception.");
    }

    return null;

  }

  /**
   * Create the simulators from the text files by generating the models and
   * initializing the global state. Create the simulators with proof model. Scan
   * the tab of files two by two, the first file is the functional file and the
   * second, the proof file.
   * 
   * @param files
   *          an array of files to test.
   * @return a list of simulator.
   */
  private LinkedList<GraphSimulator> initListOfSimulatorWithProofModel(
      String[] files) throws IOException {

    GlobalState global_state = new GlobalState();
    LinkedList<GraphSimulator> liste_simulator = new LinkedList<GraphSimulator>();
    String class_name = this.getClass().getSimpleName();

    GraphFactoryAEFD test = new GraphFactoryAEFD();
    try {
      for (int i = 0; i < files.length - 1; i = i + 2) {
        Model model = test.buildModel("src/test/resources/" + class_name + "/"
            + files[i], "Testing model");
        Model proof = test.buildModel("src/test/resources/" + class_name + "/"
            + files[i + 1], "Testing proof model");
        initGlobalState(model, global_state);
        initGlobalState(proof, global_state);
        GraphSimulator simulator;
        simulator = new GraphSimulator(model, proof, global_state);
        liste_simulator.add(simulator);

      }

      return liste_simulator;

    } catch (IOException e) {
      e.printStackTrace();
      fail("Unexpected exception.");
    }

    return null;

  }

  /**
   * Init the global state by putting all the current state of the state
   * machines of the model to "0" and all the variable to true.
   * 
   * @param model
   *          from where the global state will search for the state machines and
   *          the variables.
   * @param global_state
   */
  private void initGlobalState(Model model,
      AbstractGlobalState<StateMachine, State, Transition> global_state) {
    Iterator<Variable> variable_iterator = model
        .iteratorExistingVariables();
    while (variable_iterator.hasNext()) {
      Variable variable = variable_iterator.next();
      global_state.setVariableValue(variable, true);
    }
    Iterator<StateMachine> state_machine_iterator = model
        .iterator();
    while (state_machine_iterator.hasNext()) {
      StateMachine state_machine = state_machine_iterator.next();
      global_state.setState(state_machine, state_machine.getState("0"));
    }
  }

}
