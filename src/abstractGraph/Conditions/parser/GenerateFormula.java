package abstractGraph.Conditions.parser;

import java.util.HashMap;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

import abstractGraph.Conditions.AndFormula;
import abstractGraph.Conditions.Formula;
import abstractGraph.Conditions.OrFormula;
import abstractGraph.Conditions.Variable;

public class GenerateFormula extends BooleanExpressionBaseVisitor<Formula> {

  private HashMap<String, Variable> variables;

  /**
   * Constructor of the class that will initialize the hash map variables
   */
  public GenerateFormula() {
    variables = new HashMap<String, Variable>();
  }

  /**
   * This function is lunched when the parser meets an AND expression
   * 
   * @return an AndFormula
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
   * This function is lunched when the parser meets an OR expression.
   * 
   * @return an OrFormula
   */
  @Override
  public Formula visitOrExpr(@NotNull BooleanExpressionParser.OrExprContext ctx) {
    Formula left = visit(ctx.booleanExpression(0));
    Formula right = visit(ctx.booleanExpression(1));
    Formula temp_formula = new OrFormula(left, right);
    return temp_formula;
  }

  /**
   * This function is lunched when the parser meets an expression between
   * brackets.
   * 
   * @return a Formula
   */
  @Override
  public Formula visitBracketExpr(
      @NotNull BooleanExpressionParser.BracketExprContext ctx) {
    return visit(ctx.booleanExpression());
  }

  /**
   * This function is lunched when the parser meets a variable. It will add this
   * variable to the hashmap variables if it doesn't exist yet
   * 
   * @return a Formula
   */
  @Override
  public Formula visitIdExpr(@NotNull BooleanExpressionParser.IdExprContext ctx) {
    Variable temp_variable;
    if (!variables.containsKey(ctx.ID().getText())) {
      temp_variable = new Variable(ctx.ID().getText().trim());
      variables.put(ctx.ID().getText().trim(), temp_variable);
    } else {
      temp_variable = variables.get(ctx.ID().getText().trim());
    }
    Formula temp_formula = temp_variable;
    return temp_formula;
  }

}
