package graph.conditions.aefdParser;

import engine.SequentialGraphSimulator;
import graph.GraphFactoryAEFD;
import graph.State;
import graph.StateMachine;
import graph.Transition;
import graph.conditions.aefdParser.AEFDBooleanExpressionParser.IdExprContext;
import graph.variablesPair.PairLoader;
import graph.variablesPair.VariablesPairLexer;
import graph.variablesPair.VariablesPairParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.gui.TreeViewer;

import utils.IOUtils;
import utils.Pair;
import abstractGraph.conditions.AndFormula;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.FormulaFactory;
import abstractGraph.conditions.NotFormula;
import abstractGraph.conditions.OrFormula;
import abstractGraph.conditions.cnf.Literal;
import abstractGraph.events.Actions;
import abstractGraph.events.Events;
import abstractGraph.events.ExternalEvent;
import abstractGraph.events.VariableChange;

public class GenerateFormulaAEFD extends
    AEFDBooleanExpressionBaseVisitor<Formula> {

  private static final boolean VIEW_TREE = false;

  private FormulaFactory factory;

  /** MUST be null in the case there is no database of variables */
  private LinkedHashMap<String, String> positive_to_negative_variables;
  private LinkedHashMap<String, String> negative_to_positive_variables;

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
      // "_en_Action",
      // "_non_Pris",
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
      // "_Libere",
      // "_Pris",
      "_Normal",
      "_Rearme",
      "_OK",
      "_cde",
      "_en_cours",
      "_en_Init" };

  private boolean hasLoadedVariablesDatabase() {
    return positive_to_negative_variables != null;
  }

  class Quadruplet {
    public String associated_variable;
    public boolean is_negated;
    public String positive_template;
    public String negative_template;

    public Quadruplet(String associated_variable, boolean is_negated,
        String positive_template, String negative_template) {
      super();
      this.associated_variable = associated_variable;
      this.is_negated = is_negated;
      this.positive_template = positive_template;
      this.negative_template = negative_template;
    }
  }

  /**
   * For internal use, ONLY when suffixes have been loaded from a file (i.e.
   * when hasLoadedVariablesDatabase() ).
   * 
   * @param input
   *          A name of a variable literal.
   * @return (The associated variable name, is input a negation of this
   *         variable).
   */
  public Pair<String, Boolean> getAssociatedVariable(String input) {
    Quadruplet details = getAssociatedVariableDetails(input);
    return new Pair<>(details.associated_variable, details.is_negated);
  }

  private Quadruplet getAssociatedVariableDetails(String input) {

    for (Entry<String, String> entry : positive_to_negative_variables
        .entrySet()) {

      String positive_template = entry.getKey();
      String negative_template = entry.getValue();
      /*
       * In the database, there is 2 kinds of data : the instanciated one, and
       * the "direct" one.
       * We first look up for a direct occurence.
       */
      if (input.equals(positive_template)) {
        return new Quadruplet(positive_template, false, positive_template,
            negative_template);
      } else if (input.equals(negative_template)) {
        return new Quadruplet(positive_template, true, positive_template,
            negative_template);
      }

      String[] tmp;

      tmp = positive_template.split("%P");
      if (tmp.length != 2) {
        continue;
      }
      String positive_prefix = tmp[0];
      String positive_suffix = tmp[1];

      tmp = negative_template.split("%P");
      String negative_prefix = tmp[0];
      String negative_suffix = tmp[1];

      /*
       * Since for some pair, a suffix is included in its opposite suffix, we
       * need to check for the longest template first
       */
      boolean compare_to_positive_suffix_first;
      String first_prefix, first_suffix;
      String second_prefix, second_suffix;

      if (positive_template.length() > negative_template.length()) {
        compare_to_positive_suffix_first = true;
        first_prefix = positive_prefix;
        first_suffix = positive_suffix;
        second_prefix = negative_prefix;
        second_suffix = negative_suffix;
      } else {
        compare_to_positive_suffix_first = false;
        first_prefix = negative_prefix;
        first_suffix = negative_suffix;
        second_prefix = positive_prefix;
        second_suffix = positive_suffix;
      }

      if (input.startsWith(first_prefix) && input.endsWith(first_suffix)) {
        /* The variable matches the first template */
        if (compare_to_positive_suffix_first) {
          return new Quadruplet(input, false, positive_template,
              negative_template);
        } else {
          return new Quadruplet(input.replaceAll(first_suffix, second_suffix),
              true, positive_template, negative_template);
        }
      } else if (input.startsWith(second_prefix)
          && input.endsWith(second_suffix)) {
        if (compare_to_positive_suffix_first) {
          return new Quadruplet(input.replaceAll(second_suffix, first_suffix),
              true, positive_template, negative_template);
        } else {
          return new Quadruplet(input, false, positive_template,
              negative_template);
        }
      }
    }
    throw new Error("The variable " + input
        + " has not been found in the database.");
  }

  /**
   * @details Compare the given string with the given positive suffixes. Because
   *          some positive suffixes are included in the negative suffixes, we
   *          first check that the given string does not contain a negative
   *          suffix.
   * @param input
   * @return true if the given string is a positive variable. false otherwise.
   */
  public boolean isPositive(String input) {
    if (hasLoadedVariablesDatabase()) {
      Pair<String, Boolean> pair = getAssociatedVariable(input);
      return !pair.second;
    } else {

      if (isNegative(input)) {
        return false;
      }
      return removePositiveSuffix(input) != null;
    }
  }

  /**
   * 
   * @param input
   * @return true if the given string is a positive variable. false otherwise.
   */
  public boolean isNegative(String input) {
    if (hasLoadedVariablesDatabase()) {
      return !isPositive(input);
    } else {
      return removeNegativeSuffix(input) != null;
    }
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
   * @param name
   * @return
   */
  public String getOppositeName(String name) {
    if (hasLoadedVariablesDatabase()) {
      Pair<String, Boolean> pair = getAssociatedVariable(name);
      boolean is_negated = pair.second;

      if (!is_negated) {
        return pair.first;
      } else {
        String positive_name = pair.first;

        for (Entry<String, String> entry : positive_to_negative_variables
            .entrySet()) {

          String positive_template = entry.getKey();
          String negative_template = entry.getValue();
          /*
           * In the database, there is 2 kinds of data : the instanciated one,
           * and
           * the "direct" one.
           * We first look up for a direct occurence.
           */
          /* It is a positive name and is not negated */
          if (positive_name.equals(positive_template)) {
            return negative_template;
          }

          String[] tmp;

          tmp = positive_template.split("%P");
          if (tmp.length != 2) {
            continue;
          }
          String positive_prefix = tmp[0];
          String positive_suffix = tmp[1];

          tmp = negative_template.split("%P");
          String negative_suffix = tmp[1];

          if (positive_name.startsWith(positive_prefix) &&
              positive_name.endsWith(positive_suffix)) {
            return positive_name.replaceAll(positive_suffix, negative_suffix);
          }
        }
        throw new Error("Impossible scenario. The positive " + positive_name
            + " has not been found for " + name);
      }
    } else {
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
      positive_to_negative_variables = new LinkedHashMap<>();
      negative_to_positive_variables = new LinkedHashMap<>();
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
    if (!hasLoadedVariablesDatabase()) {
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
    if (!hasLoadedVariablesDatabase()) {
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
      Pair<String, Boolean> variable_identification =

          getAssociatedVariable(variable_name);
      String positive_variable = variable_identification.first;
      Boolean is_negated = variable_identification.second;

      if (is_negated) {
        return new Literal(
            factory.getVariable(positive_variable), true);
      } else {
        return new Literal(factory.getVariable(positive_variable));
      }
    }
  }

  public String generateAutomateForCTL(String CTL_pos, String CTL_neg) {

    assert (!CTL_pos.equals(CTL_neg));

    /*
     * New fresh variables are created, but since it is only written as a
     * string, it does not change anything
     */
    String positive_suffix;
    String IND_actif_name;

    if (hasLoadedVariablesDatabase()) {

      Quadruplet details = getAssociatedVariableDetails(CTL_pos);
      /*
       * String positive_template = details.positive_template;
       * String negative_template = details.negative_template;
       * details = getAssociatedVariableDetails(CTL_pos);
       * String positive_template_2 = details.positive_template;
       * String negative_template_2 = details.negative_template;
       * assert (positive_template.equals(positive_template_2));
       * assert (negative_template.equals(negative_template_2));
       * 
       * String[] tmp = positive_template.split("%P");
       * assert tmp.length == 2 : "We cannot get the suffix of "
       * + positive_template;
       * positive_suffix = tmp[1];
       * 
       * variable_name =
       * positive_prefix.substring(positive_prefix.indexOf('_') + 1,
       * positive_prefix.length());
       */
      IND_actif_name = details.associated_variable.replaceFirst("CTL", "IND");
      // positive_prefix.replaceFirst("CTL", "IND").concat(
      // positive_suffix);
    } else {
      String variable_name =
          CTL_pos.substring(CTL_pos.indexOf('_') + 1, CTL_pos.length());
      variable_name = variable_name
          .substring(0, variable_name.lastIndexOf('_'));

      positive_suffix =
          CTL_pos.substring(CTL_pos.lastIndexOf('_') + 1, CTL_pos.length());

      IND_actif_name = "IND_" + variable_name + "_" + positive_suffix;
    }

    // assert (IND_actif_name.lastIndexOf("__") == -1) : IND_actif_name;
    StateMachine machine = new StateMachine("GRAPH_" + IND_actif_name, -1);
    assert ("GRAPH_CTL_RPD_3422b_Excite" != "GRAPH_" + IND_actif_name);

    State init_state = machine.addState("0");
    State positive_state = machine.addState("1");
    State negative_state = machine.addState("2");

    Events events;
    Actions actions;
    Formula condition;
    BooleanVariable variable = factory.getVariable(IND_actif_name);

    /* Transition from 0 to 1 */
    events = new Events();
    events.addEvent(SequentialGraphSimulator.ACT_INIT);
    condition = factory.parse(CTL_pos);
    actions = new Actions();
    actions.add(new VariableChange(new Literal(variable)));
    machine.addTransition(init_state, positive_state,
        events, condition, actions);

    /* Transition from 0 to 2 */
    events = new Events();
    events.addEvent(SequentialGraphSimulator.ACT_INIT);
    condition = factory.parse(CTL_neg);
    actions = new Actions();
    actions.add(new VariableChange(new Literal(variable, true)));
    machine.addTransition(init_state, negative_state,
        events, condition, actions);

    /* Transition from 1 to 2 */
    events = new Events();
    events.addEvent(new ExternalEvent(CTL_neg));
    actions = new Actions();
    actions.add(new VariableChange(new Literal(variable, true)));
    machine.addTransition(positive_state, negative_state,
        events, null, actions);

    /* Transition from 2 to 1 */
    events = new Events();
    events.addEvent(new ExternalEvent(CTL_pos));
    actions = new Actions();
    actions.add(new VariableChange(new Literal(variable)));
    machine.addTransition(negative_state, positive_state,
        events, null, actions);

    StringBuffer buffer = new StringBuffer();

    boolean first = true;
    Iterator<Transition> trans_it = machine.iteratorTransitions();
    while (trans_it.hasNext()) {
      if (first) {
        first = false;
      } else {
        buffer.append("\r\n");
      }

      buffer
          .append(GraphFactoryAEFD.writeTransition(machine,
              trans_it.next(),
              ((AEFDFormulaFactory) factory).generator_of_formula));
    }

    return buffer.toString();
  }
}
