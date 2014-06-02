package engine;

import graph.GlobalState;
import graph.Model;
import graph.State;
import graph.StateMachine;
import graph.Transition;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import abstractGraph.AbstractGlobalState;
import abstractGraph.conditions.Variable;
import abstractGraph.events.CommandEvent;
import abstractGraph.events.ExternalEvent;
import abstractGraph.events.ModelCheckerEvent;
import abstractGraph.events.SingleEvent;
import abstractGraph.events.SynchronisationEvent;
import abstractGraph.events.VariableChange;

public class GraphSimulator implements
    GraphSimulatorInterface<GlobalState, StateMachine, State, Transition> {

  private GlobalState internal_global_state = new GlobalState();

  /** This is the list of the different queues used in the simulator. */
  private LinkedList<SingleEvent> internal_functional_event_queue = new LinkedList<SingleEvent>();
  private LinkedList<SingleEvent> internal_proof_event_queue = new LinkedList<SingleEvent>();
  private LinkedList<SingleEvent> temporary_queue = new LinkedList<SingleEvent>();

  /** This is the model to execute */
  private Model model;

  /** This is the model of the proof */
  private Model proof;

  public GraphSimulator(Model model, Model proof, GlobalState global_state) {
    this.model = model;
    this.proof = proof;
    this.internal_global_state = global_state;
  }

  public LinkedList<SingleEvent> getInternal_functional_event_queue() {
    return internal_functional_event_queue;
  }

  public LinkedList<SingleEvent> getInternal_proof_event_queue() {
    return internal_proof_event_queue;
  }

  public LinkedList<SingleEvent> getTemporary_queue() {
    return temporary_queue;
  }

  public GraphSimulator(Model model, GlobalState global_state) {
    this.model = model;
    this.internal_global_state = global_state;
  }

  public GraphSimulator(Model model, Model proof) {
    this.model = model;
    this.proof = proof;
  }

  public GraphSimulator(Model model) {
    this.model = model;
  }

  public GlobalState getGlobalState() {
    return internal_global_state;
  }

  public void setGlobalState(GlobalState global_state) {
    this.internal_global_state = global_state;
  }

  public Model getModel() {
    return model;
  }

  public Model getProof() {
    return proof;
  }

  /**
   * Execute a single event (that can be either external or internal).
   * 
   * @details
   *          Search for the transitions that contain the event. For every
   *          transition, evaluate the condition and if the value of the
   *          condition is true, process the action and update the state.
   *          It will first add in the internal event queue the synchronization
   *          events.
   *          Then, it will update all the variables affected by a
   *          VariableChange. If a variable is modified, an event saying that
   *          the variable has been set to a given value will be generated.
   * @param model
   *          The model to execute.
   * @param global_state
   *          The global state containing the current states and the values
   *          of the variables. It will be modified in place.
   * @param event
   *          The event to process.
   * @param event_list
   *          The list in which the generated internal events will be added.
   */
  private void processSingleEvent(Model model,
      AbstractGlobalState<StateMachine, State, Transition> global_state,
      SingleEvent event, LinkedList<SingleEvent> event_list) {

    LinkedHashMap<StateMachine, State> temporary_tag =
        new LinkedHashMap<StateMachine, State>();

    for (StateMachine state_machine : model) {
      State current_state = global_state.getState(state_machine);
      Iterator<Transition> transition_iterator = current_state
          .iteratorTransitions(event);
      while (transition_iterator.hasNext()) {
        Transition transition = transition_iterator.next();

        if (transition.evalCondition(global_state)) {
          temporary_tag.put(state_machine, transition.getDestination());
          processAction(transition.getActions().iterator(),
              global_state, event_list);
        }
      }
      // Update the states machines tags in the global state.
      for (Entry<StateMachine, State> entry : temporary_tag.entrySet()) {
        global_state.setState(entry.getKey(), entry.getValue());
      }
    }
    // Put the variables change event at the end of the event queue.
    event_list.addAll(temporary_queue);

    temporary_queue.clear();
    System.out.print("-->" + event.toString() + " -->\n"
        + "Internal FIFO " + event_list.toString() + "\n"
        + global_state);
  }

  /**
   * Sub-function to execute a set of single events.
   * 
   * @details
   *          Read the different Actions in the action field of the transition.
   *          If the action is a variable change, the value of that variable
   *          will be changed and the event put in a temporary queue (Only if
   *          the value changed).
   *          If the action is a synchronization event, it will be put in the
   *          event queue.
   *          If the action is an external command, it will be send to the
   *          external environment to be executed.
   *          If the action is a model checker event, it will set the isP5 isP6
   *          or isP7 to true depending of the action.
   * 
   *          At the end of the processing, all events in the temporary queue
   *          will be added to the event queue.
   * 
   * @param single_event_iterator
   *          An iterator over all the SingleEvents to execute.
   * @param global_state
   *          The global state where to search for the value of the variable.
   * @param event_list
   *          The event list in which the new events will be added.
   */
  private void processAction(Iterator<SingleEvent> single_event_iterator,
      AbstractGlobalState<StateMachine, State, Transition> global_state,
      LinkedList<SingleEvent> event_list) {

    while (single_event_iterator.hasNext()) {
      SingleEvent single_event = single_event_iterator.next();

      if (single_event instanceof VariableChange) {
        VariableChange variable_change = (VariableChange) single_event;

        /* We set the variable to true if it is not negated and vice versa */
        if (global_state
            .setVariableValue(variable_change.getModifiedVariable(),
                !variable_change.isNegated())) {
          temporary_queue.add(variable_change);
        }

      } else if (single_event instanceof SynchronisationEvent) {
        event_list.add(single_event);
      } else if (single_event instanceof CommandEvent) {
        // TODO implements the external environment
        System.out.print("The external command " + single_event
            + " has to be executed by the environment.\n");
      } else if (single_event instanceof ModelCheckerEvent) {
        switch (single_event.getName()) {
        case "P_5":
          global_state.setIsSafe(false);
          break;
        case "P_6":
          global_state.setIsLegal(false);
          break;
        case "P_7":
          global_state.setNotP7(false);
          break;
        default:
          throw new IllegalArgumentException("The argument "
              + single_event.getName() + " isn't defined in the model.\n");
        }
      }
    }
  }

  private void execute(Model m, GlobalState global_state,
      LinkedList<ExternalEvent> external_events_list) {

    LinkedList<SingleEvent> single_event_list;
    if (m == model) {
      single_event_list = internal_functional_event_queue;
    } else if (m == proof) {
      single_event_list = internal_proof_event_queue;
    } else {
      throw new Error();
    }
    System.out.print("Initial external FIFO " + external_events_list + "\n");
    while (!external_events_list.isEmpty()) {
      ExternalEvent head = external_events_list.removeFirst();
      System.out.print("External FIFO " + external_events_list + "\n");

      execute(m, global_state, head, single_event_list);
    }
  }

  /**
   * Execute all the list of external events in the right order using the
   * current states and the variables value in the global state of the argument
   * in the model.
   * 
   * @param global_state
   * @param external_events_list
   *          This list is emptied by this function.
   */
  public void executeOnlyFunctional(GlobalState global_state,
      LinkedList<ExternalEvent> external_events_list) {
    execute(model, global_state, external_events_list);
  }

  /**
   * {@inheritDoc #executeOnlyFunctional(GlobalState, LinkedList)}
   */
  public void executeOnlyFunctional(
      LinkedList<ExternalEvent> external_events_list) {
    executeOnlyFunctional(internal_global_state, external_events_list);
  }

  /**
   * Execute one external event, and all the internal events generated by that
   * external event in the model. Take the value of variables and the current
   * states from the global state in the argument.
   * 
   * @param external_events_list
   *          This list is emptied by this function.
   * @param internal_global_state
   *          an external to the simulator global state. It will be modified in
   *          place and will be the result value.
   * @return The global state modified after processing the external event.
   */
  private void executeProof(GlobalState global_state,
      LinkedList<SingleEvent> external_events_list) {

    if (proof == null) {
      throw new NullPointerException(
          "This simulator does not contain a proof model.");
    }

    System.out.print(
        "Initial external proof FIFO " + external_events_list + "\n");
    while (!external_events_list.isEmpty()) {
      SingleEvent head = external_events_list.removeFirst();
      System.out.print("External Proof FIFO " + external_events_list + "\n");
      execute(proof, global_state, head, internal_proof_event_queue);
    }
  }

  /**
   * Execute the model m, starting from the given GlobalState, on the event e
   * and using the internal event queue `single_event_queue`.
   * 
   * @param m
   *          The model to run.
   * @param starting_state
   *          The state to use. It will be modified in place.
   * @param e
   *          The event to execute.
   * @param single_event_queue
   *          The queue to use for the internal events.
   * @return
   */
  private GlobalState execute(Model m, GlobalState starting_state,
      SingleEvent e,
      LinkedList<SingleEvent> single_event_queue) {

    processSingleEvent(m, starting_state, e, single_event_queue);
    while (!single_event_queue.isEmpty()) {
      SingleEvent head = single_event_queue.remove();
      processSingleEvent(m, starting_state, head, single_event_queue);
    }
    return starting_state;
  }

  /**
   * Execute an external event and launch the proof model with it.
   * It execute one external event in the proof model, then process it in the
   * model. With the generated internal event of the model, it executes one
   * event
   * in the proof model, then in the model, then executes all the generated
   * event
   * in the proof model.
   * Use the global state given in the argument.
   * 
   * @param starting_state
   *          an external to the simulator global state. It will be modified in
   *          place and will be the result value.
   * @param event
   * @return The global state modified after the beginning of the
   */
  @Override
  public GlobalState execute(GlobalState starting_state, ExternalEvent event) {

    if (proof != null) {
      execute(proof, starting_state, event, internal_proof_event_queue);
    }
    processSingleEvent(model, starting_state, event,
        internal_functional_event_queue);

    @SuppressWarnings("unchecked")
    LinkedList<SingleEvent> transfert_list =
        (LinkedList<SingleEvent>) internal_functional_event_queue.clone();

    executeProof(starting_state, transfert_list);

    while (!internal_functional_event_queue.isEmpty()) {
      SingleEvent head = internal_functional_event_queue.removeFirst();
      transfert_list.clear();

      processSingleEvent(model, starting_state, head, transfert_list);
      internal_functional_event_queue.addAll(transfert_list);

      if (proof != null) {
        executeProof(starting_state, transfert_list);
      }
    }
    return internal_global_state;
  }

  /**
   * Same as {@link #execute(GlobalState, ExternalEvent)} but uses
   * the internal GlobalState.
   */
  @Override
  public GlobalState execute(ExternalEvent event) {
    return execute(internal_global_state, event);
  }

  public GlobalState execute(Iterable<ExternalEvent> list) {
    GlobalState result = internal_global_state;
    for (ExternalEvent e : list) {
      result = execute(e);
    }
    return result;
  }

  /**
   * Check that the functional model and the proof model can be executed
   * simultaneously.
   * 
   * @details
   *          If verifies that:
   *          <ol>
   *          <li>
   *          1) the proof model does not write any variable that belongs to the
   *          functional model.</li>
   *          <li>
   *          2) the proof model does not write any synchronization event that
   *          is used (i.e. written or listened) by the functional model.</li>
   *          <li>
   *          3) the proof model does not write ExternalCommands</li>
   *          <li>
   *          4) the functional model does not write ModelCheckerEvents</li>
   *          </ol>
   * @return true if the models are ok
   */
  public boolean checkCompatibility() {
    if (model == null) {
      System.err.println("The functionnal model within the simulator is null");
      return false;
    }
    if (proof == null) {
      return true;
    }

    for (StateMachine machine : proof) {
      Iterator<Transition> transition_iterator = machine.iteratorTransitions();
      while (transition_iterator.hasNext()) {
        Transition transition = transition_iterator.next();

        for (SingleEvent event : transition.getEvents()) {
          if (event instanceof ExternalEvent) {

          } else if (event instanceof SynchronisationEvent) {

          } else if (event instanceof CommandEvent) {

          } else if (event instanceof VariableChange) {

          }
        }

        for (SingleEvent event : transition.getActions()) {
          if (event instanceof VariableChange) {
            /* Verification of 1) */
            Variable var = ((VariableChange) event).getModifiedVariable();
            if (model.containsVariable(var)) {
              System.err.println(
                  "The proof model does write the variable " + var +
                      " which is also present in the functional model.");
              return false;
            }
          } else if (event instanceof SynchronisationEvent) {
            /* Verification of 2) */
            if (model
                .containsSynchronisationEvent((SynchronisationEvent) event)) {
              System.err
                  .println(
                  "The proof model does write the synchronisation event" +
                      event + " which is also present in the functional model.");
              return false;
            }
          } else if (event instanceof CommandEvent) {
            /* Verification of 3) */
            System.err.println(
                "The proof model does write the external command " + event);
            return false;
          }

        }
      }
    }

    /* Verification of 4) */
    for (StateMachine machine : model) {
      Iterator<Transition> transition_iterator = machine.iteratorTransitions();
      while (transition_iterator.hasNext()) {
        Transition transition = transition_iterator.next();

        for (SingleEvent event : transition.getActions()) {
          /* Verification of 4) */
          if (event instanceof ModelCheckerEvent) {

            System.err.println(
                "The functional model does write a variable that is reserved"
                    + " to proof models: " + event);
            return false;

          }
        }
      }
    }

    return true;
  }
}
