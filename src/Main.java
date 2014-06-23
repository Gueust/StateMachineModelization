import engine.GraphSimulator;
import engine.ModelChecker;
import graph.GlobalState;
import graph.GraphFactoryAEFD;
import graph.Model;
import graph.State;
import graph.StateMachine;
import graph.Transition;
import graph.templates.GeneratorFromTemplate;
import graph.verifiers.Verifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import utils.Monitoring;
import utils.TeeOutputStream;
import utils.javaAgent.ObjectSizeFetcher;
import abstractGraph.events.ExternalEvent;

public class Main {

  public static void main(String[] args) throws Exception {

    /* Launching logging */
    launchLogging("verification_tools_logfile.txt");

    DateFormat date_format = DateFormat.getTimeInstance();
    System.out.println("Execution launched at " +
        date_format.format(new Date()) + "\n");

    long startTime = System.nanoTime();

    String functional_model = GeneratorFromTemplate
        .load("fonctionnel2voie.yaml");
    String proof_model = GeneratorFromTemplate
        .load("preuve2voie.yaml");

    launcheModelChecking(functional_model, proof_model);
    long estimatedTime = System.nanoTime() - startTime;
    Monitoring.printFullPeakMemoryUsage();
    System.out.println("Execution took " + estimatedTime / 1000000000.0 + "s");

  }

  private static void verifyModel(Model model) {
    verifyModel(model, true);
  }

  private static void verifyModel(Model model, boolean verbose) {

    Verifier default_verifier = Verifier.DEFAULT_VERIFIER;

    boolean is_ok = !default_verifier.checkAll(model, verbose);
    System.out.println();
    if (is_ok) {
      System.out
          .println("*** FAILURE WHEN TESTING IMPERATIVE PROPERTIES ***\n");
    } else {
      System.out.println("*** IMPERATIVE PROPERTIES VERIFIED ***");
    }
    System.out.println();

    Verifier warning_verifier = Verifier.WARNING_VERIFIER;
    if (!warning_verifier.check(model, verbose)) {
      System.out
          .println("*** Some additionnal properties are not verified ***");
    } else {
      System.out.println("*** All other properties verifier ***");
    }
    System.out.println();
  }

  /**
   * Duplicate the standard output into the given file. If its size is greater
   * than 10Mo, it will empty the file. It will write at the end of the file
   * otherwise.
   * 
   * @param logfile_name
   */
  private static void launchLogging(String logfile_name) {
    File file = new File(logfile_name);
    try {
      /* We make sure than we have no more than 10Mo */
      FileOutputStream fos;
      if (file.length() > 10000000) {
        fos = new FileOutputStream(file);
      } else {
        fos = new FileOutputStream(file, true);
      }

      /* We want to print in the standard "System.out" and in "file" */
      TeeOutputStream myOut = new TeeOutputStream(System.out, fos);
      PrintStream ps = new PrintStream(myOut);
      System.setOut(ps);

      /* Same for System.err */
      TeeOutputStream myErr = new TeeOutputStream(System.err, fos);
      PrintStream ps_err = new PrintStream(myErr);
      System.setErr(ps_err);
    } catch (Exception e) {
      e.printStackTrace();
    }
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

    final GraphSimulator simulator = new GraphSimulator(model, proof);
    final GraphSimulator simulator_without_proof = new GraphSimulator(model);

    simulator.setVerbose(false);
    simulator_without_proof.setVerbose(false);
    verifyModel(model);
    verifyModel(proof);

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

    model_checker.configureInitialGlobalStates(simulator.getAllInitialStates());
    model_checker_without_proof
        .configureInitialGlobalStates(simulator_without_proof
            .getAllInitialStates());
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
    // model.build();
    Model proof = graph_factory.buildModel(proof_model, proof_model);
    proof.build();

    GraphSimulator simulator = new GraphSimulator(model);
    simulator.setVerbose(false);
    verifyModel(model);
    verifyModel(proof);

    ModelChecker<GlobalState, StateMachine, State, Transition> model_checker =
        new ModelChecker<>();

    /* The all CTL true initial state */
    HashMap<String, Boolean> initialization_variables =
        new HashMap<String, Boolean>();
    HashMap<String, String> pairs_of_ctl = model.regroupCTL();
    for (Entry<String, String> pair : pairs_of_ctl.entrySet()) {
      initialization_variables.put(new ExternalEvent(pair.getKey()).getName(),
          true);
    }

    // simulator.init(initialization_variables);
    // GlobalState global_state = simulator.getGlobalState();
    // System.out.println(global_state);
    // System.out.println("Size of a GS: " + (global_state == null)
    // + ObjectSizeFetcher.deepSizeOf(global_state));
    // System.exit(-1);
    // model_checker.configureInitialGlobalStates(simulator.getGlobalState());

    model_checker.configureInitialGlobalStates(simulator.getAllInitialStates());

    GlobalState result = model_checker.verify(simulator);
    if (result == null) {
      System.err.println("Success of the proof");
    } else {
      System.err.println("A state is not safe:\n" + result);
    }
  }
}
