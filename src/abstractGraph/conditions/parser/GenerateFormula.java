package abstractGraph.conditions.parser;

import java.util.HashMap;

import org.antlr.v4.runtime.misc.NotNull;

import abstractGraph.conditions.AndFormula;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.NotFormula;
import abstractGraph.conditions.OrFormula;
import abstractGraph.conditions.Variable;

class GenerateFormula extends BooleanExpressionBaseVisitor<Formula> {

  private HashMap<String, Variable> variables;

  public void setVariables(HashMap<String, Variable> variables) {
    this.variables = variables;
  }

  /**
   * Constructor of the class that will initialize the hash map variables to the
   * given parameter.
   */
  public GenerateFormula(HashMap<String, Variable> variables) {
    this.variables = variables;
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
    Variable v;
    String variable_name = ctx.ID().getText().trim();

    /* If the BooleanFactory is in single formula mode */
    if (variables == null) {
      v = new Variable(variable_name);
    } else { /* Otherwise we retrieve the variable if it exists */
      v = variables.get(variable_name);
      if (v == null) {
        v = new Variable(variable_name);
        variables.put(variable_name, v);
      }
    }
    return v;
  }
}
