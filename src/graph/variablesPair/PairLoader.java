package graph.variablesPair;

import java.util.LinkedList;

import graph.variablesPair.VariablesPairParser.PairContext;
import graph.variablesPair.VariablesPairParser.PairsContext;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

import utils.Pair;

public class PairLoader extends AbstractParseTreeVisitor<Object>
    implements VariablesPairVisitor<Object> {

  @SuppressWarnings("unchecked")
  @Override
  public Object visitPairs(PairsContext ctx) {
    LinkedList<Pair<String, String>> result = new LinkedList<>();

    for (PairContext pair_ctx : ctx.pair()) {
      result.add((Pair<String, String>) visitPair(pair_ctx));
    }

    return result;

  }

  @Override
  public Object visitPair(PairContext ctx) {
    String first = ctx.ID(0).getText();
    String second = ctx.ID(1).getText();
    return new Pair<String, String>(first, second);
  }

}
