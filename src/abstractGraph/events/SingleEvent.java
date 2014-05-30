package abstractGraph.events;

public abstract class SingleEvent {

  /* SingleEvents are immutable */
  protected final String name;

  /**
   * Creates a new event using the name `name`.
   * 
   * @param name
   *          The name of the event
   */
  public SingleEvent(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    SingleEvent e2 = (SingleEvent) obj;
    return this.name.equals(e2.name);
  }

  @Override
  public String toString() {
    return name;
  }
}
