package abstractGraph.events;

public abstract class SingleEvent {

  protected String prefix;
  protected String name;

  /**
   * Creates a new event using the name `name`. The characters before the first
   * occurence of an underscore (i.e. '_') will be taken at the prefix of the
   * event.
   * 
   * @param name
   *          The name of the event
   */
  public SingleEvent(String name) {
    this.name = name;
    this.prefix = name.substring(0, name.indexOf('_'));
  }

  public String getPrefix() {
    return prefix;
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
