package engine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import utils.Pair;
import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractModel;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.events.CommandEvent;
import abstractGraph.events.ComputerCommandFunction;
import abstractGraph.events.Events;
import abstractGraph.events.ExternalEvent;
import abstractGraph.events.ModelCheckerEvent;
import abstractGraph.events.SingleEvent;
import abstractGraph.events.SynchronisationEvent;
import abstractGraph.events.VariableChange;

/**
 * A simulator for a couple (functional model, proof model).
 * The proof model can be null.
 * 
 * @details This class is NOT thread safe.
 */
class GraphSimulator<GS extends AbstractGlobalState<M, S, T, ?>, M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>>
    implements
    GraphSimulatorInterface<GS, M, S, T> {

  /** This is the list of the different queues used in the simulator. */
  protected LinkedList<SingleEvent> internal_functional_event_queue =
      new LinkedList<SingleEvent>();
  protected LinkedList<SingleEvent> internal_proof_event_queue =
      new LinkedList<SingleEvent>();
  protected LinkedList<SingleEvent> commands_queue =
      new LinkedList<SingleEvent>();
  protected LinkedList<SingleEvent> temporary_commands_queue =
      new LinkedList<SingleEvent>();
  protected LinkedList<ExternalEvent> ACT_FCI_queue =
      new LinkedList<ExternalEvent>();
  protected LinkedHashMap<M, S> functionnal_transitions_pull_list =
      new LinkedHashMap<M, S>();
  protected LinkedHashMap<M, S> proof_transitions_pull_list =
      new LinkedHashMap<M, S>();
  protected LinkedList<ExternalEvent> restrained_external_event_list = null;
  protected HashMap<BooleanVariable, Boolean> temporary_variable_change = new HashMap<BooleanVariable, Boolean>();
  /* Only used when executing the micro-steps */
  protected LinkedList<SingleEvent> external_proof_event_queue =
      new LinkedList<SingleEvent>();

  /** This is the model to execute */
  protected AbstractModel<M, S, T> model;

  /** This is the model of the proof */
  protected AbstractModel<M, S, T> proof;

  protected boolean verbose = true;

  public GraphSimulator(AbstractModel<M, S, T> model,
      AbstractModel<M, S, T> proof) {
    this.model = model;
    this.proof = proof;

    checkCompatibility();
  }

  public GraphSimulator(AbstractModel<M, S, T> model) {
    this.model = model;
    checkCompatibility();
  }

  @Override
  public GraphSimulator<GS, M, S, T> clone() {
    GraphSimulator<GS, M, S, T> result =
        new GraphSimulator<GS, M, S, T>(this.model, this.proof);
    result.setVerbose(this.verbose);
    return result;
  }

  public LinkedHashMap<M, S> getFunctionnalTransitionsPullList() {
    return functionnal_transitions_pull_list;
  }

  public LinkedHashMap<M, S> getProofTransitionsPullList() {
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
  public AbstractModel<M, S, T> getModel() {
    return model;
  }

  @Override
  public AbstractModel<M, S, T> getProof() {
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

  /*
   * It is true iff the proof model has executed an external event that has not
   * been executed by the functional model
   */
  private boolean has_executed_external_event_in_proof = false;
  /* The last external event processed by the proof model */
  private ExternalEvent external_event_to_execute = null;

  /**
   * This function execute the smallest step possible:
   * <ol>
   * <li>
   * If the proof model is not completely executed, it execute one step in the
   * proof model</li>
   * <li>
   * If the proof model is stable, it then executes a step in the functional
   * model if there is any</li>
   * <li>
   * Otherwise, it executes the first external event in the proof model if not
   * already executed in the model. It will be executed in the functional model
   * otherwise.</li>
   * </ol>
   * 
   * @param external_events
   */
  public void processSmallestStep(GS internal_global_state,
      LinkedList<ExternalEvent> external_events) {

    if (proof != null && internal_proof_event_queue.size() != 0) {
      proof_transitions_pull_list.clear();
      processSingleEvent(proof, internal_global_state,
          internal_proof_event_queue.remove(), internal_proof_event_queue);
      return;
    }

    if (proof != null && external_proof_event_queue.size() != 0) {
      proof_transitions_pull_list.clear();
      processSingleEvent(proof, internal_global_state,
          external_proof_event_queue.remove(), internal_proof_event_queue);
      return;
    }

    if (internal_functional_event_queue.size() != 0) {
      functionnal_transitions_pull_list.clear();

      processSingleEvent(model, internal_global_state,
          internal_functional_event_queue.remove(), external_proof_event_queue);

      internal_functional_event_queue.addAll(external_proof_event_queue);

      if (proof != null) {
        external_proof_event_queue.addAll(temporary_commands_queue);
        temporary_commands_queue.clear();
      } else {
        /*
         * If the proof model is empty, we need to clear its queue that will
         * otherwise never be emptied
         */
        external_proof_event_queue.clear();
      }
      return;
    }

    if (has_executed_external_event_in_proof || proof == null) {
      functionnal_transitions_pull_list.clear();
      commands_queue.clear();
      external_event_to_execute = external_events.poll();
      processSingleEvent(model, internal_global_state,
          external_event_to_execute, external_proof_event_queue);
      has_executed_external_event_in_proof = false;
      internal_functional_event_queue.addAll(external_proof_event_queue);
      if (proof != null) {
        external_proof_event_queue.addAll(temporary_commands_queue);
        temporary_commands_queue.clear();
      }
    } else {
      proof_transitions_pull_list.clear();
      external_event_to_execute = external_events.poll();
      processSingleEvent(proof, internal_global_state,
          external_event_to_execute, internal_proof_event_queue);
      has_executed_external_event_in_proof = true;
    }

  }

  /* Used for the next function */
  private LinkedHashMap<M, S> temporary_tag =
      new LinkedHashMap<M, S>();
  private LinkedList<SingleEvent> temporary_queue =
      new LinkedList<SingleEvent>();

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
  protected void processSingleEvent(AbstractModel<M, S, T> model,
      AbstractGlobalState<M, S, T, ?> global_state,
      SingleEvent event, LinkedList<SingleEvent> event_list) {

    temporary_tag.clear();

    for (M state_machine : model) {
      S current_state = global_state.getState(state_machine);
      assert current_state != null : "No state selected for the state machine "
          + state_machine.getName();
      Iterator<T> transition_iterator =
          current_state.iteratorTransitions(event);
      while (transition_iterator.hasNext()) {
        T transition = transition_iterator.next();

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
          break;
        }
      }
      // Update the states machines tags in the global state after saving the
      // list of transitions pull.
      if (model == this.model) {
        functionnal_transitions_pull_list.putAll(temporary_tag);
      } else {
        proof_transitions_pull_list.putAll(temporary_tag);
      }
      for (Entry<M, S> entry : temporary_tag.entrySet()) {
        global_state.setState(entry.getKey(), entry.getValue());
      }
    }
    // Put the variables change event at the end of the event queue.
    for (Entry<BooleanVariable, Boolean> entry : temporary_variable_change
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
  private void processAction(Iterator<SingleEvent> single_event_iterator,
      AbstractGlobalState<M, S, T, ?> global_state,
      LinkedList<SingleEvent> event_list) {

    while (single_event_iterator.hasNext()) {
      SingleEvent single_event = single_event_iterator.next();

      if (single_event instanceof VariableChange) {
        VariableChange variable_change = (VariableChange) single_event;

        /*
         * We put the variable change in a temporary queue to be processed at
         * the end of processSingleEvent
         */
        if (!global_state.variableIsInitialized(variable_change
            .getModifiedVariable())) {
          temporary_variable_change.put(variable_change.getModifiedVariable(),
              !variable_change.isNegated());
        } else if (global_state
            .variableValueWillChanged(
                variable_change.getModifiedVariable(),
                !variable_change.isNegated())) {
          temporary_variable_change.put(variable_change.getModifiedVariable(),
              !variable_change.isNegated());
          temporary_queue.add(variable_change);
        }
      } else if (single_event instanceof SynchronisationEvent) {
        event_list.add(single_event);
      } else if (single_event instanceof ComputerCommandFunction) {

        commands_queue.add(single_event);
        if (!(model.getACTFCI((ComputerCommandFunction) single_event) == null)) {

          LinkedList<Pair<Formula, LinkedList<ExternalEvent>>> list = model
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
      }
    }
  }

  /**
   * Execute the model m, starting from the given CompactGlobalState, on the
   * event e
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
   */
  protected void execute(AbstractModel<M, S, T> m, GS starting_state,
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

    @SuppressWarnings("unchecked")
    GS copied_starting_state = (GS) starting_state.clone();

    functionnal_transitions_pull_list.clear();
    proof_transitions_pull_list.clear();
    commands_queue.clear();

    if (proof != null) {
      execute(proof, copied_starting_state, event, internal_proof_event_queue);
    }

    LinkedList<SingleEvent> transfert_list = new LinkedList<SingleEvent>();
    SingleEvent curr_event = event;

    do {
      transfert_list.clear();
      processSingleEvent(model, copied_starting_state, curr_event,
          transfert_list);
      internal_functional_event_queue.addAll(transfert_list);

      if (proof != null) {
        transfert_list.addAll(temporary_commands_queue);
        temporary_commands_queue.clear();
        executeProofCompletely(copied_starting_state, transfert_list);
      }

      curr_event = internal_functional_event_queue.poll();
    } while (curr_event != null);
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
    if (model == null) {
      throw new Error("The functionnal model within the simulator is null");
    }

    /* Verification of 4) */
    for (M machine : model) {
      Iterator<T> transition_iterator = machine.iteratorTransitions();
      while (transition_iterator.hasNext()) {
        T transition = transition_iterator.next();

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

    for (M machine : proof) {
      Iterator<T> transition_iterator = machine.iteratorTransitions();
      while (transition_iterator.hasNext()) {
        T transition = transition_iterator.next();

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
            BooleanVariable var = ((VariableChange) event)
                .getModifiedVariable();
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

    return true;
  }

  @Override
  public LinkedHashSet<ExternalEvent> getPossibleEvent(GS global_state) {
    LinkedHashSet<ExternalEvent> list_events =
        new LinkedHashSet<ExternalEvent>();
    for (M state_machine : getModel()) {
      S current_state = global_state.getState(state_machine);
      for (T transition : current_state) {
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
      for (M state_machine : getProof()) {
        S current_state = global_state.getState(state_machine);
        for (T transition : current_state) {
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

}
