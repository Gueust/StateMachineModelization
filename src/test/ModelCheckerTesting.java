package test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import engine.GraphSimulator;
import engine.ModelChecker;
import graph.GlobalState;
import graph.GraphFactoryAEFD;
import graph.Model;
import graph.State;
import graph.StateMachine;
import graph.Transition;
import graph.templates.GeneratorFromTemplate;
import graph.verifiers.SingleWritingChecker;
import graph.verifiers.Verifier;

public class ModelCheckerTesting {

  private void generalTest(String[] files, int[] results) throws IOException {
    assertTrue(files.length == results.length);

    int i = 0;
    ModelChecker<GlobalState, StateMachine, State, Transition> model_checker;
    for (i = 0; i < files.length; i++) {
      model_checker = new ModelChecker<>();
      GraphSimulator simulator = generateSimulator(files[i], null);
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
    Verifier verifier = new Verifier();
    verifier.addVerification(new SingleWritingChecker());

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

  public GraphSimulator generateSimulator(String model_file, String proof_file)
      throws IOException {
    String functional_model = GeneratorFromTemplate
        .load("src/test/resources/" + this.getClass().getSimpleName() + "/"
            + model_file);
    String proof_model;
    Model model;
    GraphSimulator simulator;
    GraphFactoryAEFD graph_factory = new GraphFactoryAEFD();
    model = graph_factory
        .buildModel(functional_model, functional_model);
    model.build();
    if (proof_file != null) {
      proof_model = GeneratorFromTemplate
          .load("src/test/resources/" + this.getClass().getSimpleName() + "/"
              + proof_file);
      Model proof = graph_factory.buildModel(proof_model, proof_model);
      proof.build();
      simulator = new GraphSimulator(model, proof);
    } else {
      simulator = new GraphSimulator(model);
    }
    simulator.setVerbose(false);
    return simulator;

  }

}
