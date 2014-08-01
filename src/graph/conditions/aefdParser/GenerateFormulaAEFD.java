package graph.conditions.aefdParser;

import graph.conditions.aefdParser.AEFDBooleanExpressionParser.IdExprContext;
import graph.variablesPair.PairLoader;
import graph.variablesPair.VariablesPairLexer;
import graph.variablesPair.VariablesPairParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.gui.TreeViewer;

import utils.IOUtils;
import utils.Pair;
import abstractGraph.conditions.AndFormula;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.FormulaFactory;
import abstractGraph.conditions.NotFormula;
import abstractGraph.conditions.OrFormula;
import abstractGraph.conditions.cnf.Literal;

public class GenerateFormulaAEFD extends
    AEFDBooleanExpressionBaseVisitor<Formula> {

  private static final boolean VIEW_TREE = false;

  private FormulaFactory factory;

  /** MUST be null in the case there is no database of variables */
  private HashMap<String, String> positive_to_negative_variables;
  private HashMap<String, String> negative_to_positive_variables;

  /**
   * This is used ONLY when we do not provied a file defining the pair of
   * variables.
   */
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
  public GenerateFormulaAEFD(FormulaFactory factory,
      String pair_of_variables_file_name) {
    this.factory = factory;

    if (pair_of_variables_file_name == null) {

      assert (positive_suffix.length == negative_suffix.length);

    } else {
      positive_to_negative_variables = new HashMap<>();
      negative_to_positive_variables = new HashMap<>();
      /** We first load the pair of IND and CTL */
      String content;
      try {
        content = IOUtils
            .readFile(pair_of_variables_file_name, StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new Error(e.toString());
      }

      ANTLRInputStream input = new ANTLRInputStream(content);

      /* Create a lexer that feeds off of input CharStream */
      VariablesPairLexer lexer = new VariablesPairLexer(input);

      /* Create a buffer of tokens pulled from the lexer */
      CommonTokenStream tokens = new CommonTokenStream(lexer);

      /* Create a parser that feeds off the tokens buffer */
      VariablesPairParser parser = new VariablesPairParser(tokens);
      /* begin parsing at booleanExpression rule */
      ParseTree tree = parser.pairs();

      /* If view_tree is true, we print the debug tree window */
      if (VIEW_TREE) {
        TreeViewer viewer = new TreeViewer(null, tree);
        viewer.open();
      }

      /** This is the ACTIVE, INACTIVE list of IND and CTL keywords */
      @SuppressWarnings("unchecked")
      LinkedList<Pair<String, String>> list_of_pairs =
          (LinkedList<Pair<String, String>>) new PairLoader().visit(tree);

      for (Pair<String, String> pair : list_of_pairs) {
        String positive = pair.first;
        String negative = pair.second;
        positive_to_negative_variables.put(positive, negative);
        negative_to_positive_variables.put(negative, positive);
      }
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
   * This function is launched when the parser meets a variable.
   * It will find the associated variable, and find if it is a positive or
   * negative version of a declared variable.
   * 
   * @return a Formula
   */
  @Override
  public Formula visitIdExpr(IdExprContext ctx) {
    String indicateur = ctx.ID().getText().trim();

    /* In the case there is no database of variable loaded */
    if (positive_to_negative_variables == null) {
      if (isNegative(indicateur)) {
        String opposite_name = getOppositeName(indicateur);
        if (opposite_name == null) {
          throw new NullPointerException(
              "The parser did not find any negative " +
                  "suffix in the variable " + indicateur);
        }

        return new NotFormula(factory.getVariable(opposite_name));

      } else {
        String variable_name = removePositiveSuffix(indicateur);

        if (variable_name == null) {
          throw new NullPointerException(
              "The parser did not find any negative " +
                  "suffix in the variable " + indicateur);
        }

        Formula temp_formula = factory.getVariable(indicateur);
        assert (temp_formula != null);
        return temp_formula;
      }
    } else {
      return getLiteral(indicateur);
    }
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
   * @param variable_name
   * @return
   */
  Literal getLiteral(String variable_name) {

    /* In the case there is no database of variable loaded */
    if (negative_to_positive_variables == null) {
      /* We MUST call the negative suffix */
      if (isNegative(variable_name)) {
        /* It is a literal: "NOT" + variable_name */
        String opposite_name = getOppositeName(variable_name);
        assert opposite_name != null : "Opposite of " + variable_name
            + " not found.";
        return new Literal(factory.getVariable(opposite_name),
            true);
      } else if (isPositive(variable_name)) {
        /* If it is a literal: variable_name */
        return new Literal(factory.getVariable(variable_name));
      } else {
        throw new NullPointerException("The parser did not find any negative" +
            " or positive suffix in the variable " + variable_name);
      }
    } else {
      /* If the suffix is a positive one */
      if (positive_to_negative_variables.get(variable_name) != null) {
        return new Literal(factory.getVariable(variable_name));
      } else if (negative_to_positive_variables.get(variable_name) != null) {
        return new Literal(
            factory.getVariable(negative_to_positive_variables
                .get(variable_name)),
            true);
      } else {
        throw new Error("The keyword " + variable_name
            + " has not been defined in the database.");
      }
    }

  }

  public void initHashMapSuffixes() {

  }

}
