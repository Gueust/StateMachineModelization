
import abstractGraph.Conditions.parser.ConditionParser;
import Graph.GraphFactoryAEFD;
import Graph.Model;

public class Main {

  public static void main(String[] args) throws Exception {

    GraphFactoryAEFD test =
        new GraphFactoryAEFD("ACLE.txt");
    Model model = test.buildModel("Testing model");
    System.out.println(model);

    String condition = "x1 OU (x2 ET x3)";
    ConditionParser.build(condition);
    condition = "x1 OU x2 ET x3";
    ConditionParser.build(condition);
    condition = "x1 ET x2 OU x3";
    ConditionParser.build(condition);
  }

}
