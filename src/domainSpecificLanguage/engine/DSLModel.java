package domainSpecificLanguage.engine;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import utils.GenericToString;
import utils.Pair;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.conditions.Enumeration;
import abstractGraph.events.CommandEvent;
import abstractGraph.events.ExternalEvent;
import abstractGraph.events.InternalEvent;
import abstractGraph.events.ModelCheckerEvent;
import abstractGraph.events.SingleEvent;
import abstractGraph.events.SynchronisationEvent;
import abstractGraph.events.VariableChange;
import domainSpecificLanguage.Template;
import domainSpecificLanguage.DSLValuation.CompactValuation;
import domainSpecificLanguage.graph.DSLTransition;

public class DSLModel implements DSLSimulatorInterface<CompactValuation> {

  public Set<Enumeration> enumerations = new HashSet<>();
  public Map<EnumeratedVariable, Enumeration> enumerated_variable = new HashMap<>();
  public Set<Pair<EnumeratedVariable, Byte>> variables = new HashSet<>();
  public Set<ExternalEvent> external_events = new HashSet<>();
  public Set<InternalEvent> internal_events = new HashSet<>();
  public Set<CommandEvent> command_events = new HashSet<>();
  public Set<Template> templates = new HashSet<>();
  public Set<DSLTransition> transitions = new HashSet<>();

  public Set<DSLTransition> proof_transitions = new HashSet<>();

  public DSLModel() {
  }

  public DSLModel(Collection<Enumeration> enumerations,
      Collection<Template> templates,
      Collection<DSLTransition> transitions) {
    this.enumerations.addAll(enumerations);
    this.templates.addAll(templates);
    this.transitions.addAll(transitions);
  }

  public void addEnumeration(Enumeration enumeration) {
    enumerations.add(enumeration);
  }

  public void addAllEnumeration(Collection<Enumeration> enumerations) {
    for (Enumeration enumeration : enumerations) {
      addEnumeration(enumeration);
    }
  }

  public void addTemplate(Template template) {
    templates.add(template);
  }

  public void addTransition(DSLTransition node) {
    transitions.add(node);
  }

  /**
   * 
   * @param identation
   *          Indentation
   * @return
   */
  private String variablesToString(String identation) {
    StringBuffer string_buffer = new StringBuffer();
    for (Pair<EnumeratedVariable, Byte> pair : variables) {
      EnumeratedVariable variable = pair.first;
      byte initial_value = pair.second;
      Enumeration enumeration = enumerated_variable.get(variable);

      string_buffer.append(identation);
      if (variable instanceof BooleanVariable) {
        string_buffer.append("bool " + variable + "("
            + BooleanVariable.getStringFromByte(initial_value) + ");\n");
      } else if (variable instanceof EnumeratedVariable) {
        string_buffer.append(enumeration.getName() + " " + variable +
            "(" + enumeration.getOption(initial_value) + ");\n");
      } else {
        throw new Error();
      }
    }
    return string_buffer.toString();
  }

  @Override
  public String toString() {
    StringBuffer string_buffer = new StringBuffer();

    if (enumerations.size() != 0) {
      string_buffer
          .append(GenericToString.printCollection(enumerations) + "\n");
    }
    if (variables.size() != 0) {
      string_buffer.append("variables\n" + variablesToString("  ") + "end\n\n");
    }
    if (external_events.size() != 0) {
      string_buffer.append("external_events\n  " +
          GenericToString.printCollection(external_events) + ";\nend\n\n");
    }
    if (command_events.size() != 0) {
      string_buffer.append("command_events\n  " +
          GenericToString.printCollection(command_events) + ";\nend\n\n");
    }
    if (internal_events.size() != 0) {
      string_buffer.append("internal_events\n  " +
          GenericToString.printCollection(internal_events) + ";\nend\n\n");
    }

    if (transitions.size() != 0) {
      string_buffer.append("trans\n  " +
          GenericToString.printCollection(transitions, "\n  ") + "\nend\n\n");
    }
    return string_buffer.toString();
  }

  @Override
  public CompactValuation execute(CompactValuation starting_state,
      ExternalEvent e) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public LinkedHashSet<ExternalEvent> getPossibleEvent(
      CompactValuation global_state) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Check that the functional model and the proof model can be executed
   * simultaneously.
   * 
   * @details
   *          If verifies that:
   *          <ol>
   *          <li>
   *          1) the proof model does not write any variable that belongs to the
   *          functional model.</li>
   *          <li>
   *          2) the proof model does not write any synchronization event that
   *          is used (i.e. written or listened) by the functional model.</li>
   *          <li>
   *          3) the proof model does not write ExternalCommands</li>
   *          <li>
   *          4) the functional model does not write ModelCheckerEvents</li>
   *          </ol>
   * @return true if the models are ok
   */
  private boolean checkCompatibility() {

    /* Verification of 4) */
    for (DSLTransition transition : transitions) {
      for (SingleEvent event : transition.getActions()) {
        /* Verification of 4) */
        if (event instanceof ModelCheckerEvent) {
          System.err.println(
              "The functional model does write a variable that is reserved"
                  + " to proof models: " + event);
          return false;
        }
      }
    }

    return true;
  }
}
