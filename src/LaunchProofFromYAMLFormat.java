import engine.ModelChecker;
import engine.SequentialGraphSimulator;
import graph.GlobalState;
import graph.GraphFactoryAEFD;
import graph.Model;
import graph.State;
import graph.StateMachine;
import graph.Transition;
import graph.templates.GeneratorFromTemplate;
import graph.verifiers.Verifier;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import utils.Logging;
import utils.Monitoring;
import abstractGraph.events.ExternalEvent;

/**
 * Ce fichier permet de lancer l'exploration sur un fonctionnel + preuve à
 * partir des fichiers YAML correspondant. Ces fichiers permettent la génération
 * de fichiers 6 lignes.
 * Cet outil étant uniquement utilisés par ceux qui l'ont développé pour gagner
 * du temps, il n'existe actuellement pas de document expliquant la syntaxe.
 * Se référer aux exemples est néanmoins suffisant.
 * 
 */
public class LaunchProofFromYAMLFormat {

  public static void main(String[] args) throws Exception {

    /* Launching logging */
    Logging.launchLogging("verification_tools_logfile.txt");

    DateFormat date_format = DateFormat.getTimeInstance();
    System.out.println("Execution launched at " +
        date_format.format(new Date()) + "\n");

    long startTime = System.nanoTime();

    String functional_model = GeneratorFromTemplate
        .load("PN/PN_JB_1_voie.yaml");
    String proof_model = GeneratorFromTemplate
        .load("PN/PN_JB_1_voie_preuve.yaml");

    launcheModelChecking(functional_model, proof_model);

    long estimatedTime = System.nanoTime() - startTime;
    Monitoring.printFullPeakMemoryUsage();
    System.out.println("Execution took " + estimatedTime / 1000000000.0 + "s");
  }

  public static void launcheModelCheckingWithProofTesting(
      String functional_model,
      String proof_model) throws IOException, InterruptedException {
    GraphFactoryAEFD graph_factory = new GraphFactoryAEFD();

    Model model = graph_factory
        .buildModel(functional_model, functional_model);
    model.build();
    Model proof = graph_factory.buildModel(proof_model, proof_model);
    proof.build();

    final SequentialGraphSimulator simulator =
        new SequentialGraphSimulator(model, proof);
    final SequentialGraphSimulator simulator_without_proof =
        new SequentialGraphSimulator(model, proof);

    simulator.setVerbose(false);
    simulator_without_proof.setVerbose(false);
    Verifier.verifyModel(model);
    Verifier.verifyModel(proof);

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

    simulator.generateAllInitialStates(model_checker);
    simulator_without_proof
        .generateAllInitialStates(model_checker_without_proof);

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

  public static void launcheModelChecking(
      String functional_model,
      String proof_model) throws IOException {

    GraphFactoryAEFD graph_factory = new GraphFactoryAEFD();

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
    Verifier.verifyModel(model);
    Verifier.verifyModel(proof);

    ModelChecker<GlobalState, StateMachine, State, Transition> model_checker =
        new ModelChecker<>();

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

    GlobalState global_state = simulator.init(initialization_variables);
    System.out.println(global_state);
    // System.out.println("Size of a GS: " + (global_state == null)
    // + ObjectSizeFetcher.deepSizeOf(global_state));
    // System.exit(-1);
    model_checker.addInitialState(global_state);

    // simulator.generateAllInitialStates(model_checker);

    GlobalState result = model_checker.verify(simulator);

    if (result == null) {
      System.err.println("Success of the proof");
    } else {
      System.err.println("A state is not safe:\n" + result);
    }
  }

}
