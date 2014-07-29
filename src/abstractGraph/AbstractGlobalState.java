package abstractGraph;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import utils.Pair;
import utils.javaAgent.ObjectSizeFetcher;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.conditions.valuation.AbstractValuation;
import abstractGraph.conditions.valuation.Valuation;
import abstractGraph.events.ExternalEvent;

public abstract class AbstractGlobalState<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>, V extends AbstractValuation> {

  protected V variables_values;
  protected HashMap<M, S> state_machines_current_state =
      new HashMap<M, S>();

  protected boolean is_legal_state = true, is_safe_state = true;
  protected boolean isNotP7 = true;

  /*
   * The following attributes are used by the model checker to build the tree
   * and display it
   */
  public ExternalEvent last_processed_external_event = null;
  public AbstractGlobalState<M, S, T, ?> previous_global_state = null;

  public AbstractGlobalState(V variables_values) {
    this.variables_values = variables_values;
  }

  /**
   * Set the active state of `machine` to S.
   * 
   * @param machine
   *          The machine to modify.
   * @param S
   *          The state to set.
   */
  public void setState(M machine, S state) {
    state_machines_current_state.put(machine, state);
  }

  /**
   * Return the current state of a state machine.
   * 
   * @param machine
   * @return the current state if the state machine exists, null otherwise.
   */
  public S getState(M machine) {
    return state_machines_current_state.get(machine);
  }

  /**
   * Search for a state machine by name and return its current state.
   * 
   * @param state_machine_name
   * @return the current state if the state machine exists, null otherwise.
   */
  public S getState(String state_machine_name) {
    for (M state_machine : state_machines_current_state.keySet()) {
      if (state_machine.getName().equals(state_machine_name)) {
        return state_machines_current_state.get(state_machine);
      }
    }
    return null;
  }

  /**
   * 
   * @param variable
   * @return the value of the variable
   */
  public boolean getVariableValue(BooleanVariable variable) {
    return variables_values.getValue(variable);
  }

  public byte getVariableValue(EnumeratedVariable variable) {
    return variables_values.getValue(variable);
  }

  public String getStringValue(EnumeratedVariable variable) {
    if (variable instanceof BooleanVariable) {
      return Boolean.toString(getVariableValue((BooleanVariable) variable));
    } else {
      return variable.getOptionFromByte(getVariableValue(variable));
    }
  }

  /**
   * Change the value of the variable.
   * 
   * @param variable
   * @param value
   *          the new value to assign
   * @return true if the variable changed its value, false otherwise.
   */
  public boolean setVariableValue(BooleanVariable variable, boolean value) {
    return variables_values.setValue(variable, value);
  }

  /**
   * Change the value of the variable.
   * 
   * @param variable
   * @param value
   *          The new byte value to assign.
   * @return true if the variable changed its value, false otherwise.
   */
  public boolean setVariableValue(EnumeratedVariable variable, Byte value) {
    return variables_values.setValue(variable, value);
  }

  /**
   * @return the environment with the value of the variables.
   */
  public V getValuation() {
    return variables_values;
  }

  public boolean isLegal() {
    return is_legal_state;
  }

  public void setIsLegal(boolean legal) {
    is_legal_state = legal;
  }

  public boolean isSafe() {
    return is_safe_state;
  }

  public void setIsSafe(boolean safe) {
    is_safe_state = safe;
  }

  public boolean isNotP7() {
    return isNotP7;
  }

  public void setNotP7(boolean NotP7) {
    isNotP7 = NotP7;
  }

  public boolean variableValueWillChanged(BooleanVariable variable,
      boolean value) {
    return variables_values.variableValueWillChange(variable, value);
  }

  public boolean variableIsInitialized(BooleanVariable variable) {
    return variables_values.variableInitialized(variable);
  }

  @Override
  public String toString() {
    String result = "";

    for (M state_machine : state_machines_current_state.keySet()) {
      result = result + state_machine.getName()
          + " : state "
          + state_machines_current_state.get(state_machine).getId() + ".\n";
    }
    result = result + "The value of the variables are : "
        + variables_values + ".\n";
    result += "Safe: " + is_safe_state + ", legal: " + is_legal_state
        + ", isNotP7: " + isNotP7 + "\n";
    return result;
  }

  /**
   * The implementation make it possible to omit casts. However, all
   * implementations should simply use
   * {@link #copyTo(AbstractGlobalState, AbstractGlobalState)()}
   */
  @Override
  public abstract AbstractGlobalState<M, S, T, V> clone();

