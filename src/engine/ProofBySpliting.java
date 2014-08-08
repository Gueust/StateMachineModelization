package engine;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractModel;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import domainSpecificLanguage.engine.DSLSequentialGraphSimulator;
import domainSpecificLanguage.graph.DSLModel;
import engine.BuildActivationGraph.MyNode;
import graph.Model;

public class ProofBySpliting<GS extends AbstractGlobalState<M, S, T, ?>, M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> {

  HashSet<GraphSimulatorInterface<GS, M, S, T>> simulators = new HashSet<GraphSimulatorInterface<GS, M, S, T>>();

  public ProofBySpliting(AbstractModel<M, S, T> model,
      AbstractModel<M, S, T> proof)
      throws IOException, InstantiationException, IllegalAccessException {

    BuildActivationGraph<M, S, T> activation_graph_builder = new BuildActivationGraph<M, S, T>(
        model, proof);

    HashMap<M, MyNode> nodes = new HashMap<M, MyNode>();
    nodes.putAll(activation_graph_builder.nodes);
    SplitProof<M, S, T> split_proof = new SplitProof<M, S, T>(nodes, model,
        proof);
    LinkedHashSet<LinkedHashSet<M>> list_of_list_state_machine = split_proof
        .Split();

    for (LinkedHashSet<M> liste_state_machine : list_of_list_state_machine) {
      simulators.add(prove(liste_state_machine, model, proof));
    }
  }

  @SuppressWarnings("unchecked")
  public GraphSimulatorInterface<GS, M, S, T> prove(
      LinkedHashSet<M> liste_state_machine,
      AbstractModel<M, S, T> model,
      AbstractModel<M, S, T> proof) throws InstantiationException,
      IllegalAccessException {
    AbstractModel<M, S, T> sub_model = model.getClass().newInstance();
    AbstractModel<M, S, T> sub_proof = model.getClass().newInstance();
    for (M state_machine : liste_state_machine) {
      if (model.containsStateMachine(state_machine)) {
        sub_model.addStateMachine(state_machine);
      } else if (proof.containsStateMachine(state_machine)) {
        sub_proof.addStateMachine(state_machine);
      } else {
        throw new Error("The state machine " + state_machine
            + " can't be found in neither the proof nor the functional model.");
      }
    }
    sub_model.build();
    sub_proof.build();
    if (model instanceof Model) {
      SequentialGraphSimulator tmp = new SequentialGraphSimulator(
          (Model) sub_model, (Model) sub_proof);
      tmp.setVerbose(false);
      return (GraphSimulatorInterface<GS, M, S, T>) tmp;
    } else if (model instanceof DSLModel) {
      return (GraphSimulatorInterface<GS, M, S, T>) new DSLSequentialGraphSimulator<>(
          (DSLModel) sub_model, (DSLModel) sub_proof);
    }
    throw new Error();
  }

  public HashSet<GraphSimulatorInterface<GS, M, S, T>> getSimulators() {
    return simulators;
  }

}
