package domainSpecificLanguage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.gui.TreeViewer;

import utils.IOUtils;
import domainSpecificLanguage.graph.DSLModel;
import domainSpecificLanguage.parser.FSM_LanguageLexer;
import domainSpecificLanguage.parser.FSM_LanguageParser;
import domainSpecificLanguage.parser.FSM_builder;

public class DSLParserTest {

  public static void main(String args[]) throws IOException {
    String content = IOUtils.readFile("src/domainSpecificLanguage/Example.txt",
        StandardCharsets.UTF_8);

    ANTLRInputStream input = new ANTLRInputStream(content);

    /* Create a lexer that feeds off of input CharStream */
    FSM_LanguageLexer lexer = new FSM_LanguageLexer(input);

    /* Create a buffer of tokens pulled from the lexer */
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    /* Create a parser that feeds off the tokens buffer */
    FSM_LanguageParser parser = new FSM_LanguageParser(tokens);
    /* begin parsing at booleanExpression rule */
    ParseTree tree = parser.model();

    /* If view_tree is true, we print the debug tree window */
    TreeViewer viewer = new TreeViewer(null, tree);
    viewer.open();

    FSM_builder builder = new FSM_builder();
    builder.visit(tree);
    DSLModel functionnal_model = builder.getModel();
    DSLModel proof_model = builder.getProof();
    System.out.println(functionnal_model);
    System.out.println(proof_model.toString(true));

  }
}