  /**
   * Copy A into B.
   * 
   * @param A
   * @param B
   */
  @SuppressWarnings("unchecked")
  protected void copyTo(AbstractGlobalState<M, S, T, V> A,
      AbstractGlobalState<M, S, T, V> B) {
    B.state_machines_current_state =
        (HashMap<M, S>) state_machines_current_state.clone();
    B.variables_values = (V) this.variables_values.clone();
    B.is_legal_state = this.is_legal_state;
    B.is_safe_state = this.is_safe_state;
    B.isNotP7 = this.isNotP7;
  }

  /* Automatically generated by Eclipse */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (isNotP7 ? 1231 : 1237);
    result = prime * result + (is_legal_state ? 1231 : 1237);
    result = prime * result + (is_safe_state ? 1231 : 1237);
    result = prime
        * result
        + ((state_machines_current_state == null) ? 0
            : state_machines_current_state.hashCode());
    result = prime * result
        + ((variables_values == null) ? 0 : variables_values.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    @SuppressWarnings("rawtypes")
    AbstractGlobalState other = (AbstractGlobalState) obj;
    if (isNotP7 != other.isNotP7)
      return false;
    if (is_legal_state != other.is_legal_state)
      return false;
    if (is_safe_state != other.is_safe_state)
      return false;
    if (state_machines_current_state == null) {
      if (other.state_machines_current_state != null)
        return false;
    } else if (!state_machines_current_state
        .equals(other.state_machines_current_state))
      return false;
    if (variables_values == null) {
      if (other.variables_values != null)
        return false;
    } else if (!variables_values.equals(other.variables_values))
      return false;
    return true;
  }

  /**
   * The java agent must have been loaded for this function to work.
   * 
   * @returnReturn a JVM implementation dependent approximation of the size of
   *               the object. It only takes into account the size of the actual
   *               allocated data for this object. In particular, it does not
   *               count the shared data.
   */
  public long sizeOf() {
    long result = 0;

    result += ObjectSizeFetcher.getObjectSize(variables_values);
    result += ObjectSizeFetcher.getObjectSize(state_machines_current_state);
    result += ObjectSizeFetcher.getObjectSize(is_legal_state);
    result += ObjectSizeFetcher.getObjectSize(is_safe_state);
    result += ObjectSizeFetcher.getObjectSize(isNotP7);

    return result;
  }

  /**
   * Prints the differences between the current_state and the other_state.
   * It supposes that both global states contains the SAME variables set.
   */
  public void compare(AbstractGlobalState<M, S, T, ?> other_state) {

    System.out.println("** Beginning of the comparison");

    if (is_legal_state != other_state.is_legal_state) {
      System.out.println("Is legal:" + is_legal_state + " versus "
          + other_state.is_legal_state);
    }
    if (is_safe_state != other_state.is_safe_state) {
      System.out.println("Is safe:" + is_safe_state + " versus "
          + other_state.is_safe_state);
    }
    if (isNotP7 != other_state.isNotP7) {
      System.out.println("isNotP7:" + isNotP7 + " versus "
          + other_state.isNotP7);
    }

    /* Comparison of the states of the machines. */
    for (Entry<M, S> entry : state_machines_current_state.entrySet()) {
      M machine = entry.getKey();
      S state = entry.getValue();
      S state2 = other_state.state_machines_current_state.get(machine);
      if (state2 == null) {
        System.out.println("The machine " + machine.getName()
            + " does not exist in the second model.");
      } else if (!state.id.equals(state2.id)) {
        System.out.println("In " + machine.getName() + ": " + state.id
            + " versus "
            + state2.id);
      }
    }

    for (Entry<M, S> entry : other_state.state_machines_current_state
        .entrySet()) {
      if (state_machines_current_state.get(entry.getKey()) == null) {
        System.out.println("The machine " + entry.getKey().getName()
            + " does not exist in the first model.");
      }
    }

    /* Comparison of the variables. */
    for (Entry<BooleanVariable, Boolean> entry : ((Valuation) variables_values)
        .getSetVariables()) {
      BooleanVariable var = entry.getKey();
      Boolean value = entry.getValue();
      boolean other_value = other_state.getVariableValue(var);
      if (other_value != value) {
        System.out.println(var + ": " + value + " versus " + other_value);
      }
    }

    System.out.println("** End of the comparison");
  }
}
