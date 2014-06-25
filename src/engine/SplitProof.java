package engine;

import genericLabeledGraph.Edge;
import genericLabeledGraph.Node;
import graph.Model;
import graph.State;
import graph.StateMachine;
import graph.Transition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import abstractGraph.conditions.Variable;
import abstractGraph.conditions.cnf.Literal;
import abstractGraph.events.SingleEvent;
import abstractGraph.events.SynchronisationEvent;
import abstractGraph.events.VariableChange;

/**
 * A class that allows the split of the graphs in small groups to optimize the
 * proof.
 */
public class SplitProof {
  private Model model;
  private Model proof;

  /*
   * Store for every StateMachine A, the list of states machines B linked by A
   * -> B
   */
  private LinkedHashMap<StateMachine, LinkedHashSet<StateMachine>> activation_graph =
      new LinkedHashMap<StateMachine, LinkedHashSet<StateMachine>>();
  private HashSet<MyNode> graphs =
      new HashSet<MyNode>();
  private HashMap<StateMachine, MyNode> nodes = new HashMap<StateMachine, MyNode>();

  public SplitProof(Model model, Model proof) {
    this.model = model;
    this.proof = proof;
  }

  /**
   * Create a graph of graphs, where every arc A -> B represents that graph A
   * modifies some variables or generate SYN's that are eaten by graph B.
   */
  public void createGraphOfGraphs() {
    // model
    HashMap<Variable, LinkedList<StateMachine>> writing_state_machine =
        model.getWritingStateMachines();
    @SuppressWarnings("unchecked")
    HashMap<Variable, LinkedList<StateMachine>> writing_state_machine_proof =
        (HashMap<Variable, LinkedList<StateMachine>>) proof
            .getWritingStateMachines()
            .clone();
    /*
     * The graphs of the proof model need to know the graphs that write on the
     * variable
     */
    writing_state_machine_proof.putAll(writing_state_machine);

    /*
     * Store for every SynchronisationEvent (i.e. its name) the set of state
     * machine that have this event in an action field.
     */
    LinkedHashMap<String, LinkedHashSet<StateMachine>> syn_event_in_graphs =
        new LinkedHashMap<String, LinkedHashSet<StateMachine>>();

    /* We first discover which state machine write which SynchronizationEvents. */
    searchForSynchronizationEventWriting(syn_event_in_graphs, this.model);
    searchForSynchronizationEventWriting(syn_event_in_graphs, this.proof);

    /* We then build the Activation Graph */
    buildOutgoingLinksFromModel(writing_state_machine, syn_event_in_graphs,
        this.model);
    buildOutgoingLinksFromModel(writing_state_machine_proof,
        syn_event_in_graphs,
        this.proof);
  }

  /**
   * TODO: add a description for this function.
   * 
   * @param writing_state_machine
   * @param syn_event_in_graphs
   * @param current_model
   */
  private void buildOutgoingLinksFromModel(
      HashMap<Variable, LinkedList<StateMachine>> writing_state_machine,
      LinkedHashMap<String, LinkedHashSet<StateMachine>> syn_event_in_graphs,
      Model current_model) {
    for (StateMachine state_machine : current_model) {

      for (State state : state_machine) {
        for (Transition transition : state) {
          for (SingleEvent event : transition.getEvents()) {
            if (event instanceof VariableChange) {
              addWritingStateMachineInGraph(writing_state_machine,
                  state_machine, (VariableChange) event);
            } else if (event instanceof SynchronisationEvent) {
              for (StateMachine writer : syn_event_in_graphs.get(event
                  .getName())) {
                addInActivationGraph(writer, state_machine, event);

              }
            }
          }
          HashSet<Variable> list_variable = new HashSet<Variable>();
          transition.getCondition().allVariables(list_variable);
          for (Variable variable : list_variable) {
            addWritingStateMachineInGraph(writing_state_machine, state_machine,
                new VariableChange(new Literal(variable)));
          }
        }
      }
    }

  }

  private void addWritingStateMachineInGraph(
      HashMap<Variable, LinkedList<StateMachine>> writing_state_machine,
      StateMachine state_machine,
      VariableChange variable_change) {
    if (writing_state_machine.get(variable_change.getModifiedVariable()).size() > 1) {
      throw new IllegalArgumentException("The variable " + variable_change
          + " is written by more than one graph.");
    }
    addInActivationGraph(writing_state_machine.get(
        variable_change.getModifiedVariable()).getFirst(),
        state_machine, variable_change);
  }

  /**
   * Add in the syn_event_in_graphs structure, the automata that are writing
   * some Synchronization event.
   * 
   * @param syn_event_in_graphs
   *          The association :
   *          (SynchronizationEvent name) -> (state_machines writing it).
   * @param current_model
   *          The model to process.
   */
  private void searchForSynchronizationEventWriting(
      LinkedHashMap<String, LinkedHashSet<StateMachine>> syn_event_in_graphs,
      Model current_model) {
    for (StateMachine state_machine : current_model) {
      nodes.put(state_machine, new MyNode(state_machine));
      for (State state : state_machine) {
        for (Transition transition : state) {
          for (SingleEvent action : transition.getActions()) {
            if (action instanceof SynchronisationEvent) {
              LinkedHashSet<StateMachine> liste_state_machine =
                  syn_event_in_graphs.get(action.getName());
              if (liste_state_machine == null) {
                liste_state_machine = new LinkedHashSet<StateMachine>();
                syn_event_in_graphs.put(action.getName(), liste_state_machine);
              }
              liste_state_machine.add(state_machine);
            }
          }
        }
      }
    }
  }

  private void addInActivationGraph(StateMachine source_state_machine,
      StateMachine destination_state_machine, SingleEvent label) {
    MyNode node = nodes.get(source_state_machine);
    node.add(new MyEdge(node, nodes.get(destination_state_machine), label));
    graphs.add(node);
  }

  class MyNode extends Node<StateMachine, MyNode, SingleEvent> {
    public MyNode(StateMachine data) {
      super(data);
    }
  }

  class MyEdge extends Edge<MyNode, SingleEvent> {
    public MyEdge(MyNode from, MyNode to, SingleEvent label) {
      super(from, to, label);
    }
  }

}
