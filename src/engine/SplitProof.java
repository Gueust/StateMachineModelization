package engine;

import graph.Model;
import graph.State;
import graph.StateMachine;
import graph.Transition;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import abstractGraph.conditions.Variable;
import abstractGraph.events.SingleEvent;
import abstractGraph.events.SynchronisationEvent;
import abstractGraph.events.VariableChange;

/**
 * A class that allow the split of the graphs in small group to optimize the
 * proof
 */
public class SplitProof {
  private Model model;
  private Model proof;
  private LinkedHashMap<StateMachine, LinkedHashSet<StateMachine>> graph_of_graph =
      new LinkedHashMap<StateMachine, LinkedHashSet<StateMachine>>();
  private LinkedHashMap<SynchronisationEvent, LinkedHashSet<StateMachine>> syn_event_in_graphs =
      new LinkedHashMap<SynchronisationEvent, LinkedHashSet<StateMachine>>();

  public SplitProof(Model model, Model proof) {
    this.model = model;
    this.proof = proof;
  }

  /**
   * Create a graph of graph, where every arc represent that the first graph
   * modify some variables or generate SYN's that are eaten by the second graph.
   */
  public void createGraphOfGraphs() {
    HashMap<Variable, LinkedList<StateMachine>> writing_state_machine =
        model.getWritingStateMachines();

    for (StateMachine state_machine : model) {
      for (State state : state_machine) {
        for (Transition transition : state) {
          for (SingleEvent action : transition.getActions()) {
            if (action.getName().startsWith("SYN_")
                || action.getName().startsWith("ACT_")) {
              LinkedHashSet<StateMachine> liste_state_machine = syn_event_in_graphs
                  .get(action);
              if (liste_state_machine == null) {
                liste_state_machine = new LinkedHashSet<StateMachine>();
              }
              liste_state_machine.add(state_machine);
            }
          }
        }
      }
    }
    for (StateMachine state_machine : model) {
      for (State state : state_machine) {
        for (Transition transition : state) {
          for (SingleEvent event : transition.getEvents()) {
            if (event instanceof VariableChange) {
              if (writing_state_machine.get(event).size() > 1) {
                throw new IllegalArgumentException("The variable " + event
                    + " is written by more than one graph.");
              }
              addInGraphOfGraph(writing_state_machine.get(event).getFirst(),
                  state_machine);
            } else if (event instanceof SynchronisationEvent) {
              for (StateMachine writer : syn_event_in_graphs.get(event)) {
                addInGraphOfGraph(writer, state_machine);
              }
            }
          }
        }
      }
    }
  }

  private void addInGraphOfGraph(StateMachine source_state_machine,
      StateMachine destination_state_machine) {
    LinkedHashSet<StateMachine> arc = graph_of_graph.get(source_state_machine);
    if (arc == null) {
      arc = new LinkedHashSet<StateMachine>();
    }
    arc.add(destination_state_machine);
  }
}
