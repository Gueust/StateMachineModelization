package graph;

import abstractGraph.AbstractGlobalState;

public class GlobalState extends
    AbstractGlobalState<StateMachine, State, Transition> {

  @Override
  public GlobalState clone() {
    GlobalState result = new GlobalState();
    super.copyTo(this, result);
    return result;
  }

}
