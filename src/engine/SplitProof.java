package engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractModel;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import engine.BuildActivationGraph.MyNode;

public class SplitProof<GS extends AbstractGlobalState<M, S, T, ?>, M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> {

  HashMap<M, MyNode> nodes;
  AbstractModel<M, S, T> model;
  AbstractModel<M, S, T> proof;
  LinkedHashSet<LinkedHashSet<M>> list_of_list_state_machine = new LinkedHashSet<LinkedHashSet<M>>();
  HashSet<M> proof_state_machine_found = new HashSet<M>();

  public SplitProof(HashMap<M, MyNode> nodes,
      AbstractModel<M, S, T> model,
      AbstractModel<M, S, T> proof) {
    this.nodes = nodes;
    this.model = model;
    this.proof = proof;
  }

  public void Split() {
    for (M state_machine : proof) {
      if (!proof_state_machine_found.contains(state_machine)) {
        proof_state_machine_found.add(state_machine);
        LinkedHashSet<M> list_state_machine = new LinkedHashSet<M>();
        list_state_machine = Split(state_machine);
        list_of_list_state_machine.add(list_state_machine);
      }
    }
  }

  public LinkedHashSet<M> Split(M state_machine) {
    LinkedHashSet<M> list_state_machine = new LinkedHashSet<M>();
    LinkedHashSet<M> tmp_list_state_machine = new LinkedHashSet<M>();
    list_state_machine.add(state_machine);
    MyNode head = nodes.get(state_machine);
    for (Entry<M, MyNode> node : nodes.entrySet()) {
      if (node.getValue().equals(head)) {
        tmp_list_state_machine.add(node.getKey());
        if (proof.containsStateMachine(node.getKey())) {
          proof_state_machine_found.add(node.getKey());
        }
      }
    }
    while (!tmp_list_state_machine.isEmpty()) {
      LinkedList<M> tmp_linked_list_state_machine = new LinkedList<M>();
      tmp_linked_list_state_machine.addAll(tmp_list_state_machine);
      tmp_list_state_machine.clear();
      while (!tmp_linked_list_state_machine.isEmpty()) {
        M machine = tmp_linked_list_state_machine.removeFirst();
        for (Entry<M, MyNode> node : nodes.entrySet()) {
          if (node.getValue().equals(machine)) {
            if (!list_state_machine.contains(node.getKey())) {
              tmp_list_state_machine.add(node.getKey());
              if (proof.containsStateMachine(node.getKey())) {
                proof_state_machine_found.add(node.getKey());
              }
            }
          }
        }
      }
      list_state_machine.addAll(tmp_list_state_machine);
    }

    return list_state_machine;
  }
}
