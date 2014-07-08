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
import utils.Pair;
import abstractGraph.conditions.AndFormula;
import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.conditions.Enumeration;
import abstractGraph.conditions.EnumerationEqualityFormula;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.NotFormula;
import abstractGraph.conditions.OrFormula;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.events.Assignment;
import abstractGraph.events.CommandEvent;
import abstractGraph.events.ExternalEvent;
import abstractGraph.events.InternalEvent;
import abstractGraph.events.SingleEvent;
import domainSpecificLanguage.engine.DSLModel;
import domainSpecificLanguage.graph.DSLTransition;
import domainSpecificLanguage.graph.DSLVariableEvent;
import domainSpecificLanguage.parser.FSM_LanguageParser.ActionAssignmentContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.ActionContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.ActionEventContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.ActionsContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.AndExprContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.BoolDeclarationContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.BracketExprContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.Commands_declarationContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.Domain_declarationContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.EnumerationEqualityExprContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.External_eventsContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.FalseExprContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.IdExprContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.Internal_eventsContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.List_of_IDContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.ModelContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.Model_alternativesContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.NotExprContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.One_bool_declarationContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.One_other_declarationContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.OrExprContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.OtherDeclarationContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.PairContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.ProofBoolDeclarationContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.ProofOtherDeclarationContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.Proof_variables_declarationContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.SubContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.TemplateContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.TransitionContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.TransitionsContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.TrueExprContext;
import domainSpecificLanguage.parser.FSM_LanguageParser.Variables_declarationContext;

