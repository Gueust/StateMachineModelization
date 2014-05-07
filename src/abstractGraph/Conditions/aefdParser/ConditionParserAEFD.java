package abstractGraph.Conditions.aefdParser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.gui.TreeViewer;

import abstractGraph.Conditions.Formula;

public class ConditionParserAEFD {

  static public void build(String expression) throws ClassNotFoundException {

    ANTLRInputStream input = new ANTLRInputStream(expression);

    // create a lexer that feeds off of input CharStream
    AEFDBooleanExpressionLexer lexer = new AEFDBooleanExpressionLexer(input);

    // create a buffer of tokens pulled from the lexer
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    // create a parser that feeds off the tokens buffer
    AEFDBooleanExpressionParser parser = new AEFDBooleanExpressionParser(tokens);
    parser.setBuildParseTree(true);
    ParseTree tree = parser.booleanExpression(); // begin parsing at init rule
    System.out.println(tree.toStringTree(parser)); // print LISP-style tree

    GenerateFormulaAEFD generation_of_formula = new GenerateFormulaAEFD();
    Formula f = generation_of_formula.visit(tree);
    System.out.print(f);
  }
}
