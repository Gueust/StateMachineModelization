package abstractGraph;

import java.util.Arrays;
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
  protected Object[] state_machines_current_state;

  protected boolean is_legal_state = true, is_safe_state = true;
  protected boolean isNotP7 = true;

  /*
   * The following attributes are used by the model checker to build the tree
   * and display it
   */
  public ExternalEvent last_processed_external_event = null;
  public AbstractGlobalState<M, S, T, ?> previous_global_state = null;
  public LinkedHashSet<Pair<AbstractGlobalState<M, S, T, ?>, ExternalEvent>> children_states;

  public AbstractGlobalState(int number_state_machines, V variables_values) {
    this.variables_values = variables_values;
    state_machines_current_state = new Object[number_state_machines];
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
    state_machines_current_state[machine.getUniqueIdentifier()] = state;
  }

  /**
   * Return the current state of a state machine.
   * 
   * @param machine
   * @return the current state if the state machine exists, null otherwise.
   */
  @SuppressWarnings("unchecked")
  public S getState(M machine) {
    assert (machine != null);
    assert (state_machines_current_state != null);
    return (S) state_machines_current_state[machine.getUniqueIdentifier()];
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

  @SuppressWarnings("unchecked")
  @Override
  /** This should be used only for debug.
   * See {@link GraphSimulatorInterface#globalStateToString(GS)} for a human
   * readable version.
   */
  public String toString() {
    String result = "";

    result += "The values of the states are : ";
    for (int i = 0; i < state_machines_current_state.length; i++) {
      result += ((S) state_machines_current_state[i]).getId();
    }
    result += "\n";

    result = result + "The value of the variables are : "
        + variables_values + ".\n";
    result += "Safe: " + is_safe_state + ", legal: " + is_legal_state
        + ", isNotP7: " + isNotP7 + "\n";
    return result;
  }

  public abstract String toString(Iterable<M> state_machines,
      Iterable<EnumeratedVariable> variables);

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
        Arrays.copyOf(state_machines_current_state,
            state_machines_current_state.length);
    B.variables_values = (V) this.variables_values.clone();
    B.is_legal_state = this.is_legal_state;
    B.is_safe_state = this.is_safe_state;
    B.isNotP7 = this.isNotP7;
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
  @SuppressWarnings("unchecked")
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
    for (int i = 0; i < state_machines_current_state.length; i++) {
      S state = (S) state_machines_current_state[i];
      S state2 = (S) other_state.state_machines_current_state[i];
      if (state2 == null) {
        System.out.println("The machine associated to " + i
            + " does not exist in the second model.");
      } else if (!state.id.equals(state2.id)) {
        System.out.println("In the machine associated to " + i + ": "
            + state.id
            + " versus "
            + state2.id);
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

  /* Automatically generated by Eclipse */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (isNotP7 ? 1231 : 1237);
    result = prime * result + (is_legal_state ? 1231 : 1237);
    result = prime * result + (is_safe_state ? 1231 : 1237);
    result = prime * result + Arrays.hashCode(state_machines_current_state);
    result = prime * result
        + ((variables_values == null) ? 0 : variables_values.hashCode());
    return result;
  }

  /* Automatically generated by Eclipse */
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
    if (!Arrays.equals(state_machines_current_state,
        other.state_machines_current_state))
      return false;
    if (variables_values == null) {
      if (other.variables_values != null)
        return false;
    } else if (!variables_values.equals(other.variables_values))
      return false;
    return true;
  }
}
