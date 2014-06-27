package engine;

import graph.GlobalState;
import graph.Model;

import java.util.LinkedList;

import abstractGraph.events.ExternalEvent;
import abstractGraph.events.SingleEvent;

/**
 * A simulator for a couple (functional model, proof model).
 * When executing an external event, it executes it completely in the functional
 * model before executing it plus the resulting events in the proof model.
 * 
 * @details This class is NOT thread safe.
 */
public class SequentialGraphSimulator extends GraphSimulator {

  public SequentialGraphSimulator(Model model, Model proof) {
    super(model, proof);
  }

  public SequentialGraphSimulator(Model model) {
    super(model);
  }

  @Override
  public SequentialGraphSimulator clone() {
    SequentialGraphSimulator result =
        new SequentialGraphSimulator(this.model, this.proof);
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
  public GlobalState execute(GlobalState starting_state,
      ExternalEvent event) {

    functionnal_transitions_pull_list.clear();
    proof_transitions_pull_list.clear();
    commands_queue.clear();

    GlobalState copied_starting_state = starting_state.clone();

    LinkedList<SingleEvent> transfert_list = new LinkedList<SingleEvent>();
    SingleEvent curr_event = event;

    do {
      transfert_list.clear();

      processSingleEvent(model, copied_starting_state, curr_event,
          transfert_list);
      internal_functional_event_queue.addAll(transfert_list);

      external_proof_event_queue.add(curr_event);
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
}
