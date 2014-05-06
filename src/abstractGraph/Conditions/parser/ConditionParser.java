package abstractGraph.Conditions.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.gui.TreeViewer;

import abstractGraph.Conditions.Formula;

public class ConditionParser {

  static public void build(String expression) throws ClassNotFoundException {

    ANTLRInputStream input = new ANTLRInputStream(expression);

    // create a lexer that feeds off of input CharStream
    BooleanExpressionLexer lexer = new BooleanExpressionLexer(input);

    // create a buffer of tokens pulled from the lexer
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    // create a parser that feeds off the tokens buffer
    BooleanExpressionParser parser = new BooleanExpressionParser(tokens);
    parser.setBuildParseTree(true);
    ParseTree tree = parser.booleanExpression(); // begin parsing at init rule
    System.out.println(tree.toStringTree(parser)); // print LISP-style tree

    TreeViewer viewer = new TreeViewer(null, tree);
    viewer.open();

    GenerateFormula generation_of_formula = new GenerateFormula();
    Formula f = generation_of_formula.visit(tree);
    System.out.print(f);
  }
}
