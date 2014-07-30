import domainSpecificLanguage.DSLGlobalState.DSLGlobalState;
import domainSpecificLanguage.engine.DSLGraphSimulator;
import domainSpecificLanguage.graph.DSLModel;
import domainSpecificLanguage.graph.DSLState;
import domainSpecificLanguage.graph.DSLStateMachine;
import domainSpecificLanguage.graph.DSLTransition;
import domainSpecificLanguage.parser.FSM_builder;
import domainSpecificLanguage.verifiers.DSLVerifier;
import engine.ModelChecker;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import utils.Logging;
import utils.Monitoring;
import utils.Pair;
import abstractGraph.verifiers.Verifier;

/*
 * Ce fichier permet de lancer l'exploration sur un fonctionnel + preuve à
 * partir d'un fichier écrit en DSL
 * Les résultats sont écrit à la fois dans le fichier :
 * verification_tools_logfile.txt
 * mais aussi directement sur la console.
 */

public class LaunchProofFromDSLFormat {

  public static void main(String[] args) throws Exception {

    /* Launching logging */
    Logging.launchLogging("verification_tools_logfile.txt");

    DateFormat date_format = DateFormat.getTimeInstance();
    System.out.println("Execution launched at " +
        date_format.format(new Date()) + "\n");

    long startTime = System.nanoTime();

    /*
     * Il suffit de commenter/décommenter les lignes afin de lancer le fichier
     * souhaité
     */
    // launchModelChecking("src/domainSpecificLanguage/Example.txt");
    launchModelChecking("examples/DSL/PN à SAL.txt");

    long estimatedTime = System.nanoTime() - startTime;
    Monitoring.printFullPeakMemoryUsage();
    System.out.println("Execution took " + estimatedTime / 1000000000.0 + "s");
  }

  public static void launchModelChecking(
      String dsl_model_file_name) throws IOException, InterruptedException {

    FSM_builder builder = new FSM_builder();
    Pair<DSLModel, DSLModel> pair = builder.parseFile(dsl_model_file_name);

    DSLModel model = pair.first;
    DSLModel proof = pair.second;

    final DSLGraphSimulator<DSLGlobalState> simulator =
        new DSLGraphSimulator<>(model, proof);

    simulator.setVerbose(false);

    DSLVerifier verifier = new DSLVerifier();
    verifier.verifyModel(model);
    verifier.verifyModel(proof);

    final ModelChecker<DSLGlobalState, DSLStateMachine, DSLState, DSLTransition> model_checker =
        new ModelChecker<>();

    DSLGlobalState global_state = simulator.getInitialGlobalState();
    System.out.println(global_state);
    model_checker.addInitialState(global_state);

    DSLGlobalState result = model_checker.verify(simulator);

    if (result == null) {
      System.err.println("Success of the proof");
    } else {
      System.err.println("A state is not safe:\n"
          + simulator.globalStateToString(result));
    }
  }
}
