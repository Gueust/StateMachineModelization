package abstractGraph.Conditions.aefdParser;

import java.util.HashMap;

import org.antlr.v4.runtime.misc.NotNull;

import abstractGraph.Conditions.AndFormula;
import abstractGraph.Conditions.Formula;
import abstractGraph.Conditions.NotFormula;
import abstractGraph.Conditions.OrFormula;
import abstractGraph.Conditions.Variable;

public class GenerateFormulaAEFD extends
    AEFDBooleanExpressionBaseVisitor<Formula> {

  private HashMap<String, Variable> variables;
  private String[] negative_suffixe = { "_non_Bloque", "_non_Condamne",
      "_non_Decondamne", "_non_Etabli", "Chute", "_Inactif", "_non_Prise",
      "_non_Enclenchee", "_Occupee", "_Droite", "_HS", "_Ferme",
      "_non_Controle", "_non_Assuree", "_non_Vide", "_non_Valide", "_NM",
      "_Bas" };
  private String[] positive_suffixe = { "_Bloque", "_Condamne", "_Decondamne",
      "_Etabli", "_Excite", "_Actif", "_Prise", "_Enclenchee", "_Libere",
      "_Gauche", "_ES", "_Ouvert", "_Controle", "_Assuree", "_Vide", "_Valide",
      "_M", "_Haut" };

  /**
   * Constructor of the class that will initialize the hash map variables
   */
  public GenerateFormulaAEFD() {
    variables = new HashMap<String, Variable>();
  }

  /**
   * This function is lunched when the parser meets an AND expression
   * 
   * @return an AndFormula
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
   * This function is lunched when the parser meets an OR expression.
   * 
   * @return an OrFormula
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
   * This function is lunched when the parser meets an expression between
   * brackets.
   * 
   * @return a Formula
   */
  @Override
  public Formula visitBracketExpr(
      @NotNull AEFDBooleanExpressionParser.BracketExprContext ctx) {
    return visit(ctx.booleanExpression());
  }

  /**
   * This function is lunched when the parser meets a variable with a negative
   * prefix. It will remove the prefixe and it will add this variable to the
   * hashmap variables if it doesn't exist yet
   * 
   * @return a NotFormula
   */

  @Override
  public Formula visitIdnegatifExpr(
      @NotNull AEFDBooleanExpressionParser.IdnegatifExprContext ctx) {
    String variable = "";
    int length_variable;
    for (String suffixe : negative_suffixe) {
      if (ctx.IDNEGATIF().getText().contains(suffixe)) {
        length_variable = ctx.IDNEGATIF().getText().length() - suffixe.length();
        variable = ctx
            .IDNEGATIF()
            .getText()
            .substring(0, length_variable)
            .trim();
      }
    }
    Variable temp_variable;
    if (!variables.containsKey(variable)) {
      temp_variable = new Variable(variable);
      variables.put(variable, temp_variable);
    } else {
      temp_variable = variables.get(variable);
    }
    Formula temp_formula = new NotFormula(temp_variable);
    return (temp_formula);
  }

  /**
   * This function is lunched when the parser meets a variable with a positive
   * prefix. It will remove the prefixe and it will add this variable to the
   * hashmap variables if it doesn't exist yet
   * 
   * @return a Formula
   */
  @Override
  public Formula visitIdpositifExpr(
      @NotNull AEFDBooleanExpressionParser.IdpositifExprContext ctx) {
    String variable = "";
    int length_variable;
    for (String suffixe : positive_suffixe) {
      if (ctx.IDPOSITIF().getText().contains(suffixe)) {
        length_variable = ctx.IDPOSITIF().getText().length() - suffixe.length();
        variable = ctx
            .IDPOSITIF()
            .getText()
            .substring(0, length_variable)
            .trim();
      }
    }
    Variable temp_variable;
    if (!variables.containsKey(variable)) {
      temp_variable = new Variable(variable);
      variables.put(variable, temp_variable);
    } else {
      temp_variable = variables.get(variable);
    }
    Formula temp_formula = temp_variable;
    return temp_formula;
  }

}
