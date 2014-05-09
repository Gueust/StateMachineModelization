package graph.conditions.aefdParser;

import org.antlr.v4.runtime.misc.NotNull;

import abstractGraph.conditions.AndFormula;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.FormulaFactory;
import abstractGraph.conditions.NotFormula;
import abstractGraph.conditions.OrFormula;
import abstractGraph.conditions.cnf.Literal;

class GenerateFormulaAEFD extends
    AEFDBooleanExpressionBaseVisitor<Formula> {

  private FormulaFactory factory;

  private String[] negative_suffix = { "_non_Bloque", "_non_Condamne",
      "_non_Decondamne", "_non_Etabli", "Chute", "_Inactif", "_non_Prise",
      "_non_Enclenchee", "_Occupee", "_Droite", "_HS", "_Ferme",
      "_non_Controle", "_non_Assuree", "_non_Vide", "_non_Valide", "_NM",
      "_Bas" };
  private String[] positive_suffix = { "_Bloque", "_Condamne", "_Decondamne",
      "_Etabli", "_Excite", "_Actif", "_Prise", "_Enclenchee", "_Libere",
      "_Gauche", "_ES", "_Ouvert", "_Controle", "_Assuree", "_Vide", "_Valide",
      "_M", "_Haut" };

  /**
   * Constructor of the class that will initialize the internal hash map
   * containing the already created variables to the given parameter.
   */
  public GenerateFormulaAEFD(FormulaFactory factory) {
    this.factory = factory;
  }

  /**
   * This function is launched when the parser meets an AND expression.
   * 
   * @return An AndFormula
   */
  @Override
  public Formula visitAndExpr(
      @NotNull AEFDBooleanExpressionParser.AndExprContext ctx) {
    Formula left = visit(ctx.booleanExpression(0));
    Formula right = visit(ctx.booleanExpression(1));
    AndFormula temp_formula = new AndFormula(left, right);
    return temp_formula;
  }

  /**
   * This function is launched when the parser meets an OR expression.
   * 
   * @return An OrFormula
   */
  @Override
  public Formula visitOrExpr(
      @NotNull AEFDBooleanExpressionParser.OrExprContext ctx) {
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
      @NotNull AEFDBooleanExpressionParser.BracketExprContext ctx) {
    return visit(ctx.booleanExpression());
  }

  /**
   * This function is launched when the parser meets a variable with a negative
   * prefix. It will remove the prefix and it will add this variable to the
   * hashmap variables if it doesn't exist yet.
   * 
   * @return a NotFormula
   * @NullPointerException When the suffix parsed by the parser coming from the
   *                       grammar is not find. It means we have forgotten to
   *                       put the suffix in the negative_suffix array.
   */

  @Override
  public Formula visitIdnegatifExpr(
      @NotNull AEFDBooleanExpressionParser.IdnegatifExprContext ctx) {
    String indicateur = ctx.IDNEGATIF().getText().trim();
    String variable_name = removeNegativeSuffix(indicateur);

    if (variable_name == null) {
      throw new NullPointerException("The parser did not find any negative " +
          "suffix in the variable " + indicateur);
    }

    return new NotFormula(factory.getVariable(variable_name));
  }

  /**
   * This function is launched when the parser meets a variable with a positive
   * prefix. It will remove the prefix and it will add this variable to the
   * hashmap variables if it doesn't exist yet
   * 
   * @return a Formula
   */
  @Override
  public Formula visitIdpositifExpr(
      @NotNull AEFDBooleanExpressionParser.IdpositifExprContext ctx) {
    String indicateur = ctx.IDPOSITIF().getText().trim();
    String variable_name = removePositiveSuffix(indicateur);

    if (variable_name == null) {
      throw new NullPointerException("The parser did not find any negative " +
          "suffix in the variable " + indicateur);
    }

    return factory.getVariable(variable_name);
  }

  /**
   * Remove the negative suffix from the parsed variable.
   * 
   * @param input
   * @return The variable name without the first matched negative suffix.
   *         null if the suffix did not match any registered negative suffix.
   */
  private String removeNegativeSuffix(String input) {
    int length_variable;

    for (String suffixe : negative_suffix) {
      if (input.contains(suffixe)) {
        length_variable = input.length() - suffixe.length();
        return input.substring(0, length_variable).trim();
      }
    }
    return null;
  }

  /**
   * Remove the positive suffix from the parsed variable.
   * 
   * @param input
   * @return The variable name without the first matched positive suffix.
   *         null if the suffix did not match any registered positive suffix.
   */
  private String removePositiveSuffix(String input) {
    int length_variable;
    String tmp;

    for (String suffixe : positive_suffix) {
      if (input.contains(suffixe)) {
        length_variable = input.length() - suffixe.length();
        tmp = input.substring(0, length_variable).trim();
        return tmp;
      }
    }
    return null;
  }

  /**
   * Return the literal associated to the input.
   * It will take into account the suffix to decide whether it is a negated
   * variable or a variable.
   * 
   * @param s
   * @return
   */
  Literal getLiteral(String s) {
    /* We MUST call the negative suffix */
    String variable_name = removeNegativeSuffix(s);
    if (variable_name != null) {
      /* It is a literal: "NOT" + variable_name */
      return new Literal(factory.getVariable(variable_name), true);
    }

    variable_name = removePositiveSuffix(s);
    if (variable_name != null) {
      /* If it is a literal: variable_name */
      return new Literal(factory.getVariable(variable_name));
    }

    throw new NullPointerException("The parser did not find any negative " +
        " or positive suffix in the variable " + s);
  }

}
