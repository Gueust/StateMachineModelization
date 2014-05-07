import graph.GraphFactoryAEFD;
import graph.Model;
import graph.conditions.aefdParser.AEFDFormulaFactory;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.FormulaFactory;

public class Main {

  public static void main(String[] args) throws Exception {

    GraphFactoryAEFD test =
        new GraphFactoryAEFD("ACLE.txt");
    Model model = test.buildModel("Testing model");
    System.out.println(model);

    String condition = "x1 OU (x2 ET x3)";
    Formula.DEFAULT_FACTORY.parse(condition);
    condition = "x1 OU x2 ET x3";
    Formula.DEFAULT_FACTORY.parse(condition);
    condition = "x1 ET x2 OU x3";
    Formula.DEFAULT_FACTORY.parse(condition);
    condition = "not x1 ET x2 OU x3 ET NOT X4";
    Formula.DEFAULT_FACTORY.parse(condition);

    // Test the AEFD parser
    FormulaFactory aefd_factory = new AEFDFormulaFactory();
    String conditionAEFD = "x1_Libere OU (x2_Occupee ET x3_Bas)";
    aefd_factory.parse(conditionAEFD);
    conditionAEFD = "x1_non_Condamne OU x2_NM ET x3_Libere";
    aefd_factory.parse(conditionAEFD);
    conditionAEFD = "x1_Haut ET x2_Gauche OU x3_Droite";
    aefd_factory.parse(conditionAEFD);
  }

}
