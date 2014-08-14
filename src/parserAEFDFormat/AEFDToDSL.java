package parserAEFDFormat;

import java.io.IOException;
import java.util.LinkedHashSet;

import domainSpecificLanguage.graph.DSLModel;
import domainSpecificLanguage.graph.DSLState;
import domainSpecificLanguage.graph.DSLStateMachine;
import domainSpecificLanguage.graph.DSLTransition;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.conditions.cnf.Literal;
import abstractGraph.events.Assignment;
import abstractGraph.events.CommandEvent;
import abstractGraph.events.ExternalEvent;
import abstractGraph.events.InternalEvent;
import abstractGraph.events.SingleEvent;
import abstractGraph.events.VariableChange;
import graph.GraphFactoryAEFD;
import graph.Model;
import graph.State;
import graph.StateMachine;
import graph.Transition;

public class AEFDToDSL {

  static public String translate(String functional_file, String proof_file)
      throws IOException {

    StringBuilder string_builder = new StringBuilder();

    /* We load the model */
    GraphFactoryAEFD graph_factory = new GraphFactoryAEFD(null);

    Model model = graph_factory.buildModel(functional_file, functional_file);
    model.build();

    DSLModel dsl_model = AEFDModelToDSLModel(model);

    string_builder.append(dsl_model.toString());

    Model proof = null;
    if (proof_file != null) {
      proof = graph_factory.buildModel(proof_file, proof_file);
      proof.build();
      DSLModel dsl_proof_model = AEFDModelToDSLModel(proof);

      /* We remove the internal events that belong to the functional model */
      for (SingleEvent e : model.getSynchronisation_events().values()) {
        dsl_proof_model.internal_events.remove(e);
      }
      for (EnumeratedVariable v : model.getExistingVariables()) {
        dsl_proof_model.variables.remove(v);
      }

      string_builder.append(dsl_proof_model.toString(true));
    }

    return string_builder.toString();
  }

  static private DSLModel AEFDModelToDSLModel(Model model) {
    DSLModel dsl_model = new DSLModel();

    dsl_model.external_events =
        new LinkedHashSet<ExternalEvent>(model.external_events.values());
    dsl_model.internal_events =
        new LinkedHashSet<InternalEvent>(model
            .getSynchronisation_events()
            .values());
    dsl_model.variables =
        new LinkedHashSet<EnumeratedVariable>(model.getExistingVariables());

    for (EnumeratedVariable v : dsl_model.variables) {
      dsl_model.initial_values.put(v, new Byte((byte) 0));
    }
    dsl_model.command_events =
        new LinkedHashSet<CommandEvent>(model.getCommands_events().values());

    for (StateMachine machine : model) {
      DSLStateMachine dsl_machine = new DSLStateMachine(machine.getName(), -1);
      dsl_model.addStateMachine(dsl_machine);
      for (State state : machine) {
        for (Transition transition : state) {

          String source = transition.getSource().getId();
          String destination = transition.getDestination().getId();

          DSLState dsl_state_from = dsl_machine.getState(source);
          if (dsl_state_from == null) {
            dsl_state_from = dsl_machine.addState(source);
          }
          DSLState dsl_state_to = dsl_machine.getState(destination);
          if (dsl_state_to == null) {
            dsl_state_to = dsl_machine.addState(destination);
          }

          DSLTransition dsl_transition =
              new DSLTransition(dsl_state_from, dsl_state_to);

          for (SingleEvent e : transition.getEvents().getCollection()) {
            if (e instanceof VariableChange) {
              dsl_transition.addSingleEvent(
                  new VariableChange(
                      new Literal(((VariableChange) e).getModifiedVariable())));
            } else {
              dsl_transition.addSingleEvent(e);
            }
          }

          dsl_transition.setCondition(transition.getCondition());

          for (SingleEvent e : transition.getActions().getCollection()) {
            if (e instanceof VariableChange) {
              byte value;
              if (((VariableChange) e).isNegated()) {
                value = BooleanVariable.FALSE;
              } else {
                value = BooleanVariable.TRUE;
              }
              dsl_transition.addAction(
                  new Assignment(((VariableChange) e).getModifiedVariable(),
                      value));
            } else {
              dsl_transition.addAction(e);
            }
          }
        }
        dsl_machine.setInitial_state(dsl_machine.getState("0"));
      }
    }
    return dsl_model;
  }
}
