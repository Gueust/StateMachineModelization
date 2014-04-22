import Graph.Events.SynchronisationEvent;


public class Main {

  public static void main(String[] args) {

    SynchronisationEvent syn = new SynchronisationEvent("SYN_AUTO_3");
    System.out.println("Event: " + syn.getPrefix());

  }

}
