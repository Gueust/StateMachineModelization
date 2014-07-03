package domainSpecificLanguage.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utils.GenericToString;
import abstractGraph.conditions.AndFormula;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.NotFormula;
import abstractGraph.conditions.OrFormula;
import abstractGraph.conditions.Variable;
import abstractGraph.events.ExternalEvent;
import domainSpecificLanguage.Enumeration;
import domainSpecificLanguage.graph.DSLTransition;
import domainSpecificLanguage.parser.FSM_LanguageParser.ActionAssignmentContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.ActionEventContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.ActionsContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.AndExprContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.BoolDeclarationContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.BracketExprContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.Domain_declarationContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.EventsContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.FalseExprContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.IdExprContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.List_of_IDContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.ModelContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.Model_alternativesContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.NodeContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.NotExprContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.One_bool_declarationContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.One_other_declarationContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.OrExprContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.OtherDeclarationContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.PairContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.SubContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.TemplateContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.TransContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.TransitionContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.TrueExprContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.Variables_declarationContext;

public class FSM_builder extends AbstractParseTreeVisitor<Object>
    implements FSM_LanguageVisitor<Object> {

  Map<String, Variable> variables = new HashMap<>();
  Map<Variable, Byte> initial_values = new HashMap<>();
  Map<String, ExternalEvent> external_events = new HashMap<>();
  Map<String, Enumeration> enumerations = new HashMap<>();
  Map<Variable, Enumeration> enumerated_variable = new HashMap<>();

  Set<DSLTransition> transitions = new HashSet<>();

  private Variable getVariable(String name) {
    Variable var = variables.get(name);
    if (var == null) {
      var = new Variable(name);
      variables.put(name, var);
    }
    return var;
  }

  private String getDetails(Token token) {
    return "line " + token.getLine() + ", character "
        + token.getCharPositionInLine();
  }

  @Override
  public Object visitTemplate(TemplateContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitAndExpr(AndExprContext ctx) {
    Formula left = (Formula) visit(ctx.formula(0));
    Formula right = (Formula) visit(ctx.formula(1));
    return new AndFormula(left, right);
  }

  /**
   * @return A Set of the contained IDs.
   */
  @Override
  public Object visitList_of_ID(List_of_IDContext ctx) {
    /*
     * We can either access the list of ID iterating over the IDs, or call this
     * function.
     */
    Set<String> result = new HashSet<>();
    for (TerminalNode terminal_node : ctx.ID()) {
      String item = terminal_node.getText();
      if (result.contains(item)) {
        throw new Error("The identifier " + item + " is present twice at "
            + getDetails(ctx.getStart()));
      }
      result.add(item);
    }
    return result;
  }

  @Override
  public Object visitModel(ModelContext ctx) {
    // TODO Auto-generated method stub
    /* We first load the enumerations */
    visitChildren(ctx);
    System.out.println("External events: "
        + GenericToString.printCollection(external_events.keySet()));

    System.out.println("Enumerations:" + enumerations);
    System.out.println("Variables: " + variables.keySet());
    System.out.println("Initial values: " + initial_values);

    return null;
  }

  @Override
  public Object visitBracketExpr(BracketExprContext ctx) {
    return visit(ctx.formula());
  }

  @Override
  public Object visitPair(PairContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitIdExpr(IdExprContext ctx) {
    String variable_name = ctx.ID().getText().trim();
    return getVariable(variable_name);
  }

  @Override
  public Object visitActionAssignment(ActionAssignmentContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitDomain_declaration(Domain_declarationContext ctx) {

    Enumeration enumeration = new Enumeration();
    for (TerminalNode terminal_node : ctx.list_of_ID().ID()) {
      String item = terminal_node.getText();
      enumeration.add(item);
    }

    String enumeration_name = ctx.ID().getText();
    enumerations.put(enumeration_name, enumeration);
    return enumeration;
  }

  @Override
  public Object visitOrExpr(OrExprContext ctx) {
    Formula left = (Formula) visit(ctx.formula(0));
    Formula right = (Formula) visit(ctx.formula(1));
    return new OrFormula(left, right);
  }

  @Override
  public Object visitActionEvent(ActionEventContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @return A LinkedList of DSLTransition.
   */
  @Override
  public Object visitTrans(TransContext ctx) {
    LinkedList<DSLTransition> transitions = new LinkedList<>();
    for (TransitionContext trans_context : ctx.transition()) {
      DSLTransition transition = (DSLTransition) visitTransition(trans_context);
      transitions.add(transition);
    }
    return transitions;
  }

  private boolean one_node_visited = false;

  @Override
  public Object visitNode(NodeContext ctx) {
    if (one_node_visited) {
      throw new Error("Only one node is permitted. You have a second"
          + " declaration at " + getDetails(ctx.start));
    }
    one_node_visited = true;
    visitChildren(ctx);
    return null;
  }

  /**
   * @return null
   */
  @Override
  public Object visitModel_alternatives(Model_alternativesContext ctx) {
    visitChildren(ctx);
    return null;
  }

  /**
   * @return A TRUE formula.
   */
  @Override
  public Object visitTrueExpr(TrueExprContext ctx) {
    return Formula.TRUE;
  }

  /**
   * @return A FALSE formula.
   */
  @Override
  public Object visitFalseExpr(FalseExprContext ctx) {
    return Formula.FALSE;
  }

  /**
   * Parses the declarations.
   * 
   * @return null
   */
  @Override
  public Object visitVariables_declaration(Variables_declarationContext ctx) {
    /* We let the children declare their own variables */
    visitChildren(ctx);
    return null;
  }

  /**
   * Parses the declarations.
   * 
   * @return null
   */
  @Override
  public Object visitBoolDeclaration(BoolDeclarationContext ctx) {
    /* We let the children declare their own variables */
    visitChildren(ctx);
    return null;
  }

  @Override
  public Object visitOtherDeclaration(OtherDeclarationContext ctx) {
    /* We let the children declare their own variables */
    String domain_type = ctx.ID().getText();

    Enumeration enumeration = enumerations.get(domain_type);
    if (enumeration != null) {
      for (One_other_declarationContext one_decl : ctx.one_other_declaration()) {
        visitEnumerationDeclaration(domain_type, enumeration, one_decl);
      }
    } else {
      throw new Error("The type " + domain_type + " has not been defined at "
          + getDetails(ctx.getStart()));
    }

    return null;
  }

  private void visitEnumerationDeclaration(
      String enumeration_name,
      Enumeration enumeration,
      One_other_declarationContext ctx) {
    String var_name = ctx.ID(0).getText();
    String enumeration_type = ctx.ID(1).getText();

    Variable var = getVariable(var_name);
    Byte value = enumeration.get(enumeration_type);
    if (value == null) {
      throw new Error("The enumeration type " + enumeration_type
          + " is not a type of the enumeration " + enumeration_name + " at "
          + getDetails(ctx.ID(1).getSymbol()));
    }
    /* We set the initial value of the variable */
    initial_values.put(var, value);
    /* We register this is an enumerated variable */
    enumerated_variable.put(var, enumeration);
  }

  @Override
  public Object visitOne_other_declaration(One_other_declarationContext ctx) {
    throw new NotImplementedException();
  }

  @Override
  public Object visitOne_bool_declaration(One_bool_declarationContext ctx) {
    String var_name = ctx.ID().getText();
    boolean is_true = ctx.TRUE() != null;
    Variable var = getVariable(var_name);
    Byte value;
    if (is_true) {
      value = 1;
    } else {
      value = 0;
    }
    initial_values.put(var, value);
    return null;
  }

  /**
   * @return The corresponding NotFormula.
   */
  @Override
  public Object visitNotExpr(NotExprContext ctx) {
    return new NotFormula((Formula) visit(ctx.formula()));
  }

  /**
   * Store the events in the private set `external_events`.
   * 
   * @return null
   */
  @Override
  public Object visitEvents(EventsContext ctx) {
    for (TerminalNode a : ctx.list_of_ID().ID()) {
      String event_name = a.getText();
      if (external_events.containsKey(event_name)) {
        throw new Error("Declaring an already existing event at "
            + getDetails(a.getSymbol()));
      }
      external_events.put(event_name, new ExternalEvent(event_name));
    }
    return null;
  }

  @Override
  public Object visitSub(SubContext ctx) {
    // TODO Auto-generated method stub
    /* Returns a list of transitions */
    return null;
  }

  private static final String ALL_EVENTS_IN_CONDITION = "*";

  /**
   * @return A {@link DSLTransition}.
   */
  @Override
  public Object visitTransition(TransitionContext ctx) {

    DSLTransition transition = new DSLTransition();
    // TODO Auto-generated method stub

    boolean automatic_filling = false;
    /* We first look if the event field is only a ALL_EVENTS_IN_CONDITION */
    if (ctx.list_of_ID().ID().size() == 1
        && ctx.list_of_ID().ID().get(0).equals(ALL_EVENTS_IN_CONDITION)) {
      automatic_filling = true;
    } else {
      for (TerminalNode node : ctx.list_of_ID().ID()) {
        String event_name = node.getText();
        ExternalEvent external_event = external_events.get(event_name);
        if (external_event == null) {
          throw new Error("The external event " + event_name + " at "
              + getDetails(node.getSymbol())
              + " has not been defined previously.");
        }
        transition.addExternalEvent(external_event);
      }
    }

    /* We now parse the formula */
    Formula formula = (Formula) visit(ctx.formula());
    transition.setCondition(formula);

    if (automatic_filling) {

    }
    System.out.println("Creating the transition:\n" + transition);
    return null;
  }

  @Override
  public Object visitActions(ActionsContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }
}
