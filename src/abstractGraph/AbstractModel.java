package abstractGraph;

import java.util.Iterator;

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
   * The order of the elements is not specified.
   * 
   * @return An iterator over all the state machines.
   */
  public abstract Iterator<M> iterator();
}
