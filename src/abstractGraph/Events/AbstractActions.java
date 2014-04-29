package abstractGraph.Events;

public abstract class AbstractActions {
  /**
   * Actions are the events raised when firing a transition. They are resolved
   * in the order of their addition.
   * 
   * @param e
   *          An event to add to the list of actions
   */
  public abstract void add(SingleEvent e);
}
