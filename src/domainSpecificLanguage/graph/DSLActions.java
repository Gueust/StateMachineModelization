package domainSpecificLanguage.graph;

import utils.GenericToString;
import abstractGraph.conditions.CustomToString;
import abstractGraph.events.Actions;

public class DSLActions extends Actions {

  @Override
  public String toString(CustomToString customizer) {
    return GenericToString.printCollection(events);
  }

}
