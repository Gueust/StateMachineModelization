
import Graph.GraphFactoryAEFD;
import Graph.Model;

public class Main {

  public static void main(String[] args) throws Exception {

    GraphFactoryAEFD test =
        new GraphFactoryAEFD("ACLE.txt");
    Model model = test.buildModel("Testing model");
    System.out.println(model);

  }

}
