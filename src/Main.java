import java.io.IOException;

import Graph.GraphFactory;
import Graph.Model;
import Graph.Events.SynchronisationEvent;

public class Main {

  public static void main(String[] args) throws IOException {

    GraphFactory test = new GraphFactory("AP_P5_ITI_f_Instance_3411_3421.txt");
    Model model = test.buildModel("Testing model");
    System.out.println(model);

  }

}
