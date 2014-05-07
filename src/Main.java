import graph.GraphFactoryAEFD;
import graph.Model;
import abstractGraph.conditions.aefdParser.ConditionParserAEFD;
import abstractGraph.conditions.Formula;

public class Main {

  public static void main(String[] args) throws Exception {

    GraphFactoryAEFD test =
        new GraphFactoryAEFD("ACLE.txt");
    Model model = test.buildModel("Testing model");
    System.out.println(model);

    String condition = "x1 OU (x2 ET x3)";
    Formula.parse(condition);
    condition = "x1 OU x2 ET x3";
    Formula.parse(condition);
    condition = "x1 ET x2 OU x3";
    Formula.parse(condition);
    condition = "not x1 ET x2 OU x3 ET NOT X4";
    Formula.parse(condition);

    // Test the AEFD parser
    String conditionAEFD = "x1_Libere OU (x2_Occupee ET x3_Bas)";
    ConditionParserAEFD.build(conditionAEFD);
    conditionAEFD = "x1_non_Condamne OU x2_NM ET x3_Libere";
    ConditionParserAEFD.build(conditionAEFD);
    conditionAEFD = "x1_Haut ET x2_Gauche OU x3_Droite";
    ConditionParserAEFD.build(conditionAEFD);
  }

}
