package abstractGraph.events;

import java.util.Iterator;
import java.util.LinkedList;

public class Actions {

  private LinkedList<SingleEvent> events;
  private LinkedList<SingleEvent> alarm_events;

  public Actions() {
    events = new LinkedList<SingleEvent>();
  }

  public void add(SingleEvent e) {
    events.add(e);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Iterator<SingleEvent> iterator = events.iterator();
    while (iterator.hasNext()) {
      SingleEvent single_event = iterator.next();
      sb.append(single_event.toString() + ";");
    }
    if (this.hasAlarm()) {
      sb.append("\n");
      Iterator<SingleEvent> alarm_iterator = alarm_events.iterator();
      while (alarm_iterator.hasNext()) {
        SingleEvent alarm = alarm_iterator.next();
        sb.append(alarm.toString() + ";");
      }
    }
    return sb.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Actions other = (Actions) obj;
    if (events == null) {
      if (other.events != null) {
        return false;
      }
    }

    if (this.events.size() != other.events.size()) {
      return false;
    }

    for (int i = 0; i < events.size(); i++) {
      if (!events.get(i).toString().equals(other.events.get(i).toString())) {
        return false;
      }
    }
    return true;
  }

  public Iterator<SingleEvent> iteratorActions() {
    return events.iterator();
  }

  /**
   * Usually one will call {@link #iteratorAlarms()} after this.
   * 
   * @return true if the transition owns an alarm.
   */
  public boolean hasAlarm() {
    return (alarm_events != null);
  }

  public void addAlarm(SingleEvent e) {
    if (!hasAlarm()) {
      alarm_events = new LinkedList<SingleEvent>();
    }
    alarm_events.add(e);
  }

  /**
   * This function can be called ONLY if hasAlarm() has returned true.
   * 
   * @return An iterator over the actions contained in the alarm field.
   */
  public Iterator<SingleEvent> iteratorAlarms() {
    return alarm_events.iterator();
  }

}