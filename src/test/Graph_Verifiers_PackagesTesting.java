package test;

import static org.junit.Assert.*;

import java.io.IOException;

import graph.GraphFactoryAEFD;
import graph.Model;
import graph.verifiers.SingleWritingChecker;
import graph.verifiers.DeterminismChecker;
import graph.verifiers.NoUselessVariables;
import graph.verifiers.Verifier;

import org.junit.Test;

/**
 * Test all verification units.
 */
public class Graph_Verifiers_PackagesTesting {

  /**
   * Load a model from the resource/$thisClassName/
   * 
   * @param name
   *          The name of the file
   * @return The model build using a GraphFactoryAEFD.
   * @throws IOException
   */
  private Model loadFile(String name) throws IOException {

    String class_name = this.getClass().getSimpleName();

    GraphFactoryAEFD test =
        new GraphFactoryAEFD("src/test/resources/" + class_name + "/" + name);
    Model model = test.buildModel("Testing model");
    return model;
  }

  /**
   * Apply the given verifier to all the given files, expecting that the
   * verifier return the results.
   * 
   * @param verifier
   * @param files
   * @param results
   *          results[i] is the answer for files[i].
   */
  private void generalTest(Verifier verifier, String[] files, Boolean[] results) {
    assertTrue(files.length == results.length);

    try {
      for (int i = 0; i < files.length; i++) {
        assertTrue("Error on " + files[i],
            verifier.check(loadFile(files[i]), true) == results[i]);
        assertTrue("Error on " + files[i],
            verifier.check(loadFile(files[i]), false) == results[i]);
      }
    } catch (IOException e) {
      e.printStackTrace();
      fail("Unexpected exception.");
    }
  }

  /**
   * Testing of {@link SingleWritingChecker}.
   * 
   * @details This test uses different files representing simple graphs:
   *          <ol>
   *          <li>
   *          Graph_with_no_variable.txt is not writing any variable.
   * 
   *          </li>
   *          <li>
   *          Graph_with_concurrent_writing.txt : 3 state machines, 2 variables.
   *          The 2 first states machines write the same variable, and the 3rd
   *          write the second variable.
   * 
   *          </li>
   *          <li>
   *          Graph_without_concurrent_writing.txt : 2 state machines, 2
   *          variables, each of them written by one state machine.
   * 
   *          </li>
   * 
   *          <li>
   *          Graph_with_not_written_variables.txt : Graph with a variable in a
   *          condition field but never written on.</li>
   * 
   *          </ol>
   */
  @Test
  public void SingleWritingChecker() {
    Verifier verifier = new Verifier();
    verifier.addVerification(new SingleWritingChecker());

    String[] files = {
        "Graph_with_no_variable.txt",
        "Graph_without_concurrent_writing.txt",
        "Graph_with_concurrent_writing.txt",
        "Graph_with_not_written_variables.txt"
    };

    Boolean[] results = {
        true,
        true,
        false,
        false
    };

    generalTest(verifier, files, results);
  }

  /**
   * Testing of {@link DeterminismChecker}.
   * 
   * @details This test uses different files representing simple graphs:
   *          <ol>
   *          <li>
   *          Determinism_without_SAT_solving.txt : 1 state machines, 2 states.
   *          2 transitions from 0 to 1, labeled with a different event and
   *          without condition.
   * 
   *          </li>
   *          <li>
   *          Determinism_with_SAT_solving.txt : 2 states, 2 transitions from 0
   *          to 1,labeled with the same event and two simple exlusive
   *          conditions.</li>
   *          <li>
   *          Not_determinist_graph_1.txt : 2 identical transitions labeled with
   *          the same event and without condition.
   * 
   *          </li>
   *          <li>
   *          Not_determinist_graph_2.txt : same as the above, both the first
   *          transition is labeled with several events while the second
   *          transition is labeled only one 1 event (that is also labeling the
   *          first transition).
   * 
   *          </li>
   *          <li>
   *          Not_determinism_with_SAT_solving.txt : 2 transitions labeled with
   *          the same event, and with an incompatible condition.
   *          </li>
   *          <li>
   *          Determinism_two_identical_transitions.txt: two identical
   *          transitions that should not raise an error, but only a warning.
   *          </li>
   *          </ol>
   */
  @Test
  public void DeterminismChecker() {
    Verifier verifier = new Verifier();
    verifier.addVerification(new DeterminismChecker());

    String[] files = {
        "Determinism_without_SAT_solving.txt",
        "Determinism_with_SAT_solving.txt",
        "Not_determinist_graph_1.txt",
        "Not_determinist_graph_2.txt",
        "Not_determinism_with_SAT_solving.txt",
        "Determinism_two_identical_transitions.txt",
        "Determinism_with_identically_labeled_transitions.txt"
    };

    Boolean[] results = {
        true,
        true,
        false,
        false,
        false,
        true,
        true
    };

    generalTest(verifier, files, results);
  }

  /**
   * Testing of {@link NoUselessVariables}.
   * 
   * @details This test uses different files representing simple graphs:
   *          <ol>
   *          <li>
   *          Graph_with_no_variable.txt : Graph_with_no_variable.txt is not
   *          writing or reading any variable.</li>
   * 
   *          <li>
   *          Graph_without_useless_variables.txt : Graph writing and reading
   *          variables without any usless one.</li>
   * 
   *          <li>
   *          Graph_with_not_used_variables.txt :Graph with a variable in an
   *          action field but never used in a condition.</li>
   * 
   */
  @Test
  public void NoUselessVariablesChecker() {
    Verifier verifier = new Verifier();
    verifier.addVerification(new NoUselessVariables());

    String[] files = {
        "Graph_with_no_variable.txt",
        "Graph_without_useless_variables.txt",
        "Graph_with_not_used_variables.txt",

    };

    Boolean[] results = {
        true,
        true,
        false
    };

    generalTest(verifier, files, results);
  }
}
