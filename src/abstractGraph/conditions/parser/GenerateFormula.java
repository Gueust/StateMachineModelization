package abstractGraph.conditions.parser;

import org.antlr.v4.runtime.misc.NotNull;

import abstractGraph.conditions.AndFormula;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.FormulaFactory;
import abstractGraph.conditions.NotFormula;
import abstractGraph.conditions.OrFormula;

class GenerateFormula extends BooleanExpressionBaseVisitor<Formula> {

  private FormulaFactory factory;

  /**
   * Constructor of the class that will initialize the hash map variables to the
   * given parameter.
   */
  public GenerateFormula(FormulaFactory factory) {
    this.factory = factory;
  }

  /**
   * This function is launched when the parser meets a negation ('not' or
   * 'NOT').
   * 
   * @return A Formula
   */
  @Override
  public Formula visitNotExpr(
      @NotNull BooleanExpressionParser.NotExprContext ctx) {
    Formula temp_formula = new NotFormula(visit(ctx.booleanExpression()));
    return temp_formula;
  }

  /**
   * This function is launched when the parser meets an AND expression.
   * 
   * @return An AndFormula
   */
  @Override
  public Formula visitAndExpr(
      @NotNull BooleanExpressionParser.AndExprContext ctx) {
    Formula left = visit(ctx.booleanExpression(0));
    Formula right = visit(ctx.booleanExpression(1));
    AndFormula temp_formula = new AndFormula(left, right);
    return temp_formula;
  }

  /**
   * This function is launched when the parser meets an OR expression.
   * 
   * @return An OrFormula.
   */
  @Override
  public Formula visitOrExpr(@NotNull BooleanExpressionParser.OrExprContext ctx) {
    Formula left = visit(ctx.booleanExpression(0));
    Formula right = visit(ctx.booleanExpression(1));
    Formula temp_formula = new OrFormula(left, right);
    return temp_formula;
  }

  /**
   * This function is launched when the parser meets an expression between
   * parenthesis.
   * 
   * @return The Formula within parenthesis.
   */
  @Override
  public Formula visitBracketExpr(
      @NotNull BooleanExpressionParser.BracketExprContext ctx) {
    return visit(ctx.booleanExpression());
  }

  /**
   * This function is launched when the parser meets a variable. It will use
   * the hashmap `variables` to retrieve already existing variables if it is not
   * null.
   * 
   * @return a Formula
   */
  @Override
  public Formula visitIdExpr(@NotNull BooleanExpressionParser.IdExprContext ctx) {
    String variable_name = ctx.ID().getText().trim();

    return factory.getVariable(variable_name);
  }
}
