package domainSpecificLanguage.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import utils.Pair;
import abstractGraph.AbstractGlobalState;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.conditions.Formula;
import abstractGraph.events.Assignment;
import abstractGraph.events.CommandEvent;
import abstractGraph.events.ComputerCommandFunction;
import abstractGraph.events.EnumeratedVariableChange;
import abstractGraph.events.Events;
import abstractGraph.events.ExternalEvent;
import abstractGraph.events.InternalEvent;
import abstractGraph.events.ModelCheckerEvent;
import abstractGraph.events.SingleEvent;
import abstractGraph.events.SynchronisationEvent;
import abstractGraph.events.VariableChange;
import domainSpecificLanguage.DSLGlobalState.DSLGlobalState;
import domainSpecificLanguage.graph.DSLModel;
import domainSpecificLanguage.graph.DSLState;
import domainSpecificLanguage.graph.DSLStateMachine;
import domainSpecificLanguage.graph.DSLTransition;
import engine.GraphSimulatorInterface;

/**
 * A simulator for a couple (functional model, proof model).
 * The proof model can be null.
 * 
 * @details This class is NOT thread safe.
 */
public class DSLSequentialGraphSimulator<GS extends AbstractGlobalState<DSLStateMachine, DSLState, DSLTransition, ?>>
    implements
    GraphSimulatorInterface<GS, DSLStateMachine, DSLState, DSLTransition> {

  /** This is the list of the different queues used in the simulator. */
  protected LinkedList<SingleEvent> internal_functional_event_queue =
      new LinkedList<>();
  protected LinkedList<SingleEvent> internal_proof_event_queue =
      new LinkedList<>();
  protected LinkedList<SingleEvent> commands_queue =
      new LinkedList<>();
  protected LinkedList<SingleEvent> temporary_commands_queue =
      new LinkedList<>();
  protected LinkedList<ExternalEvent> ACT_FCI_queue =
      new LinkedList<>();
  protected LinkedHashMap<DSLStateMachine, DSLState> functionnal_transitions_pull_list =
      new LinkedHashMap<>();
  protected LinkedHashMap<DSLStateMachine, DSLState> proof_transitions_pull_list =
      new LinkedHashMap<>();
  protected LinkedList<ExternalEvent> restrained_external_event_list = null;
  protected HashMap<EnumeratedVariable, Byte> temporary_variable_change =
      new HashMap<>();

  /* Only used when executing the micro-steps */
  protected LinkedList<SingleEvent> external_proof_event_queue =
      new LinkedList<SingleEvent>();

  /** This is the model to execute */
  protected DSLModel functional_model;

  /** This is the model of the proof */
  protected DSLModel proof;

  protected boolean verbose = false;

  /** This is the union of the variables of both models */
  HashSet<EnumeratedVariable> variables;

  public DSLSequentialGraphSimulator(DSLModel model, DSLModel proof) {
    this.functional_model = model;
    this.proof = proof;

    variables = new HashSet<>();
    variables.addAll(model.variables);
    variables.addAll(proof.variables);

    checkCompatibility();
  }

  public DSLSequentialGraphSimulator(DSLModel model) {
    this.functional_model = model;

    variables = new HashSet<>();
    variables.addAll(model.variables);

    checkCompatibility();
  }

  @Override
  public String globalStateToString(GS global_state) {
    return ((DSLGlobalState) global_state).toString(variables);
  }

  public int getNumberVariables() {
    return functional_model.variables.size() + proof.variables.size();
  }

  @Override
  public DSLSequentialGraphSimulator<GS> clone() {
    DSLSequentialGraphSimulator<GS> result =
        new DSLSequentialGraphSimulator<>(this.functional_model, this.proof);
    result.setVerbose(this.verbose);
    return result;
  }

  public LinkedHashMap<DSLStateMachine, DSLState> getFunctionnalTransitionsPullList() {
    return functionnal_transitions_pull_list;
  }

  public LinkedHashMap<DSLStateMachine, DSLState> getProofTransitionsPullList() {
    return proof_transitions_pull_list;
  }

  public LinkedList<SingleEvent> getInternalFunctionalEventQueue() {
    return internal_functional_event_queue;
  }

  public LinkedList<SingleEvent> getInternalProofEventQueue() {
    return internal_proof_event_queue;
  }

  public LinkedList<SingleEvent> getExternalProofEventQueue() {
    return external_proof_event_queue;
  }

  public LinkedList<SingleEvent> getCommandsQueue() {
    return commands_queue;
  }

  public void setVerbose(boolean value) {
    this.verbose = value;
  }

  @Override
  public DSLModel getModel() {
    return functional_model;
  }

  @Override
  public DSLModel getProof() {
    return proof;
  }

  public LinkedList<ExternalEvent> getACTFCIList() {
    return ACT_FCI_queue;
  }

  public void setRestrainedExternalEventList(
      LinkedList<ExternalEvent> external_event_list) {
    restrained_external_event_list = external_event_list;
  }

  /**
   * 
   * @return True if there is no internal events to process in the functional
   *         model. Note that the proof model is then also stable.
   */
  public boolean isStable() {
    return internal_functional_event_queue.size() == 0;
  }

  /* Used for the next function */
  private LinkedHashMap<DSLStateMachine, DSLState> temporary_tag =
      new LinkedHashMap<>();
  private LinkedList<InternalEvent> temporary_queue =
      new LinkedList<>();

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
   *          The event to process. If this parameter is null, it does neither
   *          execute any event nor raise an error.
   * @param event_list
   *          The list in which the generated internal events will be added.
   */
  protected void processSingleEvent(
      DSLModel model,
      AbstractGlobalState<DSLStateMachine, DSLState, DSLTransition, ?> global_state,
      SingleEvent event, LinkedList<SingleEvent> event_list) {

    temporary_tag.clear();

    for (DSLStateMachine state_machine : model) {
      DSLState current_state = global_state.getState(state_machine);
      assert current_state != null : "No state selected for the state machine "
          + state_machine.getName();
      Iterator<DSLTransition> transition_iterator =
          current_state.iteratorTransitions(event);
      while (transition_iterator.hasNext()) {
        DSLTransition transition = transition_iterator.next();

        boolean evaluation;

        try {
          evaluation = transition.evalCondition(global_state);
        } catch (NoSuchElementException e) {
          throw new Error("When evaluation the transition " + transition
              + ", it was impossible to retrieve a value. The error was:\n "
              + e.toString());
        }

        if (evaluation) {
          temporary_tag.put(state_machine, transition.getDestination());
          processAction(transition.getActions().iterator(),
              global_state, event_list);

          /* ONLY one transition should be true per automaton */
          break;
        }
      }
      // Update the states machines tags in the global state after saving the
      // list of transitions pull.
      if (model == this.functional_model) {
        functionnal_transitions_pull_list.putAll(temporary_tag);
      } else {
        proof_transitions_pull_list.putAll(temporary_tag);
      }
      for (Entry<DSLStateMachine, DSLState> entry : temporary_tag.entrySet()) {
        global_state.setState(entry.getKey(), entry.getValue());
      }
    }
    // Put the variables change event at the end of the event queue.
    for (Entry<EnumeratedVariable, Byte> entry : temporary_variable_change
        .entrySet()) {
      global_state.setVariableValue(entry.getKey(), entry.getValue());
    }
    temporary_variable_change.clear();
    event_list.addAll(temporary_queue);
    temporary_queue.clear();

    if (this.verbose) {
      System.out.print("-->"
          + ((event != null) ? event.toString() : "null")
          + ((model == this.proof) ? " in proof model"
              : " in functionnal model")
          + " -->\n"
          + "Internal FIFO " + event_list.toString() + "\n"
          + global_state);
    }
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
  private void processAction(
      Iterator<SingleEvent> single_event_iterator,
      AbstractGlobalState<DSLStateMachine, DSLState, DSLTransition, ?> global_state,
      LinkedList<SingleEvent> event_list) {

    while (single_event_iterator.hasNext()) {
      SingleEvent single_event = single_event_iterator.next();

      if (single_event instanceof VariableChange) {
        VariableChange variable_change = (VariableChange) single_event;
        throw new Error(
            "VariableChange events are not allowed in the DSLGraphSimulator:"
                + variable_change);
      } else if (single_event instanceof SynchronisationEvent) {
        event_list.add(single_event);
      } else if (single_event instanceof Assignment) {
        EnumeratedVariable variable = ((Assignment) single_event).getVariable();
        Byte value = ((Assignment) single_event).getValue();

        temporary_variable_change.put(variable, value);
        temporary_queue.add(new EnumeratedVariableChange(variable));
      } else if (single_event instanceof ComputerCommandFunction) {

        commands_queue.add(single_event);
        if (!(functional_model
            .getACTFCI((ComputerCommandFunction) single_event) == null)) {

          LinkedList<Pair<Formula, LinkedList<ExternalEvent>>> list = functional_model
              .getACTFCI((ComputerCommandFunction) single_event);

          for (Pair<Formula, LinkedList<ExternalEvent>> condition_with_act : list) {
            if (condition_with_act.getFirst().eval(
                global_state.getValuation())) {
              ACT_FCI_queue.addAll(condition_with_act.getSecond());
            }
          }
        }
        if (proof != null) {
          temporary_commands_queue.add(single_event);
        }
      } else if (single_event instanceof CommandEvent) {
        commands_queue.add(single_event);
        if (proof != null) {
          temporary_commands_queue.add(single_event);
        }
      } else if (single_event instanceof ModelCheckerEvent) {
        commands_queue.add(single_event);

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
      } else {
        throw new Error("Unknown action Event " + single_event.getClass()
            + " : " + single_event);
      }
    }
  }

  /**
   * Execute the model m, starting from the given CompactGlobalState, on the
   * event e and using the internal event queue `single_event_queue`.
   * 
   * @param m
   *          The model to run.
   * @param starting_state
   *          The state to use. It will be modified in place.
   * @param e
   *          The event to execute.
   * @param single_event_queue
   *          The queue to use for the internal events.
   */
  protected void execute(DSLModel m, GS starting_state,
      SingleEvent e,
      LinkedList<SingleEvent> single_event_queue) {

    processSingleEvent(m, starting_state, e, single_event_queue);
    while (!single_event_queue.isEmpty()) {
      SingleEvent head = single_event_queue.remove();
      processSingleEvent(m, starting_state, head, single_event_queue);
    }
  }

  /**
   * Execute one external event, and all the internal events generated by that
   * external event in the model. Take the value of variables and the current
   * states from the global state in the argument.
   * 
   * @param external_events_list
   *          This list is emptied by this function.
   * @param global_state
   *          an external to the simulator global state. It will be modified in
   *          place and will be the result value.
   */
  protected void executeProofCompletely(GS global_state,
      LinkedList<SingleEvent> external_events_list) {

    if (proof == null) {
      throw new NullPointerException(
          "This simulator does not contain a proof model.");
    }
    if (verbose) {
      System.out.print(
          "Initial external proof FIFO " + external_events_list + "\n");
    }
    while (!external_events_list.isEmpty()) {
      SingleEvent head = external_events_list.removeFirst();
      if (verbose) {
        System.out.print("External Proof FIFO " + external_events_list + "\n");
      }
      execute(proof, global_state, head, internal_proof_event_queue);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * Execute an external event and launch the proof model with it.
   * It execute one external event in the proof model, then process it in the
   * model. With the generated internal event of the model, it executes one
   * event in the proof model, then in the model, then executes all the
   * generated event in the proof model. When the internal event get empty, it
   * executes all the act FCI possibly generated by that external event.
   * Use the global state given in the argument.
   * 
   * If `event` is null, it does only finish the execution of the proof and
   * functional model to be ready for the next external event.
   */
  @Override
  public GS execute(GS starting_state, ExternalEvent event) {
    GS copied_starting_state = executeSimulator(starting_state, event);
    return executeACTFCIwithProof(copied_starting_state);
  }

  /**
   * {@inheritDoc}
   * 
   * Execute an external event and launch the proof model with it.
   * It execute one external event in the proof model, then process it in the
   * model. With the generated internal event of the model, it executes one
   * event in the proof model, then in the model, then executes all the
   * generated event in the proof model.
   * Use the global state given in the argument.
   * 
   * If `event` is null, it does only finish the execution of the proof and
   * functional model to be ready for the next external event.
   */
  public GS executeSimulator(GS starting_state,
      ExternalEvent event) {

    functionnal_transitions_pull_list.clear();
    proof_transitions_pull_list.clear();
    commands_queue.clear();
    if (proof == null) {
      external_proof_event_queue.clear();
    }

    @SuppressWarnings("unchecked")
    GS copied_starting_state = (GS) starting_state.clone();

    LinkedList<SingleEvent> transfert_list = new LinkedList<SingleEvent>();
    SingleEvent curr_event = event;
    if (curr_event != null) {
      external_proof_event_queue.add(curr_event);
    }

    do {
      transfert_list.clear();

      processSingleEvent(functional_model, copied_starting_state, curr_event,
          transfert_list);
      internal_functional_event_queue.addAll(transfert_list);

      external_proof_event_queue.addAll(transfert_list);
      external_proof_event_queue.addAll(temporary_commands_queue);
      temporary_commands_queue.clear();

      curr_event = internal_functional_event_queue.poll();
    } while (curr_event != null);

    if (proof != null) {
      executeProofCompletely(copied_starting_state, external_proof_event_queue);
    }
    return copied_starting_state;
  }

  /**
   * Execute all the ACT FCI that are in ACT_FCI_queue.
   * 
   * @param global_state
   */
  public GS executeACTFCIwithProof(GS global_state) {
    GS result = executeAll(global_state, ACT_FCI_queue);
    ACT_FCI_queue.clear();
    return result;
  }

  /**
   * Same as {@link #execute(CompactGlobalState, ExternalEvent)} but uses a
   * LinkedList
   * of events
   */
  public GS executeAll(GS starting_state,
      LinkedList<ExternalEvent> list) {
    GS result = starting_state;
    while (!list.isEmpty()) {
      ExternalEvent event = list.poll();
      result = execute(result, event);
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
  private boolean checkCompatibility() {
    if (functional_model == null) {
      throw new Error("The functionnal model within the simulator is null");
    }

    /* Verification of 4) */
    for (DSLStateMachine machine : functional_model) {
      Iterator<DSLTransition> transition_iterator = machine
          .iteratorTransitions();
      while (transition_iterator.hasNext()) {
        DSLTransition transition = transition_iterator.next();

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

    if (proof == null) {
      return true;
    }

    for (DSLStateMachine machine : proof) {
      Iterator<DSLTransition> transition_iterator = machine
          .iteratorTransitions();
      while (transition_iterator.hasNext()) {
        DSLTransition transition = transition_iterator.next();

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
            BooleanVariable var =
                ((VariableChange) event).getModifiedVariable();
            if (functional_model.containsVariable(var)) {
              System.err.println(
                  "The proof model does write the variable " + var +
                      " which is also present in the functional model.");
              return false;
            }
          } else if (event instanceof SynchronisationEvent) {
            /* Verification of 2) */
            if (functional_model
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

    return true;
  }

  @Override
  public LinkedHashSet<ExternalEvent> getPossibleEvent(GS global_state) {
    LinkedHashSet<ExternalEvent> list_events =
        new LinkedHashSet<ExternalEvent>();
    for (DSLStateMachine state_machine : getModel()) {
      DSLState current_state = global_state.getState(state_machine);
      for (DSLTransition transition : current_state) {
        Events events = transition.getEvents();
        for (SingleEvent single_event : events) {
          if (single_event instanceof ExternalEvent) {
            assert (!single_event.getName().startsWith("IND_"));
            if (restrained_external_event_list == null
                || restrained_external_event_list.contains(single_event)) {
              if (!single_event.getName().startsWith("ACT_")) {
                list_events.add((ExternalEvent) single_event);
              }
            }
          }
        }
      }
    }
    /*
     * Add all the external events of the proof that are not internal
     * events of the functional
     */
    if (proof != null) {
      for (DSLStateMachine state_machine : getProof()) {
        DSLState current_state = global_state.getState(state_machine);
        for (DSLTransition transition : current_state) {
          Events events = transition.getEvents();
          for (SingleEvent single_event : events) {

            /* If it is an external event of the functional model */
            if (single_event instanceof ExternalEvent) {
              ExternalEvent external_event = (ExternalEvent) single_event;
              if (getModel().containsExternalEvent(external_event)) {
                if (restrained_external_event_list == null
                    || restrained_external_event_list.contains(single_event)) {
                  if (!single_event.getName().startsWith("ACT_")) {
                    list_events.add((ExternalEvent) single_event);
                  }
                }
              }
            }
          }
        }
      }
    }
    return list_events;
  }

  /**
   * @return The initial global state.
   */
  public DSLGlobalState getInitialGlobalState() {
    DSLGlobalState global_state = new DSLGlobalState(getNumberVariables());
    for (DSLStateMachine m : functional_model) {
      global_state.setState(m, m.getInitial_state());
    }
    for (DSLStateMachine m : proof) {
      global_state.setState(m, m.getInitial_state());
    }
    for (Entry<EnumeratedVariable, Byte> pair : functional_model.initial_values
        .entrySet()) {
      global_state.setVariableValue(pair.getKey(), pair.getValue());
    }
    for (Entry<EnumeratedVariable, Byte> pair : proof.initial_values.entrySet()) {
      global_state.setVariableValue(pair.getKey(), pair.getValue());
    }
    return global_state;
  }
}
