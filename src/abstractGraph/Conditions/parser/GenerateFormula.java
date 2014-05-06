package abstractGraph.Conditions.parser;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

import abstractGraph.Conditions.AndFormula;
import abstractGraph.Conditions.Formula;
import abstractGraph.Conditions.OrFormula;
import abstractGraph.Conditions.Variable;

public class GenerateFormula extends BooleanExpressionBaseVisitor<Formula> {

  @Override
  public Formula visitAndExpr(
      @NotNull BooleanExpressionParser.AndExprContext ctx) {
    Formula left = visit(ctx.booleanExpression(0));
    Formula right = visit(ctx.booleanExpression(1));
    AndFormula temp_formula = new AndFormula(left, right);
    return temp_formula;
  }

  @Override
  public Formula visitOrExpr(@NotNull BooleanExpressionParser.OrExprContext ctx) {
    Formula left = visit(ctx.booleanExpression(0));
    Formula right = visit(ctx.booleanExpression(1));
    Formula temp_formula = new OrFormula(left, right);
    return temp_formula;
  }

  @Override
  public Formula visitBracketExpr(
      @NotNull BooleanExpressionParser.BracketExprContext ctx) {
    return visit(ctx.booleanExpression());
  }

  @Override
  public Formula visitIdExpr(@NotNull BooleanExpressionParser.IdExprContext ctx) {
    Formula temp_formula = new Variable(ctx.ID().getText());
    return temp_formula;
  }

}
