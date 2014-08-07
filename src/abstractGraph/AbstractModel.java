package abstractGraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import utils.Pair;
import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.conditions.Formula;
import abstractGraph.events.CommandEvent;
import abstractGraph.events.ComputerCommandFunction;
import abstractGraph.events.ExternalEvent;
import abstractGraph.events.SynchronisationEvent;
import abstractGraph.verifiers.AbstractVerificationUnit;

public abstract class AbstractModel<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>>
    implements Iterable<M> {
  protected String model_name;

  public AbstractModel(String name) {
    model_name = name;
  }

  public String getModelName() {
    return model_name;
  }

  public void setModelName(String name) {
    model_name = name;
  }

  /**
   * Add a state machine to a model. If the state machine is modified, then the
   * model will update itself if needed. By default, the order of the state
   * machine is the order of their addition to the model.
   * 
   * @param state_machine
   *          The state machine to add to the model
   */
  public abstract void addStateMachine(M state_machine);

  /**
   * Search a variable by its name. It creates it if it does not exist.
   */
  public abstract EnumeratedVariable getVariable(String variable_name);

  public abstract Collection<EnumeratedVariable> getExistingVariables();

  /**
   * This function must be called after having added and filled all the state
   * machines.
   * The internal effects are internal implementation specific.
   */
  public void build() {
    FCI_generate_ACT.clear();
  }

  /**
   * The order of the elements is not specified.
   * 
   * @return An iterator over all the state machines.
   */
  public abstract Iterator<M> iterator();

  /**
   * Require to have previously called {@link #build()}.
   * 
   * @return True if the model writes on or listens to the argument event.
   */
  public abstract boolean containsSynchronisationEvent(
      SynchronisationEvent event);

  public abstract boolean containsSynchronousEvent(String synchronous_event);

  public abstract boolean containsVariable(EnumeratedVariable variable);

  public abstract boolean containsExternalEvent(ExternalEvent external_event);

  public abstract boolean containsStateMachine(
      M state_machine);

  /**
   * This function is reserved to specific uses that require to access internal
   * data of the model. In particular, it can be useful to write new
   * {@link AbstractVerificationUnit}.
   * 
   * @return The HashMap linking for every EnumeratedVariable, the list of the
   *         state machines that are modifying its value.
   */
  public abstract HashMap<EnumeratedVariable, Collection<M>> getWritingStateMachines();

  /**
   * Some commands are set to generate external events that will be executed
   * atomically.
   */
  protected HashMap<ComputerCommandFunction, LinkedList<Pair<Formula, LinkedList<ExternalEvent>>>> FCI_generate_ACT = new HashMap<>();

  public LinkedList<Pair<Formula, LinkedList<ExternalEvent>>> getACTFCI(
      CommandEvent fci) {
    LinkedList<Pair<Formula, LinkedList<ExternalEvent>>> result =
        FCI_generate_ACT.get(fci);
    return result;
  }

  public abstract AbstractModel<M, S, T> newInstance();

}
