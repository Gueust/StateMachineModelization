import engine.ModelChecker;
import engine.SequentialGraphSimulator;
import graph.GlobalState;
import graph.GraphFactoryAEFD;
import graph.Model;
import graph.State;
import graph.StateMachine;
import graph.Transition;
import graph.conditions.aefdParser.GenerateFormulaAEFD;
import graph.verifiers.Verifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import utils.Logging;
import utils.Monitoring;
import abstractGraph.events.ExternalEvent;

/*
 * Ce fichier permet de lancer l'exploration sur un fonctionnel + preuve à
 * partir des fichiers 6 lignes correspondant.
 * Les résultats sont écrit à la fois dans le fichier :
 * verification_tools_logfile.txt
 * mais aussi directement sur la console.
 *
 */

public class LaunchProofFromAEFDFormat {

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
    // launchModelChecking("examples/PN à SAL.txt", null);
    // launchModelChecking("examples/PN à SAL.txt",
    // "examples/PN à SAL Preuve.txt");
    // launchModelChecking("examples/PN à SAL+TPL.txt",
    // "examples/PN à SAL+TPL Preuve.txt");
    // launchModelChecking("examples/PN a SAL Cas2.txt",

    // launchModelChecking("compteur essieux.txt",
    // "compteur essieux preuve.txt");
    // launchModelChecking("PN/PN a SAL Cas2.txt",
    // "PN/PN a SAL Cas2 Preuve.txt");
    // launchModelChecking("PN/PN a SAL Cas2.txt",
    // null);
    // launchModelChecking("PN/PN a SAL Cas3.txt",
    // "PN/PN a SAL Cas3 Preuve.txt");

    // launchModelChecking("../Compteur essieu/CompteurEssieux.txt", null);
    // Cette partie du code permet de lancer Noisy
    /*
     * GraphFactoryAEFD factory = new GraphFactoryAEFD();
     * GraphFactoryAEFD graph_factory = new GraphFactoryAEFD();
     * 
     * CTLReplacer ctl_replacer = new CTLReplacer("Noisy/Noisy_init.txt",
     * "Noisy/Noisy_corrected.txt", false);
     * 
     * Model model = graph_factory
     * .buildModel("Noisy/Noisy_corrected.txt", "Noisy/Noisy_corrected.txt");
     * model.build();
     * System.out.print("*** nombre de CTL " + model.regroupCTL().size() +
     * "\n ");
     */
    // Cette partie du code permet de lancer Nurieux

    launchNurieuxWithRestrainedEventList("Nurieux_corrected.txt",
        "Preuve_3423_3431_without_CTL.txt",
        // "Nurieux/Liste_evenement_externe.txt",
        "Nurieux/Liste_evenement_externe3423_3431.txt",
        "Nurieux/liste_FCI.yaml");

    long estimatedTime = System.nanoTime() - startTime;
    Monitoring.printFullPeakMemoryUsage();
    System.out.println("Execution took " + estimatedTime / 1000000000.0 + "s");
  }

  public static void launchModelCheckingWithProofTesting(
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
        new SequentialGraphSimulator(model);

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

  public static Set<GlobalState> launchModelChecking(

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

    GlobalState global_state = simulator.init(initialization_variables);
    System.out.println(global_state);
    // System.out.println("Size of a GS: " + (global_state == null)
    // + ObjectSizeFetcher.deepSizeOf(global_state));
    // System.exit(-1);
    model_checker.addInitialState(global_state);

    // simulator.generateAllInitialStates(model_checker);

    GlobalState result = model_checker.verify(simulator, true);

    model_checker.displayTree();

    if (result == null) {
      System.err.println("Success of the proof");
    } else {
      System.err.println("A state is not safe:\n" + result);
    }
    return model_checker.getVisited_states();

  }

  private static void launchNurieuxWithRestrainedEventList(
      String functional_model,
      String proof_model,
      String restrained_event_file,
      String FCI_file) throws IOException {
    GraphFactoryAEFD graph_factory = new GraphFactoryAEFD();

    Model model = graph_factory
        .buildModel(functional_model, functional_model);
    model.build();
    model.loadFCI(FCI_file);
    Model proof = graph_factory.buildModel(proof_model, proof_model);
    proof.build();

    BufferedReader reader = new BufferedReader(new FileReader(
        restrained_event_file));
    LinkedList<String> external_event_list_string = new LinkedList<String>();
    HashMap<String, String> CTL_list = new HashMap<String, String>();
    String event_read = reader.readLine();
    while (event_read != null) {
      external_event_list_string.add(event_read.trim());
      if (event_read.trim().startsWith("CTL_")) {
        String ctl_opposite_name = GenerateFormulaAEFD
            .getOppositeName(event_read.trim());
        external_event_list_string.add(ctl_opposite_name);
        if (GenerateFormulaAEFD.isNegative(event_read)) {
          CTL_list.put(ctl_opposite_name, event_read.trim());
        } else if (GenerateFormulaAEFD.isPositive(event_read)) {
          CTL_list.put(event_read, ctl_opposite_name);
        } else {
          throw new Error("The CTL " + event_read
              + " doesn't have a correct suffixe");
        }
      }
      event_read = reader.readLine();
    }
    reader.close();

    LinkedList<ExternalEvent> external_event_list = new LinkedList<ExternalEvent>();
    HashMap<String, String> all_CTL_list = model.regroupCTL();
    HashMap<String, Boolean> restrained_ctl_value_list = new HashMap<String, Boolean>();
    HashMap<String, Boolean> all_ctl_value_list = new HashMap<String, Boolean>();
    Iterator<ExternalEvent> external_event_iterator = model
        .iteratorExternalEvents();
    while (external_event_iterator.hasNext()) {
      ExternalEvent external_event = external_event_iterator.next();
      if (external_event_list_string.contains(external_event.getName())) {
        external_event_list.add(external_event);
      }
    }
    for (String ctl_name : all_CTL_list.keySet()) {
      if (ctl_name.contains("KTS") || ctl_name.contains("Zone")
          || ctl_name.contains("KLMG") || ctl_name.contains("TINT")) {
        restrained_ctl_value_list.put(ctl_name, true);
        all_ctl_value_list.put(ctl_name, true);
      } else {
        restrained_ctl_value_list.put(ctl_name, false);
        all_ctl_value_list.put(ctl_name, false);
      }
    }
    SequentialGraphSimulator simulator =
        new SequentialGraphSimulator(model, proof);
    simulator.setRestrainedExternalEventList(external_event_list);
    simulator.setVerbose(false);

    /* Read a sequence of event to initialize the model with */
    reader = new BufferedReader(new FileReader(
        "Nurieux/fichier_initialisation_3423_3431.txt"));
    LinkedList<ExternalEvent> initialization_event = new LinkedList<ExternalEvent>();
    event_read = reader.readLine();
    while (event_read != null) {
      initialization_event.add(new ExternalEvent(event_read.trim()));
      event_read = reader.readLine();
    }
    reader.close();

    GlobalState global_state_list = simulator.init(all_ctl_value_list);
    global_state_list = simulator.executeAll(global_state_list,
        initialization_event);

    // GlobalState global_state_list =
    // simulator.init(restrained_ctl_value_list);

    ModelChecker<GlobalState, StateMachine, State, Transition> model_checker = new ModelChecker<GlobalState, StateMachine, State, Transition>();
    // simulator.generateAllInitialStates(CTL_list, restrained_ctl_value_list,
    // model_checker);
    model_checker.addInitialState(global_state_list);
    model_checker.verify(simulator);
    // System.out.print(model_checker.getVisited_states());

  }

}
