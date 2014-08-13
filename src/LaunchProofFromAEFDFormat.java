import engine.ModelChecker;
import engine.SequentialGraphSimulator;
import engine.traceTree.ModelCheckerDisplayer;
import graph.GlobalState;
import graph.GraphFactoryAEFD;
import graph.Model;
import graph.State;
import graph.StateMachine;
import graph.Transition;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import utils.Logging;
import utils.Monitoring;
import abstractGraph.events.ExternalEvent;
import abstractGraph.verifiers.Verifier;

/*
 * Ce fichier permet de lancer l'exploration sur un fonctionnel + preuve à
 * partir des fichiers 6 lignes correspondant.
 * Les résultats sont écrit à la fois dans le fichier :
 * verification_tools_logfile.txt
 * mais aussi directement sur la console.
 *
 */

public class LaunchProofFromAEFDFormat {

  /** Display the execution trace */
  private static final boolean DISPLAY_TREE = false;

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
    // launchModelChecking("examples/PN à SAL.txt", null ,
    // "examples/init_file.txt");
    // launchModelChecking("examples/PN à SAL.txt",
    // "examples/PN à SAL Preuve.txt", "examples/init_file.txt");
    // launchModelChecking("examples/PN à SAL+TPL.txt",
    // "examples/PN à SAL+TPL Preuve.txt", "examples/init_file.txt");

    launchModelChecking("examples/PN à SAL Cas3.txt",
        "examples/PN à SAL Cas3 Preuve.txt", "examples/init_file.txt");

    long estimatedTime = System.nanoTime() - startTime;
    Monitoring.printFullPeakMemoryUsage();
    System.out.println("Execution took " + estimatedTime / 1000000000.0 + "s");
  }

  public static void launchModelCheckingWithProofTesting(
      String functional_model,
      String proof_model,
      String init_file) throws IOException, InterruptedException {
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

    final ModelChecker<GlobalState, StateMachine, State, Transition> model_checker =
        new ModelChecker<>();
    final ModelChecker<GlobalState, StateMachine, State, Transition> model_checker_without_proof =
        new ModelChecker<>();

    /* The all CTL true initial state */
    HashMap<String, Boolean> initialization_variables =
        new HashMap<String, Boolean>();
    HashMap<String, String> pairs_of_ctl = model.regroupCTL();
    for (Entry<String, String> pair : pairs_of_ctl.entrySet()) {
      initialization_variables.put(new ExternalEvent(pair.getKey()).getName(),
          true);
    }

    simulator.generateAllInitialStates(model_checker, init_file);
    simulator_without_proof
        .generateAllInitialStates(model_checker_without_proof, init_file);

    // simulator.init(initialization_variables);
    // model_checker.configureInitialGlobalStates(simulator.getGlobalState());

    final GlobalState[] result_with_proof = new GlobalState[1];
    final GlobalState[] result_without_proof = new GlobalState[1];

    Thread model_checker_thread = new Thread(new Runnable() {

      @Override
      public void run() {
        GlobalState result = model_checker.verify(simulator);
        if (result == null) {
          System.err.println("Success of the proof");
        } else {
          System.err.println("A state is not safe:\n" + result);
        }
        assert result_with_proof != null;
        result_with_proof[0] = result;
      }
    });

    Thread model_checker_thread_without_proof = new Thread(new Runnable() {

      @Override
      public void run() {

        GlobalState result = model_checker_without_proof
            .verify(simulator_without_proof);

        assert result_without_proof != null;
        result_without_proof[0] = result;

      }
    });

    model_checker_thread.start();
    model_checker_thread_without_proof.start();

    model_checker_thread.join();
    model_checker_thread_without_proof.join();

    if (result_without_proof[0] != null) {
      System.err.println("Error with the model checker without proof");
    }

    if (model_checker.getVisited_states().size() != model_checker_without_proof
        .getVisited_states()
        .size()) {
      System.err
          .println("Error: the execution with a proof and without a proof do not explore the same number of states");
    }
  }

  public static Set<GlobalState> launchModelChecking(
      String functional_model,
      String proof_model,
      String init_file) throws IOException {

    GraphFactoryAEFD graph_factory = new GraphFactoryAEFD(null);

    Model model = graph_factory
        .buildModel(functional_model, functional_model);
    model.build();

    Model proof;
    if (proof_model != null) {
      proof = graph_factory.buildModel(proof_model, proof_model);
      proof.build();
    } else {
      proof = null;
    }

    SequentialGraphSimulator simulator =
        new SequentialGraphSimulator(model, proof);
    simulator.setVerbose(false);

    Verifier<StateMachine, State, Transition> verifier = new Verifier<>();
    verifier.verifyModel(model);
    verifier.verifyModel(proof);

    ModelChecker<GlobalState, StateMachine, State, Transition> model_checker;
    if (DISPLAY_TREE) {
      model_checker = new ModelCheckerDisplayer<>();
    } else {
      model_checker =
          new ModelChecker<>();
    }
    // model_checker.setDiskBackUpMemory();

    /* The all CTL true initial state */
    HashMap<String, Boolean> initialization_variables =
        new HashMap<String, Boolean>();
    HashMap<String, String> pairs_of_ctl = model.regroupCTL();
    for (Entry<String, String> pair : pairs_of_ctl.entrySet()) {
      String name = pair.getKey();
      boolean value = true;
      if (name.equals("CTL_Pd_Entree_voie_AP_Actif") ||
          name.equals("CTL_Pd_Entree_voie_AI_Actif") ||
          name.startsWith("CTL_PdAn")) {
        value = false;
      }
      initialization_variables.put(
          name,
          value);
    }

    GlobalState global_state = simulator.init(initialization_variables,
        init_file);
    System.out.println(global_state);
    model_checker.addInitialState(global_state);

    // simulator.generateAllInitialStates(model_checker);

    assert (simulator != null);

    GlobalState result = model_checker.verify(simulator);

    if (result == null) {
      System.err.println("Success of the proof");
    } else {
      System.err.println("A state is not safe:\n" + result);
    }
    return model_checker.getVisited_states();
  }

}
