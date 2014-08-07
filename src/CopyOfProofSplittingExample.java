import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import abstractGraph.events.ExternalEvent;
import abstractGraph.verifiers.Verifier;
import domainSpecificLanguage.DSLGlobalState.DSLGlobalState;
import domainSpecificLanguage.engine.DSLGraphSimulator;
import domainSpecificLanguage.graph.DSLModel;
import domainSpecificLanguage.graph.DSLState;
import domainSpecificLanguage.graph.DSLStateMachine;
import domainSpecificLanguage.graph.DSLTransition;
import domainSpecificLanguage.parser.FSM_builder;
import domainSpecificLanguage.verifiers.DSLVerifier;
import engine.ModelChecker;
import engine.SequentialGraphSimulator;
import engine.SplittingModelChecker;
import graph.GlobalState;
import graph.GraphFactoryAEFD;
import graph.Model;
import graph.State;
import graph.StateMachine;
import graph.Transition;

public class CopyOfProofSplittingExample {

  public static void main(String[] args) throws IOException {

    String functional_model = "examples/PN à SAL.txt";
    String proof_model = "examples/PN à SAL Preuve.txt";

    GraphFactoryAEFD graph_factory = new GraphFactoryAEFD(null);

    Model model = graph_factory
        .buildModel(functional_model, functional_model);
    model.build();
    Model proof = graph_factory.buildModel(proof_model, proof_model);
    proof.build();

    final SequentialGraphSimulator simulator =
        new SequentialGraphSimulator(model, proof);
    final SequentialGraphSimulator simulator_without_proof =
        new SequentialGraphSimulator(model);

    simulator.setVerbose(false);
    simulator_without_proof.setVerbose(false);

    Verifier<StateMachine, State, Transition> verifier = new Verifier<>();
    verifier.verifyModel(model);
    verifier.verifyModel(proof);

    SplittingModelChecker<GlobalState, StateMachine, State, Transition> splitting_model_checker =
        new SplittingModelChecker<>();

    GlobalState result = splitting_model_checker.verify(simulator);

  }
}
