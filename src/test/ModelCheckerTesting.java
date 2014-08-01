package test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import abstractGraph.verifiers.SingleWritingChecker;
import abstractGraph.verifiers.Verifier;
import engine.ModelChecker;
import engine.SequentialGraphSimulator;
import engine.SplitProof;
import graph.GlobalState;
import graph.GraphFactoryAEFD;
import graph.Model;
import graph.State;
import graph.StateMachine;
import graph.Transition;
import graph.templates.GeneratorFromTemplate;

public class ModelCheckerTesting {

  private void generalTest(String[] files, int[] results) throws IOException {
    assertTrue(files.length == results.length);

    int i = 0;
    ModelChecker<GlobalState, StateMachine, State, Transition> model_checker;
    for (i = 0; i < files.length; i++) {
      model_checker = new ModelChecker<>();
      SequentialGraphSimulator simulator = generateSimulator(files[i], null);
      simulator.generateAllInitialStates(model_checker);
      System.out.println("Number of generated states: "
          + model_checker.getUnvisited_states().size());
      model_checker.verify(simulator);

      /*
       * for (GlobalState gs : model_checker.getVisited_states()) {
       * System.err.print(gs.toString(simulator.getAll_variables()));
       * }
       */
      int number_visited_states = model_checker.getVisited_states().size();

      assertTrue("Error on " + files[i] +
          " (" + number_visited_states + " found.  " + results[i]
          + " expected).\n",
          model_checker.getVisited_states().size() == results[i]);
    }
  }

  @Test
  public void modelCheckerTesting() throws IOException {
    Verifier<StateMachine, State, Transition> verifier = new Verifier<>();
    verifier.addVerification(
        new SingleWritingChecker<StateMachine, State, Transition>());

    String[] files = {
        "Three_ctl.yaml",
        "Eight_ctl.yaml",
        // "Twelve_ctl.yaml,"
        "graph_with_dependency.yaml"
    };

    int[] results = {
        8,
        256,
        // 1 << 12,
        3
    };

    generalTest(files, results);
  }

  public SequentialGraphSimulator generateSimulator(String model_file,
      String proof_file)
      throws IOException {
    String functional_model = GeneratorFromTemplate
        .load("src/test/resources/" + this.getClass().getSimpleName() + "/"
            + model_file);
    String proof_model;
    Model model;
    SequentialGraphSimulator simulator;
    GraphFactoryAEFD graph_factory = new GraphFactoryAEFD(null);
    model = graph_factory
        .buildModel(functional_model, functional_model);
    model.build();
    if (proof_file != null) {
      proof_model = GeneratorFromTemplate
          .load("src/test/resources/" + this.getClass().getSimpleName() + "/"
              + proof_file);
      Model proof = graph_factory.buildModel(proof_model, proof_model);
      proof.build();
      simulator = new SequentialGraphSimulator(model, proof);
    } else {
      simulator = new SequentialGraphSimulator(model);
    }
    simulator.setVerbose(false);
    return simulator;
  }

  @Test
  public void splitProofTesting() throws IOException {
    String functional_model = GeneratorFromTemplate
        .load("fonctionnel4voie.yaml");
    String proof_model = GeneratorFromTemplate
        .load("preuve4voie.yaml");

    GraphFactoryAEFD graph_factory = new GraphFactoryAEFD(null);

    Model model = graph_factory.buildModel(functional_model, functional_model);
    model.build();
    Model proof = graph_factory.buildModel(proof_model, proof_model);
    proof.build();

    SplitProof<StateMachine, State, Transition> splitter =
        new SplitProof<>(model, proof);
    splitter.printToImage("./activation_graph.");
  }
}
