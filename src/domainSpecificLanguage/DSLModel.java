package domainSpecificLanguage;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import utils.GenericToString;
import utils.Pair;
import abstractGraph.events.CommandEvent;
import abstractGraph.events.ExternalEvent;
import abstractGraph.events.InternalEvent;
import domainSpecificLanguage.DSLValuation.Enumeration;
import domainSpecificLanguage.graph.DSLTransition;
import domainSpecificLanguage.graph.DSLVariable;

public class DSLModel {

  public Set<Enumeration> enumerations = new HashSet<>();
  public Map<DSLVariable, Enumeration> enumerated_variable = new HashMap<>();
  public Set<Pair<DSLVariable, Byte>> variables = new HashSet<>();
  public Set<ExternalEvent> external_events = new HashSet<>();
  public Set<InternalEvent> internal_events = new HashSet<>();
  public Set<CommandEvent> command_events = new HashSet<>();
  public Set<Template> templates = new HashSet<>();
  public Set<DSLTransition> transitions = new HashSet<>();

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
    for (Pair<DSLVariable, Byte> pair : variables) {
      DSLVariable variable = pair.first;
      byte initial_value = pair.second;
      Enumeration enumeration = enumerated_variable.get(variable);

      string_buffer.append(identation);
      if (enumeration == null) {
        string_buffer.append("bool " + variable + "("
            + Enumeration.getBool(initial_value) + ");\n");
      } else {
        string_buffer.append(enumeration.getName() + " " + variable +
            "(" + enumeration.getOption(initial_value) + ");\n");
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
}
