import graph.GraphFactoryAEFD;
import graph.Model;
import graph.conditions.aefdParser.AEFDFormulaFactory;
import graph.verifiers.Verifier;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.FormulaFactory;

public class Main {

  public static void main(String[] args) throws Exception {

    GraphFactoryAEFD test =
        new GraphFactoryAEFD("ACLE.txt");
    Model model = test.buildModel("Testing model");
    System.out.println(model);
    Verifier default_verifier = Verifier.DEFAULT_VERIFIER;

    default_verifier.check(model);

  }

}
