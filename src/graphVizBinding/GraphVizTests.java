package graphVizBinding;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class GraphVizTests {

  @Test
  public void test() {
    GraphViz gv = new GraphViz();
    gv.addln(gv.start_graph());
    gv.addln("A -> B;");
    gv.addln("A -> C;");
    gv.add(gv.end_graph());
    System.out.println(gv.getDotSource());
    assertEquals(gv.getDotSource(), "digraph G {\n" +
        "A -> B;\n" +
        "A -> C;\n" +
        "}");

    String type = "png";
    File out = new File("./testGraphViz." + type);   // Linux
    gv.writeGraphToFile(gv.getGraph(type), out);
  }
}