public class FSM_builder extends AbstractParseTreeVisitor<Object>
    implements FSM_LanguageVisitor<Object> {

  Map<String, EnumeratedVariable> variables = new HashMap<>();
  Map<String, EnumeratedVariable> proof_variables = new HashMap<>();
  Map<EnumeratedVariable, Byte> initial_values = new HashMap<>();
  Map<String, ExternalEvent> external_events = new HashMap<>();
  Map<String, InternalEvent> internal_events = new HashMap<>();
  Map<String, CommandEvent> commands_event = new HashMap<>();
  Map<String, SingleEvent> single_events = new HashMap<>();
  Map<String, Enumeration> enumerations = new HashMap<>();
  Map<EnumeratedVariable, Enumeration> enumerated_DSLVariable = new HashMap<>();

  Set<DSLTransition> transitions = new HashSet<>();

  private void clear() {
    variables.clear();
    initial_values.clear();
    external_events.clear();
    internal_events.clear();
    commands_event.clear();
    single_events.clear();
    enumerations.clear();
    enumerated_DSLVariable.clear();
    transitions.clear();
  }

  private void raiseError(String error) {
    System.err.println(error);
    System.err.println("Aborting.");
    System.exit(-1);
  }

  /**
   * 
   * @param name
   * @param token
   * @return A not null DSLVariable.
   */
  private EnumeratedVariable getDSLVariable(String name, Token token) {
    if (external_events.containsKey(name)) {
      raiseError("The DSLVariable defined at " + getDetails(token)
          + " has already been defined as an external event.");
    } else if (internal_events.containsKey(name)) {
      raiseError("The DSLVariable defined at " + getDetails(token)
          + " has already been defined as an internal event.");
    } else if (commands_event.containsKey(name)) {
      raiseError("The external event defined at " + getDetails(token)
          + " has already been defined as a command.");
    }

    EnumeratedVariable var = variables.get(name);
    if (var == null) {
      raiseError("The variable used at " + getDetails(token)
          + " has not been defined previously.");
    }
    return var;
  }

  private EnumeratedVariable createDSLVariable(String name, Token token,
      Enumeration enumeration) {
    if (variables.containsKey(name)) {
      throw new Error("The variable declared at  " + getDetails(token)
          + " already exists.");
    }
    if (proof_variables.containsKey(name)) {
      throw new Error("The functional variable declared at  "
          + getDetails(token)
          + " already exists in the proof model.");
    }
    EnumeratedVariable variable;
    int unique_id = variables.size();
    if (enumeration == null) {
      variable = new BooleanVariable(name, unique_id);
    } else {
      variable = new EnumeratedVariable(name, unique_id, enumeration);
    }
    variables.put(name, variable);
    single_events.put(name, new DSLVariableEvent(variable));

    return variable;
  }

  private EnumeratedVariable createProofDSLVariable(String name, Token token,
      Enumeration enumeration) {
    if (variables.containsKey(name)) {
      throw new Error("The proof variable declared at  " + getDetails(token)
          + " already exists in the functional model.");
    }
    if (proof_variables.containsKey(name)) {
      throw new Error("The proof variable declared at  " + getDetails(token)
          + " already exists.");
    }
    EnumeratedVariable variable = new EnumeratedVariable(name,
        variables.size(),
        enumeration);
    proof_variables.put(name, variable);
    single_events.put(name, new DSLVariableEvent(variable));

    return variable;
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
        raiseError("The identifier " + item + " is present twice at "
            + getDetails(ctx.getStart()));
      }
      result.add(item);
    }
    return result;
  }

  @Override
  public Object visitModel(ModelContext ctx) {
    // TODO Auto-generated method stub
    clear();

    DSLModel dsl_model = new DSLModel();

    /* The parsing does fill in the internal values of the current instance */
    visitChildren(ctx);

    /* Then we use them to fill in the model */
    for (EnumeratedVariable DSLVariable : variables.values()) {
      Byte initial_value = initial_values
          .get(DSLVariable);
      assert (initial_value != null);
      dsl_model.variables
          .add(new Pair<EnumeratedVariable, Byte>(DSLVariable, initial_value));
    }

    dsl_model.external_events.addAll(external_events.values());
    dsl_model.internal_events.addAll(internal_events.values());
    dsl_model.command_events.addAll(commands_event.values());
    dsl_model.enumerations.addAll(enumerations.values());
    dsl_model.enumerated_variable.putAll(enumerated_DSLVariable);
    dsl_model.transitions.addAll(transitions);

    return dsl_model;
  }

  @Override
  public Object visitPair(PairContext ctx) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object visitDomain_declaration(Domain_declarationContext ctx) {

    String enumeration_name = ctx.ID().getText();

    Enumeration enumeration = new Enumeration(enumeration_name);
    for (TerminalNode terminal_node : ctx.list_of_ID().ID()) {
      String item = terminal_node.getText();
      enumeration.add(item);
    }

    enumerations.put(enumeration_name, enumeration);
    return enumeration;
  }

  /**
   * @return A LinkedList of DSLTransition.
   */
  @Override
  public Object visitTransitions(TransitionsContext ctx) {
    LinkedList<DSLTransition> transitions = new LinkedList<>();
    for (TransitionContext trans_context : ctx.transition()) {
      DSLTransition transition = (DSLTransition) visitTransition(trans_context);
      assert (transition != null);
      transitions.add(transition);
    }
    return transitions;
  }

  /**
   * @return null
   */
  @Override
  public Object visitModel_alternatives(Model_alternativesContext ctx) {
    visitChildren(ctx);
    return null;
  }

  @Override
  public Object visitBracketExpr(BracketExprContext ctx) {
    return visit(ctx.formula());
  }

  @Override
  public Object visitIdExpr(IdExprContext ctx) {
    String DSLVariable_name = ctx.ID().getText().trim();
    return getDSLVariable(DSLVariable_name, ctx.start);
  }

  /**
   * @return The associated AndFormula.
   */
  @Override
  public Object visitAndExpr(AndExprContext ctx) {
    Formula left = (Formula) visit(ctx.formula(0));
    Formula right = (Formula) visit(ctx.formula(1));
    return new AndFormula(left, right);
  }

  /**
   * @return The associated OrFormula.
   */
  @Override
  public Object visitOrExpr(OrExprContext ctx) {
    Formula left = (Formula) visit(ctx.formula(0));
    Formula right = (Formula) visit(ctx.formula(1));
    return new OrFormula(left, right);
  }

  /**
   * @return The corresponding NotFormula.
   */
  @Override
  public Object visitNotExpr(NotExprContext ctx) {
    return new NotFormula((Formula) visit(ctx.formula()));
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

  @Override
  public Object visitEnumerationEqualityExpr(EnumerationEqualityExprContext ctx) {
    /**
     * @return The associated EnumerationEqualityFormula.
     */
    String variable_name = ctx.ID(0).getText();
    String variable_value = ctx.ID(1).getText();

    boolean is_not;
    switch (ctx.getChild(1).getText()) {
    case "is":
      is_not = false;
      break;
    case "is not":
      is_not = true;
      break;
    default:
      throw new Error("Impossible case.");
    }
    EnumeratedVariable variable = getDSLVariable(variable_name, ctx
        .ID(0)
        .getSymbol());
    byte value = variable.getEnumeration().getByte(variable_value);
    return new EnumerationEqualityFormula(variable, value, is_not);
  }

  /**
   * Parses the declarations.
   * 
   * @return null
   */
  @Override
  public Object visitVariables_declaration(
      Variables_declarationContext ctx) {
    /* We let the children declare their own DSLVariables */
    visitChildren(ctx);
    return null;
  }

  @Override
  public Object visitProof_variables_declaration(
      Proof_variables_declarationContext ctx) {

    visitChildren(ctx);
    return null;
  }

  @Override
  public Object visitProofBoolDeclaration(ProofBoolDeclarationContext ctx) {
    for (One_bool_declarationContext bool_declaration : ctx
        .one_bool_declaration()) {
      String var_name = bool_declaration.ID().getText();
      boolean is_true = bool_declaration.TRUE() != null;
      EnumeratedVariable var = createProofDSLVariable(var_name, ctx.start, null);
      Byte value;
      if (is_true) {
        value = 1;
      } else {
        value = 0;
      }
      initial_values.put(var, value);
    }
    return null;
  }

  @Override
  public Object visitProofOtherDeclaration(ProofOtherDeclarationContext ctx) {
    /* We let the children declare their own DSLVariables */
    String domain_type = ctx.ID().getText();

    Enumeration enumeration = enumerations.get(domain_type);
    if (enumeration != null) {
      for (One_other_declarationContext one_decl : ctx.one_other_declaration()) {

        String var_name = one_decl.ID(0).getText();
        String enumeration_type = one_decl.ID(1).getText();

        EnumeratedVariable var = createProofDSLVariable(var_name,
            one_decl.start,
            enumeration);

        Byte value = enumeration.getByte(enumeration_type);
        if (value == null) {
          raiseError("The enumeration type " + enumeration_type
              + " is not a type of the enumeration " + domain_type
              + " at "
              + getDetails(one_decl.ID(1).getSymbol()));
        }
        /* We set the initial value of the DSLVariable */
        initial_values.put(var, value);
        /* We register this is an enumerated DSLVariable */
        enumerated_DSLVariable.put(var, enumeration);

      }
    } else {
      raiseError("The type " + domain_type + " has not been defined at "
          + getDetails(ctx.getStart()));
    }
    return null;
  }

  /**
   * Parses the declarations.
   * 
   * @return null
   */
  @Override
  public Object visitBoolDeclaration(BoolDeclarationContext ctx) {
    /* We let the children declare their own DSLVariables */
    visitChildren(ctx);
    return null;
  }

  @Override
  public Object visitOtherDeclaration(OtherDeclarationContext ctx) {
    /* We let the children declare their own DSLVariables */
    String domain_type = ctx.ID().getText();

    Enumeration enumeration = enumerations.get(domain_type);
    if (enumeration != null) {
      for (One_other_declarationContext one_decl : ctx.one_other_declaration()) {
        visitEnumerationDeclaration(domain_type, enumeration, one_decl);
      }
    } else {
      raiseError("The type " + domain_type + " has not been defined at "
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

    EnumeratedVariable var = createDSLVariable(var_name, ctx.start, enumeration);

    Byte value = enumeration.getByte(enumeration_type);
    if (value == null) {
      raiseError("The enumeration type " + enumeration_type
          + " is not a type of the enumeration " + enumeration_name + " at "
          + getDetails(ctx.ID(1).getSymbol()));
    }
    /* We set the initial value of the DSLVariable */
    initial_values.put(var, value);
    /* We register this is an enumerated DSLVariable */
    enumerated_DSLVariable.put(var, enumeration);
  }

  /*
   * This function is not called since its parents parses the enumeration
   * declarations
   */
  @Override
  public Object visitOne_other_declaration(One_other_declarationContext ctx) {
    throw new NotImplementedException();
  }

  @Override
  public Object visitOne_bool_declaration(One_bool_declarationContext ctx) {
    String var_name = ctx.ID().getText();
    boolean is_true = ctx.TRUE() != null;
    EnumeratedVariable var = createDSLVariable(var_name, ctx.start, null);

    initial_values.put(var, BooleanVariable.getByteFromBool(is_true));
    return null;
  }

  /**
   * Store the events in the private set `external_events`.
   * 
   * @return null
   */
  @Override
  public Object visitExternal_events(External_eventsContext ctx) {
    if (ctx.list_of_ID() == null) {
      return null;
    }
    for (List_of_IDContext internal_list : ctx.list_of_ID()) {
      for (TerminalNode terminal_node : internal_list.ID()) {
        String event_name = terminal_node.getText();
        if (external_events.containsKey(event_name)) {
          raiseError("Declaring an already existing event at "
              + getDetails(terminal_node.getSymbol()));
        }

        Token token = terminal_node.getSymbol();
        if (internal_events.containsKey(event_name)) {
          raiseError("The external event defined at " + getDetails(token)
              + " has already been defined as an internal event.");
        } else if (external_events.containsKey(event_name)) {
          raiseError("The external event defined at " + getDetails(token)
              + " has already been defined.");
        } else if (variables.containsKey(event_name)) {
          raiseError("The external event defined at " + getDetails(token)
              + " has already been defined as a DSLVariable.");
        } else if (commands_event.containsKey(event_name)) {
          raiseError("The external event defined at " + getDetails(token)
              + " has already been defined as a command.");
        }

        ExternalEvent event = new ExternalEvent(event_name);
        external_events.put(event_name, event);
        single_events.put(event_name, event);
      }
    }
    return null;
  }

  @Override
  public Object visitInternal_events(Internal_eventsContext ctx) {
    if (ctx.list_of_ID() == null) {
      return null;
    }
    for (List_of_IDContext internal_list : ctx.list_of_ID()) {
      for (TerminalNode terminal_node : internal_list.ID()) {
        String event_name = terminal_node.getText();
        if (internal_events.containsKey(event_name)) {
          raiseError("Declaring an already existing event at "
              + getDetails(terminal_node.getSymbol()));
        }

        Token token = terminal_node.getSymbol();

        if (internal_events.containsKey(event_name)) {
          raiseError("The internal event defined at " + getDetails(token)
              + " has already been defined as an internal event.");
        } else if (external_events.containsKey(event_name)) {
          raiseError("The internal event defined at " + getDetails(token)
              + " has already been defined as an external event.");
        } else if (variables.containsKey(event_name)) {
          raiseError("The internal event defined at " + getDetails(token)
              + " has already been defined as a DSLVariable.");
        } else if (commands_event.containsKey(event_name)) {
          raiseError("The external event defined at " + getDetails(token)
              + " has already been defined as a command.");
        }

        InternalEvent event = new InternalEvent(event_name);
        internal_events.put(event_name, event);
        single_events.put(event_name, event);

      }
    }
    return null;
  }

  @Override
  public Object visitCommands_declaration(Commands_declarationContext ctx) {
    if (ctx.list_of_ID() == null) {
      return null;
    }
    for (List_of_IDContext internal_list : ctx.list_of_ID()) {
      for (TerminalNode terminal_node : internal_list.ID()) {
        String event_name = terminal_node.getText();
        if (commands_event.containsKey(event_name)) {
          raiseError("Declaring an already existing event at "
              + getDetails(terminal_node.getSymbol()));
        }

        Token token = terminal_node.getSymbol();

        if (internal_events.containsKey(event_name)) {
          raiseError("The command event defined at " + getDetails(token)
              + " has already been defined as an internal event.");
        } else if (external_events.containsKey(event_name)) {
          raiseError("The command event defined at " + getDetails(token)
              + " has already been defined as an external event.");
        } else if (variables.containsKey(event_name)) {
          raiseError("The command event defined at " + getDetails(token)
              + " has already been defined as a DSLVariable.");
        } else if (commands_event.containsKey(event_name)) {
          raiseError("The command event defined at " + getDetails(token)
              + " has already been defined.");
        }

        CommandEvent event = new CommandEvent(event_name);
        commands_event.put(event_name, event);
        single_events.put(event_name, event);

      }
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
          raiseError("The external event " + event_name + " at "
              + getDetails(node.getSymbol())
              + " has not been defined previously.");
        }
        transition.addSingleEvent(external_event);
      }
    }

    /* We now parse the formula */
    Formula formula = (Formula) visit(ctx.formula());
    transition.setCondition(formula);

    if (automatic_filling) {
      HashSet<EnumeratedVariable> all_DSLVariables = new HashSet<>();
      formula.allVariables(all_DSLVariables);
      for (EnumeratedVariable variable : all_DSLVariables) {
        transition.addSingleEvent(single_events.get(variable.getVarname()));
        ;
      }
    }

    /* Then we do the actions */
    ActionsContext parsed_actions = ctx.actions();

    for (ActionContext child : parsed_actions.action()) {
      if (child instanceof ActionEventContext) {
        SingleEvent action =
            (SingleEvent) visitActionEvent((ActionEventContext) child);
        transition.addAction(action);
      } else if (child instanceof ActionAssignmentContext) {
        Assignment action =
            (Assignment) visitActionAssignment((ActionAssignmentContext) child);
        transition.addAction(action);
      } else {
        throw new Error("Unknown error during parsing.");
      }
    }

    transitions.add(transition);

    return transition;
  }

  /**
   * Does nothing. Should not be used.
   */
  @Override
  public Object visitActions(ActionsContext ctx) {
    return null;
  }

  /**
   * @return A {@link SingleEvent}.
   */
  @Override
  public Object visitActionEvent(ActionEventContext ctx) {
    String event_name = ctx.getText();
    SingleEvent event = single_events.get(event_name);
    if (event == null) {
      raiseError("The event declared in the action field at "
          + getDetails(ctx.start) + " is not definedd");
    }
    if (event instanceof ExternalEvent) {
      raiseError("The event declared in the action field at "
          + getDetails(ctx.start)
          + " is an internal event and so cannot be used as an action.");
    }
    return event;
  }

  /**
   * @return A {@link Assignment}.
   */
  @Override
  public Object visitActionAssignment(ActionAssignmentContext ctx) {
    String variable_name = ctx.ID(0).getText();
    String variable_value = ctx.ID(1).getText();

    EnumeratedVariable DSLVariable = variables.get(variable_name);
    if (DSLVariable == null) {
      raiseError("The DSLVariable " + DSLVariable + " defined at "
          + getDetails(ctx.ID(0).getSymbol())
          + " has not been previously defined.");
    }

    Enumeration enumeration = enumerated_DSLVariable.get(DSLVariable);
    Byte value;
    if (enumeration == null) {
      /* This is a boolean value */
      value = BooleanVariable.getByteFromString(variable_value);
    } else {
      /* This is an enumeration value */
      value = enumeration.getByte(variable_value);
      if (value == null) {
        throw new Error("[" + getDetails(ctx.ID(1).getSymbol()) + "]"
            + "The option " + variable_value
            + " has not been found in the enumeration " + enumeration);
      }
    }
    if (value == null) {
      throw new Error("Null value");
    }
    return new Assignment(DSLVariable, value);
  }

}
