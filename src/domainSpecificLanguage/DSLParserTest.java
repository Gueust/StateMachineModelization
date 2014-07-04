package domainSpecificLanguage;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.gui.TreeViewer;

import domainSpecificLanguage.parser.FSM_LanguageLexer;
import domainSpecificLanguage.parser.FSM_LanguageParser;
import domainSpecificLanguage.parser.FSM_builder;

public class DSLParserTest {

  static String readFile(String path, Charset encoding)
      throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, encoding);
  }

  public static void main(String args[]) throws IOException {
    String content = readFile("src/domainSpecificLanguage/Example.txt",
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
    Object obj = builder.visit(tree);
    System.out.println(obj);
  }
}
