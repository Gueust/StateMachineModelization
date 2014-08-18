package engine;

import graph.GlobalState;
import graph.Model;
import graph.State;
import graph.StateMachine;
import graph.Transition;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import abstractGraph.conditions.BooleanVariable;
import abstractGraph.events.ExternalEvent;
import abstractGraph.events.SingleEvent;

/**
 * A simulator for a couple (functional model, proof model).
 * When executing an external event, it executes it completely in the functional
 * model before executing it plus the resulting events in the proof model.
 * 
 * @details This class is NOT thread safe.
 */
public class SequentialGraphSimulator extends
    GraphSimulator<GlobalState, StateMachine, State, Transition> {

  private int total_number_state_machines = 0;

  public SequentialGraphSimulator(Model model, Model proof) {
    super(model, proof);

    /*
     * We number the state machines such that every one has a unique identifier
     * from 0, N-1 where N is the total number of state machines
     */
    for (StateMachine machine : getModel()) {
      machine.setUniqueIdentifier(total_number_state_machines);
      total_number_state_machines++;
    }
    if (proof != null) {
      for (StateMachine machine : proof) {
        machine.setUniqueIdentifier(total_number_state_machines);
        total_number_state_machines++;
      }
    }
  }

  public SequentialGraphSimulator(Model model) {
    this(model, null);
  }

  @Override
  public Model getModel() {
    return (Model) model;
  }

  @Override
  public Model getProof() {
    return (Model) proof;
  }

  @Override
  public SequentialGraphSimulator clone() {
    SequentialGraphSimulator result =
        new SequentialGraphSimulator((Model) this.model, (Model) this.proof);
    result.setVerbose(this.verbose);
    return result;
  }

  /*
   * It is true iff the proof model has processed an external event and
   * that the functional model is ready to execute the next external event.
   * It will be set to true when ending the execution in the proof model, and
   * will be set to false when the functional model has executed an external
   * event.
   */
  private boolean functional_model_allowed_to_execute_next_ext_event = true;

  /**
   * This function execute the smallest step possible:
   * <ol>
   * <li>
   * If the proof model is not completely executed, it execute one step in the
   * proof model.</li>
   * <li>
   * If the proof model is stable (has_executed_external_event_in_functional is
   * set to false), it then executes a step in the functional model if there is
   * any</li>
   * <li>
   * Otherwise, it executes the first external event in the functional model
   * (has_executed_external_event_in_functional set to true).</li>
   * </ol>
   * 
   * @param external_events
   */
  public void processSmallestStep(GlobalState global_state,
      LinkedList<ExternalEvent> external_events) {

    /* If there are internal events in the functional model */
    if (internal_functional_event_queue.size() != 0) {
      functionnal_transitions_pull_list.clear();
      commands_queue.clear();

      processSingleEvent(model, global_state,
          internal_functional_event_queue.remove(), external_proof_event_queue);

      internal_functional_event_queue.addAll(external_proof_event_queue);
      if (proof != null) {
        external_proof_event_queue.addAll(temporary_commands_queue);
        temporary_commands_queue.clear();
      } else {
        /* The proof model is not used, so we clear its external event queue */
        external_proof_event_queue.clear();
      }
      return;
    }

    /* External events to be executed in the functional model */
    if (functional_model_allowed_to_execute_next_ext_event || proof == null) {
      functionnal_transitions_pull_list.clear();
      commands_queue.clear();

      ExternalEvent external_event_to_execute = external_events.poll();
      processSingleEvent(model, global_state,
          external_event_to_execute, external_proof_event_queue);
      functional_model_allowed_to_execute_next_ext_event = false;
      internal_functional_event_queue.addAll(external_proof_event_queue);
      if (proof != null) {
        external_proof_event_queue.addAll(temporary_commands_queue);
        temporary_commands_queue.clear();
      }

      functional_model_allowed_to_execute_next_ext_event = false;

      return;
    }

    /* If there are internal events to execute in the proof model */
    if (proof != null && internal_proof_event_queue.size() != 0) {
      proof_transitions_pull_list.clear();
      processSingleEvent(proof, global_state,
          internal_proof_event_queue.remove(), internal_proof_event_queue);
      /*
       * If we have ended the execution, we let the functional execute an other
       * external event
       */
      if (internal_proof_event_queue.size() == 0 &&
          external_proof_event_queue.size() == 0) {
        functional_model_allowed_to_execute_next_ext_event = true;
      }
      return;
    }

    /*
     * There is an external event in the functional and it is not the turn of
     * the functional model
     */
    if (proof != null && external_events.size() == 0) {
      proof_transitions_pull_list.clear();
      processSingleEvent(proof, global_state,
          external_proof_event_queue.remove(), internal_proof_event_queue);
    }

    if (proof != null && external_proof_event_queue.size() != 0) {
      processSingleEvent(proof, global_state,
          external_proof_event_queue.remove(), internal_proof_event_queue);
      return;
    }

  }

  /**
   * {@inheritDoc}
   * 
   * Execute an external event and launch the proof model with it.
   * It executes completely the functional model before executing the proof
   * model.
   * During a micro-step, the actions that will be transfered to the proof model
   * will be in that order: the external event, SYNs, then variable change
   * events, and then commands.
   * 
   * If `event` is null, it does only finish the execution of the functional and
   * proof models to be ready for the next external event.
   */
  @Override
  public GlobalState executeSimulator(GlobalState starting_state,
      ExternalEvent event) {

    functionnal_transitions_pull_list.clear();
    proof_transitions_pull_list.clear();
    commands_queue.clear();
    if (proof == null) {
      external_proof_event_queue.clear();
    }

    GlobalState copied_starting_state = starting_state.clone();

    LinkedList<SingleEvent> transfert_list = new LinkedList<SingleEvent>();
    SingleEvent curr_event = event;
    if (curr_event != null) {
      external_proof_event_queue.add(curr_event);
    }

    do {
      transfert_list.clear();

      processSingleEvent(model, copied_starting_state, curr_event,
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
   * Should NOT be used. Used only by the tests.
   * We coould modify the tests to make this function private.
   */
  public GlobalState emptyGlobalState() {
    return new GlobalState(total_number_state_machines);
  }

  /**
   * Generate all the initial states that can be generated using all the
   * possible combination of CTLs.
   * 
   * @throws IOException
   */
  public void generateAllInitialStates(
      ModelChecker<GlobalState, StateMachine, State, Transition> model_checker,
      String init_file) throws IOException {
    HashMap<String, String> CTL_list = getModel().regroupCTL();

    generateAllInitialStates(CTL_list, init_file,
        new HashMap<String, Boolean>(),
        model_checker);
  }

  /**
   * Initialize the model with the given set of CTL:
   * <ol>
   * <li>
   * It does initialize all the states machines to the state "0"</li>
   * <li>
   * It executes ACT_INIT with the given set of CTL as true variables.</li>
   * <li>Clear the valuation of the CTLs that had previously been added.</li>
   * </ol>
   * 
   * @param external_values
   *          The list of the couple (CTL, value for this CTL).
   *          It must only contains CTLs and it must not contain opposite CTLs.
   * @param init_file
   *          The file where to find the list of the initialization events. One
   *          per line.
   * @throws IOException
   */
  public GlobalState init(HashMap<String, Boolean> external_values,
      String init_file) throws IOException {

    GlobalState global_state = emptyGlobalState();

    /* Initialization of the states of all the state machines */
    for (StateMachine machine : model) {
      global_state.setState(machine, machine.getState("0"));
    }
    if (proof != null) {
      for (StateMachine machine : proof) {
        global_state.setState(machine, machine.getState("0"));
      }
    }

    /* We set the CTLs to true */
    LinkedList<BooleanVariable> to_delete_from_valuation = new LinkedList<BooleanVariable>();
    for (Entry<String, Boolean> assignation : external_values.entrySet()) {
      BooleanVariable var = (BooleanVariable) model.getVariable(assignation
          .getKey());
      to_delete_from_valuation.add(var);
      global_state.setVariableValue(var, assignation.getValue());
    }

    /* We execute ACT_INIT */
    if (init_file == null) {
      global_state = execute(global_state, ACT_INIT);
    } else {
      BufferedReader buff = new BufferedReader(new FileReader(init_file));

      LinkedList<ExternalEvent> result = new LinkedList<>();

      String line;
      while ((line = buff.readLine()) != null) {
        ExternalEvent event = new ExternalEvent(line.trim());
        result.add(event);
      }

      buff.close();

      global_state = executeAll(global_state, result);
    }

    /* We delete the CTLs from the valuation */
    for (BooleanVariable variable : to_delete_from_valuation) {
      global_state.getValuation().remove(variable);
    }

    return global_state;
  }

  /**
   * Generate recursively all the initial states.
   * 
   * @param set
   *          The list of the positive CTL in the model without a value assigned
   *          to them.
   * @param tmp
   *          An empty HashMap used internally to store the already set CTLs.
   */
  int i = 0;

  private void generateAllInitialStates(Set<String> set,
      HashMap<String, Boolean> fixed_ctls,
      String init_file,
      GlobalState current_state,
      ModelChecker<GlobalState, StateMachine, State, Transition> model_checker)
      throws IOException {

    /* Terminal case */
    if (set.isEmpty()) {
      GlobalState gs = init(fixed_ctls, init_file);
      if (gs.isLegal()) {
        System.out.print(i++ + "\n");
        model_checker.addInitialState(gs);
      }
      return;
    }

    /* Recursion */
    Iterator<String> ctl_iterator = set.iterator();
    String ctl_name = ctl_iterator.next();
    ctl_iterator.remove();

    fixed_ctls.put(ctl_name, true);
    generateAllInitialStates(new HashSet<String>(set), fixed_ctls, init_file,
        current_state,
        model_checker);

    fixed_ctls.put(ctl_name, false);
    generateAllInitialStates(new HashSet<String>(set), fixed_ctls, init_file,
        current_state,
        model_checker);
  }

  /**
   * Generate all the initial states that can be generated using all the
   * possible combination of CTL_list and fixing the fixed_CTL_list
   * 
   * @throws IOException
   */
  public void generateAllInitialStates(
      HashMap<String, String> CTL_list,
      String init_file,
      HashMap<String, Boolean> fixed_CTL_list,
      ModelChecker<GlobalState, StateMachine, State, Transition> model_checker)
      throws IOException {

    generateAllInitialStates(CTL_list.keySet(), fixed_CTL_list, init_file,
        emptyGlobalState(),
        model_checker);
  }
}
