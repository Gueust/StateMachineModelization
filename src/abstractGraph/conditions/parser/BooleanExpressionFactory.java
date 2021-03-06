package abstractGraph.conditions.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.gui.TreeViewer;

import abstractGraph.conditions.Formula;
import abstractGraph.conditions.FormulaFactory;

public class BooleanExpressionFactory extends FormulaFactory {

  private GenerateFormula generator_of_formula;

  /**
   * @see FormulaFactory#FormulaFactory(boolean)
   */
  public BooleanExpressionFactory(boolean united_model_mode) {
    super(united_model_mode);

    generator_of_formula = new GenerateFormula(this);
  }

  /**
   * Parse literally a string expression into a formula.
   * The formula accepts AND, &&, &, ET as the "and" operator ;
   * OR, ||, |, OU as the "or" operator".
   * 
   * @param expression
   *          The string to parse
   * @param view_tree
   *          Set to "debug" mode and display the parsed formula as a tree
   * @return The exact formula parsed without any modification.
   *         null if the formula is empty.
   */
  @Override
  public Formula parse(String expression, boolean view_tree) {
    reset();

    String trimed = expression.trim();
    if (trimed.equals("")) {
      return null;
    }

    ANTLRInputStream input = new ANTLRInputStream(expression);

    /* Create a lexer that feeds off of input CharStream */
    BooleanExpressionLexer lexer = new BooleanExpressionLexer(input);

    /* Create a buffer of tokens pulled from the lexer */
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    /* Create a parser that feeds off the tokens buffer */
    BooleanExpressionParser parser = new BooleanExpressionParser(tokens);
    /* begin parsing at booleanExpression rule */
    ParseTree tree = parser.booleanExpression();

    /* If view_tree is true, we print the debug tree window */
    if (view_tree) {
      TreeViewer viewer = new TreeViewer(null, tree);
      viewer.open();
    }

    Formula f = generator_of_formula.visit(tree);
    return f;
  }
}
