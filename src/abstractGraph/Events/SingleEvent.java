package abstractGraph.Events;

public abstract class SingleEvent {

  protected String prefix;
  protected String name;

  /**
   * Creates a new event using the name `name`. The characters before the first
   * occurence of an underscore (i.e. '_') will be taken at the prefix of the
   * event.
   * 
   * @param name The name of the event
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
}
