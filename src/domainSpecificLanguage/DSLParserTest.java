package domainSpecificLanguage;

import java.io.IOException;

import domainSpecificLanguage.graph.DSLModel;
import domainSpecificLanguage.parser.FSM_builder;

public class DSLParserTest {

  public static void main(String args[]) throws IOException {

    String example_file = "src/domainSpecificLanguage/Example.txt";

    FSM_builder builder = new FSM_builder();

    builder.parseFile(example_file);
    DSLModel functionnal_model = builder.getModel();
    DSLModel proof_model = builder.getProof();
    System.out.println(functionnal_model);
    System.out.println(proof_model.toString(true));

  }
}
