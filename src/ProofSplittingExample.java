import java.io.IOException;

import domainSpecificLanguage.DSLGlobalState.DSLGlobalState;
import domainSpecificLanguage.engine.DSLGraphSimulator;
import domainSpecificLanguage.graph.DSLModel;
import domainSpecificLanguage.graph.DSLState;
import domainSpecificLanguage.graph.DSLStateMachine;
import domainSpecificLanguage.graph.DSLTransition;
import domainSpecificLanguage.parser.FSM_builder;
import domainSpecificLanguage.verifiers.DSLVerifier;
import engine.ModelChecker;
import engine.SplittingModelChecker;

public class ProofSplittingExample {

  public static void main(String[] args) throws IOException {
    String example_file = "examples/Decoupe/Example_decoupe.txt";

    FSM_builder builder = new FSM_builder();

    builder.parseFile(example_file);
    DSLModel functionnal_model = builder.getModel();
    DSLModel proof_model = builder.getProof();
    System.out.println(functionnal_model);
    System.out.println(proof_model.toString(true));

    final DSLGraphSimulator<DSLGlobalState> simulator =
        new DSLGraphSimulator<>(functionnal_model, proof_model);

    simulator.setVerbose(false);

    DSLVerifier verifier = new DSLVerifier();
    verifier.verifyModel(functionnal_model);
    verifier.verifyModel(proof_model);

    SplittingModelChecker<DSLGlobalState, DSLStateMachine, DSLState, DSLTransition> splitting_model_checker =
        new SplittingModelChecker<>();

    DSLGlobalState global_state = simulator.getInitialGlobalState();

    splitting_model_checker.addInitialState(global_state);

    DSLGlobalState result = splitting_model_checker.verify(simulator);

  }
}
