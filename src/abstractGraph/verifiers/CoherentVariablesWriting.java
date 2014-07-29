package abstractGraph.verifiers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import abstractGraph.AbstractModel;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.events.Actions;
import abstractGraph.events.SingleEvent;
import abstractGraph.events.VariableChange;

/**
 * NOTE: this verification unit is only done for Models (and not DSLModels).
 * 
 * This verification units checks the following property:
 * let v be a variable, let M be the state machine that is writing on it.
 * Then, for all states s in M, v must whether be true or (exclusively) false in
 * s.
 * It must not be possible to find 2 paths setting the variable in a different
 * value within a state).
 * 
 * The verification on every state machine is done by:
 * <ol>
 * <li>
 * initializing all states to have an unknown value for all variables</li>
 * <li>
 * setting the truth value gathered from the actions (i.e. writing A or not
 * A)</li>
 * <li>
 * spreading this truth values through unlabeled transitions (i.e. that does not
 * write A).</li>
 * </ol>
 * 
 */
public class CoherentVariablesWriting<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>>
    extends AbstractVerificationUnit<M, S, T> {

  /* These are empty iff the model verifies this verifier. */
  private LinkedList<AbstractState<?>> counter_example_states = new LinkedList<>();
  private LinkedList<EnumeratedVariable> counter_example_variable = new LinkedList<>();
  private LinkedList<M> counter_example_machines = new LinkedList<>();

  @Override
  public boolean check(AbstractModel<M, S, T> m, boolean verbose) {
    return checkAll(m, verbose);
  }

  /*
   * For all variable written by a state machine, each state is assigned with a
   * value for this variable
   */
  private static final Byte FALSE = 0;
  private static final Byte TRUE = 1;
  private static final Byte UNDEFINED = 2;

  /**
   * Check that a state machine is coherent.
   * 
   * @param machine
   *          The state machine to verify.
   * @param written_variables
   *          The list of the written variables to verify.
   * @return true if the state machine respects the contract.
   */
  @SuppressWarnings("unchecked")
  private boolean checkOneStateMachine(M machine,
      LinkedList<EnumeratedVariable> written_variables,
      boolean verbose) throws Error {

    if (written_variables == null || written_variables.isEmpty()) {
      return true;
    }

    HashMap<AbstractState<?>, HashMap<EnumeratedVariable, Byte>> value_associated =
        new HashMap<>();
    // Initialization of the variables written by this machines
    Iterator<AbstractState<?>> it_states =
        (Iterator<AbstractState<?>>) machine.iterator();
    while (it_states.hasNext()) {
      AbstractState<?> state = it_states.next();
      HashMap<EnumeratedVariable, Byte> state_variables = new HashMap<EnumeratedVariable, Byte>();

      Iterator<EnumeratedVariable> it_vars = written_variables.iterator();
      while (it_vars.hasNext()) {
        state_variables.put(it_vars.next(), UNDEFINED);
      }
      value_associated.put(state, state_variables);
    }

    /* If one requires to have the details */
    /*
     * System.out.println("Printing the associated values after init");
     * Iterator<Entry<State, HashMap<Variable, Byte>>> aa = value_associated
     * .entrySet()
     * .iterator();
     * while (aa.hasNext())
     * {
     * Entry<State, HashMap<Variable, Byte>> bb = aa.next();
     * System.out.println("[" + bb.getKey().getId() + ": " + bb.getValue());
     * }
     */
    /*
     * For every variable, the initial states to propagate are the one with an
     * incoming transition writing this variable
     */
    HashMap<BooleanVariable, HashSet<AbstractState<?>>> states_to_propagate =
        new HashMap<>();
    /*
     * Setting the truth values known directly with the written of variables
     * (VariableChange)
     */
    Iterator<AbstractTransition<?>> it_trans =
        (Iterator<AbstractTransition<?>>) machine.iteratorTransitions();
    while (it_trans.hasNext()) {
      AbstractTransition<?> transition = it_trans.next();
      Actions actions = transition.getActions();
      Iterator<SingleEvent> it_actions = actions.iterator();
      while (it_actions.hasNext()) {
        SingleEvent event = it_actions.next();
        if (event instanceof VariableChange) {
          VariableChange vc_event = (VariableChange) event;
          BooleanVariable variable = vc_event.getModifiedVariable();
          AbstractState<?> state = transition.getDestination();

          /*
           * If the HasSet associated to this variable does not exist, it is
           * created
           */
          HashSet<AbstractState<?>> states_to_propagate_for_this_variable =
              states_to_propagate.get(variable);
          if (states_to_propagate_for_this_variable == null) {
            states_to_propagate_for_this_variable = new HashSet<>();
            states_to_propagate.put(variable,
                states_to_propagate_for_this_variable);
          }
          /* The current state will be used to propagate */
          states_to_propagate_for_this_variable.add(state);

          Byte value;
          if (vc_event.isNegated()) {
            value = FALSE;
          } else {
            value = TRUE;
          }

          Byte old_value = value_associated.get(state).get(variable);
          if (old_value != null &&
              old_value.byteValue() != UNDEFINED &&
              !old_value.equals(value)) {
            counter_example_machines.add(machine);
            counter_example_variable.add(variable);
            counter_example_states.add(state);

            return false;
          }

          value_associated.get(state).put(variable, value);
        }
      }
    }

    /* If one requires to have the details */
    /*
     * System.out.println("Printing the associated values after first passage");
     * aa = value_associated
     * .entrySet()
     * .iterator();
     * while (aa.hasNext())
     * {
     * Entry<State, HashMap<Variable, Byte>> bb = aa.next();
     * System.out.println("[" + bb.getKey().getId() + ": " + bb.getValue());
     * }
     * 
     * 
     * System.out.println("States to propagate before propagation");
     * Iterator<Entry<Variable, HashSet<State>>> entry_it =
     * states_to_propagate.entrySet().iterator();
     * while (entry_it.hasNext()) {
     * Entry<Variable, HashSet<State>> tt = entry_it.next();
     * System.out.println("Variable " + tt.getKey() + " :  ");
     * HashSet<State> hs = tt.getValue();
     * Iterator<State> it_state = hs.iterator();
     * while (it_state.hasNext()) {
     * System.out.println(it_state.next().getId());
     * }
     * }
     */
    /*
     * We do the propagation separately for every variable.
     */
    Iterator<EnumeratedVariable> it_vars = written_variables.iterator();
    while (it_vars.hasNext()) {
      EnumeratedVariable variable = it_vars.next();

      HashSet<AbstractState<?>> states_to_propagate_var =
          states_to_propagate.get(variable);
      if (states_to_propagate_var.isEmpty()) {
        throw new Error("The state machine " + machine.getName()
            + " is writting the variable " + variable
            + " but no writtenn has been found.");
      }

      HashSet<AbstractState<?>> visited_states = new HashSet<>();
      // System.out.println("We do the proof for variable: " + variable);

      while (!states_to_propagate_var.isEmpty()) {
        /* We remove from the to-visit list and set to visited */
        AbstractState<?> state = states_to_propagate_var.iterator().next();
        states_to_propagate_var.remove(state);
        visited_states.add(state);

        /* We retrieve the current value */
        Byte propagating_value = value_associated.get(state).get(variable);

        if (propagating_value == UNDEFINED || propagating_value == null)
          throw new Error("Impossible scenario");

        /*
         * We visit the states linked to the current one if they have not
         * already been visited.
         */
        it_trans = (Iterator<AbstractTransition<?>>) state.iterator();
        while (it_trans.hasNext()) {
          AbstractTransition<?> transition = it_trans.next();
          AbstractState<?> destination = transition.getDestination();

          /*
           * If the transition write the variable, then we do not check for
           * coherence
           */
          boolean transition_does_write_variable = false;
          Iterator<SingleEvent> it_single_event = transition
              .getActions()
              .iterator();
          while (it_single_event.hasNext()) {
            SingleEvent e = it_single_event.next();
            if (e instanceof VariableChange) {
              VariableChange vc_event = (VariableChange) e;
              if (vc_event.getModifiedVariable() == variable) {
                transition_does_write_variable = true;
                break;
              }
            }
          }

          if (transition_does_write_variable) {
            continue;
          }

          /* The next state will be used to propagate */
          if (!visited_states.contains(destination)) {
            states_to_propagate_var.add(destination);
          }

          /* We do the checking */
          Byte old_value = value_associated.get(destination).get(variable);
          if (old_value != null &&
              old_value.byteValue() != UNDEFINED &&
              !old_value.equals(propagating_value)) {
            counter_example_machines.add(machine);
            counter_example_variable.add(variable);
            counter_example_states.add(destination);

            return false;
          } else {
            /* We update the target */
            assert (propagating_value != UNDEFINED);
            value_associated.get(destination).put(variable, propagating_value);
          }
        }
      }
    }

    return true;
  }

  /*
   * Creates the Hashmap associating to every state machine the variables it is
   * writing.
   */
  private HashMap<M, LinkedList<EnumeratedVariable>> writtenVariables(
      HashMap<EnumeratedVariable, LinkedList<M>> hashMap) {

    HashMap<M, LinkedList<EnumeratedVariable>> written_variables =
        new HashMap<M, LinkedList<EnumeratedVariable>>();

    Iterator<Entry<EnumeratedVariable, LinkedList<M>>> it_var_statemachine =
        hashMap.entrySet().iterator();

    while (it_var_statemachine.hasNext()) {
      Entry<EnumeratedVariable, LinkedList<M>> entry =
          it_var_statemachine.next();
      EnumeratedVariable variable = entry.getKey();
      LinkedList<M> sm = entry.getValue();

      Iterator<M> sm_iterator = sm.iterator();
      while (sm_iterator.hasNext()) {
        M machine = sm_iterator.next();

        LinkedList<EnumeratedVariable> written_vars = written_variables
            .get(machine);
        if (written_vars == null) {
          written_vars = new LinkedList<EnumeratedVariable>();
          written_variables.put(machine, written_vars);
        }

        written_vars.add(variable);
      }
    }
    return written_variables;
  }

  @Override
  public boolean checkAll(AbstractModel<M, S, T> m, boolean verbose)
      throws Error {

    counter_example_states.clear();
    counter_example_variable.clear();
    counter_example_machines.clear();

    boolean result = true;

    HashMap<M, LinkedList<EnumeratedVariable>> written_variables =
        writtenVariables(m.getWritingStateMachines());

    Iterator<M> it_state_machines =
        (Iterator<M>) m.iterator();
    while (it_state_machines.hasNext()) {
      M machine = it_state_machines.next();

      if (!checkOneStateMachine(machine, written_variables.get(machine),
          verbose)) {
        result = false;
      }
    }

    if (verbose) {
      if (result) {
        System.out.println(successMessage());
      } else {
        System.out.println(errorMessage());
      }
    }
    return result;
  }

  @Override
  public String errorMessage() {
    StringBuffer result = new StringBuffer();
    result.append("[FAILURE] Incoherent writing of variables is done.\n");
    for (int i = 0; i < counter_example_machines.size(); i++) {
      String machine_name = counter_example_machines.get(i).getName();
      String variable_name = counter_example_variable.get(i).toString();
      String state = counter_example_states.get(i).getId();

      result.append("In state machine " + machine_name + " in state " + state
          + " with variable " + variable_name + ".\n");
    }
    return result.toString();
  }

  @Override
  public String successMessage() {
    if (counter_example_states.isEmpty()) {
      return "[SUCCESS] Checking that every state machines write on the variables in a coherent way...OK";
    } else {
      throw new IllegalArgumentException(
          "The last call to check returned with errors."
              + "You should not be calling sucessMessage().");
    }
  }
}
