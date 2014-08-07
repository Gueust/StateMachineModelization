package graphVizBinding;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class GraphVizTests {

  @Test
  public void test() throws IOException {
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

    gv.writeGraphToFile("testGraphViz", type);
  }
}
