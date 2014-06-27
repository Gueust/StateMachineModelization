package graph.conditions.aefdParser;

import java.util.HashMap;

import org.antlr.v4.runtime.misc.NotNull;

import abstractGraph.conditions.AndFormula;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.FormulaFactory;
import abstractGraph.conditions.NotFormula;
import abstractGraph.conditions.OrFormula;
import abstractGraph.conditions.cnf.Literal;

public class GenerateFormulaAEFD extends
    AEFDBooleanExpressionBaseVisitor<Formula> {

  private FormulaFactory factory;

  private HashMap<String, String> suffixes;
  private static final String[] negative_suffix = { "_non_Bloque",
      "_non_Condamne",
      "_non_Decondamne",
      "_non_Etabli",
      "_Chute",
      "_Inactif",
      "_Inactive",
      "_non_Prise",
      "_non_Enclenchee",
      "_non_Enclenche",
      "_Occupee",
      "_Droite",
      "_HS",
      "_Ferme",
      "_non_Controle",
      "_non_Assuree",
      "_non_Assure",
      "_non_Vide",
      "_non_Valide",
      "_NM",
      "_Bas",
      "_en_Action",
      "_non_Pris",
      "_Renverse",
      "_Declenche",
      "_NOK",
      "_non_cde",
      "_non_en_cours",
      "_pas_Init" };
  private static final String[] positive_suffix = { "_Bloque",
      "_Condamne", "_Decondamne",
      "_Etabli", "_Excite", "_Actif",
      "_Active",
      "_Prise",
      "_Enclenchee",
      "_Enclenche",
      "_Libre",
      "_Gauche",
      "_ES",
      "_Ouvert",
      "_Controle",
      "_Assuree",
      "_Assure",
      "_Vide",
      "_Valide",
      "_M",
      "_Haut",
      "_Libere",
      "_Pris",
      "_Normal",
      "_Rearme",
      "_OK",
      "_cde",
      "_en_cours",
      "_en_Init" };

  /**
   * @details Compare the given string with the given positive suffixes. Because
   *          some positive suffixes are included in the negative suffixes, we
   *          first check that the given string does not contain a negative
   *          suffix.
   * @param input
   * @return true if the given string is a positive variable. false otherwise.
   */
  static public boolean isPositive(String input) {
    if (isNegative(input)) {
      return false;
    }
    return removePositiveSuffix(input) != null;
  }

  /**
   * 
   * @param input
   * @return true if the given string is a positive variable. false otherwise.
   */
  static public boolean isNegative(String input) {
    return removeNegativeSuffix(input) != null;
  }

  static private String getOppositeSuffix(String suffix) {

    /*
     * The test about the negative suffix MUST be done before the positive
     * suffix, since the latter is included in the former
     */
    for (int i = 0; i < negative_suffix.length; i++) {
      if (negative_suffix[i].equals(suffix)) {
        return positive_suffix[i];
      }
    }

    for (int i = 0; i < positive_suffix.length; i++) {
      if (positive_suffix[i].equals(suffix)) {
        return negative_suffix[i];
      }
    }
    throw new Error("Not found.");
  }

  /**
   * 
   * @param name
   * @return
   */
  static public String getOppositeName(String name) {
    String tmp, opposite_suffix;
    if (isPositive(name)) {
      tmp = removePositiveSuffix(name);
    } else if (isNegative(name)) {
      tmp = removeNegativeSuffix(name);
    } else {
      return null;
    }

    opposite_suffix =
        getOppositeSuffix(name.substring(tmp.length(), name.length()));

    return tmp + opposite_suffix;
  }

  /**
   * Constructor of the class that will initialize the internal hash map
   * containing the already created variables to the given parameter.
   */
  public GenerateFormulaAEFD(FormulaFactory factory) {
    this.factory = factory;
    /*
     * Initialization of the hashmap containing the suffixes
     */
    suffixes = new HashMap<String, String>();
    if (negative_suffix.length == positive_suffix.length) {
      for (int i = 0; i < positive_suffix.length; i++) {
        suffixes.put(negative_suffix[i], positive_suffix[i]);
      }
    } else {
      throw new IllegalArgumentException(
          "The two tables negative_suffixe and positive_suffixe don't have the same size. Check their contents.");
    }
  }

  /**
   * This function is launched when the parser meets a negation ('not' or
   * 'NOT').
   * 
   * @return A Formula
   */
  @Override
  public Formula visitNotExpr(
      @NotNull AEFDBooleanExpressionParser.NotExprContext ctx) {
    return new NotFormula(visit(ctx.booleanExpression()));
  }

  /**
   * This function is launched when the parser meets a "true".
   * 
   * @return
   */
  @Override
  public Formula visitTrueExpr(
      @NotNull AEFDBooleanExpressionParser.TrueExprContext ctx) {
    Formula temp_formula = Formula.TRUE;
    return temp_formula;
  }

  /**
   * This function is launched when the parser meets a "false".
   * 
   * @return
   */

  @Override
  public Formula visitFalseExpr(
      @NotNull AEFDBooleanExpressionParser.FalseExprContext ctx) {
    Formula temp_formula = Formula.FALSE;
    return temp_formula;
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
    return new AndFormula(left, right);
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
    assert (left != null && right != null);
    return new OrFormula(left, right);
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
    Formula temp_formula = visit(ctx.booleanExpression());
    assert (temp_formula != null);
    return temp_formula;
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
    String suffix_negative = indicateur.substring(variable_name.length(),
        indicateur.length());
    String suffix_positive = suffixes.get(suffix_negative);
    variable_name = variable_name.concat(suffix_positive);
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

    Formula temp_formula = factory.getVariable(indicateur);
    assert (temp_formula != null);
    return temp_formula;
  }

  /**
   * Remove the negative suffix from the parsed variable.
   * 
   * @param input
   * @return The variable name without the first matched negative suffix.
   *         null if the suffix did not match any registered negative suffix.
   */
  private static String removeNegativeSuffix(String input) {
    int length_variable;

    for (String suffixe : negative_suffix) {
      if (input.endsWith(suffixe)) {
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
  private static String removePositiveSuffix(String input) {
    int length_variable;
    String tmp;

    for (String suffixe : positive_suffix) {
      if (input.endsWith(suffixe)) {
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
      String suffix_negative = s.substring(variable_name.length(), s.length());
      /* It is a literal: "NOT" + variable_name */
      return new Literal(factory.getVariable(variable_name.concat(suffixes
          .get(suffix_negative))), true);
    }

    variable_name = removePositiveSuffix(s);
    if (variable_name != null) {
      /* If it is a literal: variable_name */
      return new Literal(factory.getVariable(s));
    }

    throw new NullPointerException("The parser did not find any negative" +
        " or positive suffix in the variable " + s);
  }

  public void initHashMapSuffixes() {

  }

}
