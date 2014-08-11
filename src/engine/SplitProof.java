package engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import abstractGraph.AbstractModel;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import engine.BuildActivationGraph.MyNode;

public class SplitProof<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> {

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

  public LinkedHashSet<LinkedHashSet<M>> Split() {
    for (M state_machine : proof) {
      proof_state_machine_found.add(state_machine);
      LinkedHashSet<M> list_state_machine = new LinkedHashSet<M>();
      list_state_machine = Split(state_machine);
      boolean already_taken = false;
      for (LinkedHashSet<M> machines : list_of_list_state_machine) {
        if (machines.containsAll(list_state_machine)) {
          already_taken = true;
          break;
        } else if (list_state_machine.containsAll(machines)) {
          list_of_list_state_machine.remove(machines);
        }
      }
      if (!already_taken) {
        list_of_list_state_machine.add(list_state_machine);
      }
    }
    return list_of_list_state_machine;
  }

  public LinkedHashSet<M> Split(M state_machine) {
    LinkedHashSet<M> list_state_machine = new LinkedHashSet<M>();
    LinkedList<M> tmp_list_state_machine = new LinkedList<M>();
    tmp_list_state_machine.add(state_machine);
    while (!tmp_list_state_machine.isEmpty()) {
      M machine = tmp_list_state_machine.remove();
      list_state_machine.add(machine);
      MyNode head = nodes.get(machine);
      LinkedHashSet<MyNode> fathers = head.getFathers();

      for (MyNode node : fathers) {
        if (!list_state_machine.contains((M) node.data)) {
          tmp_list_state_machine.add((M) node.data);

        }
      }
    }
    return list_state_machine;
  }
  /*
   * public LinkedHashSet<M> Split(M state_machine) {
   * LinkedHashSet<M> list_state_machine = new LinkedHashSet<M>();
   * LinkedHashSet<M> tmp_list_state_machine = new LinkedHashSet<M>();
   * list_state_machine.add(state_machine);
   * MyNode head = nodes.get(state_machine);
   * int i = 0;
   * for (Entry<M, MyNode> node : nodes.entrySet()) {
   * i++;
   * if (node.getValue().equals(head)) {
   * tmp_list_state_machine.add(node.getKey());
   * System.out.print(i + " *************************"
   * + head.data + " *** " + node.getValue().transitions + " *** "
   * + node.getKey()
   * + "\n");
   * if (proof.containsStateMachine(node.getKey())) {
   * proof_state_machine_found.add(node.getKey());
   * }
   * }
   * }
   * while (!tmp_list_state_machine.isEmpty()) {
   * LinkedList<M> tmp_linked_list_state_machine = new LinkedList<M>();
   * tmp_linked_list_state_machine.addAll(tmp_list_state_machine);
   * tmp_list_state_machine.clear();
   * while (!tmp_linked_list_state_machine.isEmpty()) {
   * M machine = tmp_linked_list_state_machine.removeFirst();
   * for (Entry<M, MyNode> node : nodes.entrySet()) {
   * if (node.getValue().equals(machine)) {
   * if (!list_state_machine.contains(node.getKey())) {
   * tmp_list_state_machine.add(node.getKey());
   * if (proof.containsStateMachine(node.getKey())) {
   * proof_state_machine_found.add(node.getKey());
   * }
   * }
   * }
   * }
   * }
   * 
   * list_state_machine.addAll(tmp_list_state_machine);
   * }
   * 
   * return list_state_machine;
   * }
   */
}
