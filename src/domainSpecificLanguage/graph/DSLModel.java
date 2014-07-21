package domainSpecificLanguage.graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utils.GenericToString;
import abstractGraph.AbstractModel;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.conditions.Enumeration;
import abstractGraph.events.CommandEvent;
import abstractGraph.events.ExternalEvent;
import abstractGraph.events.InternalEvent;
import abstractGraph.events.ModelCheckerEvent;
import abstractGraph.events.SingleEvent;
import abstractGraph.events.SynchronisationEvent;
import domainSpecificLanguage.Template;
import domainSpecificLanguage.DSLGlobalState.DSLGlobalState;

public class DSLModel extends
    AbstractModel<DSLStateMachine, DSLState, DSLTransition> {

  public Set<Enumeration> enumerations = new HashSet<>();
  // public Map<EnumeratedVariable, Enumeration> enumerated_variable = new
  // HashMap<>();
  public Set<EnumeratedVariable> variables = new HashSet<>();
  public Map<EnumeratedVariable, Byte> initial_values = new HashMap<>();

  public Set<ExternalEvent> external_events = new HashSet<>();
  public Set<InternalEvent> internal_events = new HashSet<>();
  public Set<CommandEvent> command_events = new HashSet<>();
  public Set<Template> templates = new HashSet<>();
  public Set<DSLStateMachine> state_machines = new HashSet<>();

  public DSLModel(String model_name) {
    super(model_name);
  }

  public DSLModel() {
    this("Default Name");
  }

  public DSLModel(Collection<Enumeration> enumerations,
      Collection<Template> templates,
      Collection<DSLStateMachine> state_machines) {
    this();
    this.enumerations.addAll(enumerations);
    this.templates.addAll(templates);
    this.state_machines.addAll(state_machines);
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

  /**
   * 
   * @param identation
   *          Indentation
   * @return
   */
  private String variablesToString(String identation) {
    StringBuffer string_buffer = new StringBuffer();
    for (EnumeratedVariable variable : variables) {
      byte initial_value = initial_values.get(variable);

      string_buffer.append(identation);
      if (variable instanceof BooleanVariable) {
        string_buffer.append("bool " + variable + "("
            + BooleanVariable.getStringFromByte(initial_value) + ");\n");
      } else if (variable instanceof EnumeratedVariable) {

        Enumeration enumeration = variable.getEnumeration();
        assert (enumeration != null);
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
    return toString(false);
  }

  public String toString(boolean is_proof) {
    StringBuilder string_builder = new StringBuilder();

    if (enumerations.size() != 0) {
      string_builder
          .append(GenericToString.printCollection(enumerations) + "\n");
    }
    if (variables.size() != 0) {
      String beginning;
      if (is_proof) {
        beginning = "proof variables";
      } else {
        beginning = "variables";
      }
      string_builder
          .append(beginning + "\n" + variablesToString("  ") + "end\n\n");
    }
    if (external_events.size() != 0) {
      string_builder.append("external_events\n  " +
          GenericToString.printCollection(external_events) + ";\nend\n\n");
    }
    if (command_events.size() != 0) {
      string_builder.append("command_events\n  " +
          GenericToString.printCollection(command_events) + ";\nend\n\n");
    }
    if (internal_events.size() != 0) {
      String beginning;
      if (is_proof) {
        beginning = "proof internal_events";
      } else {
        beginning = "internal_events";
      }
      string_builder.append(beginning + "\n  " +
          GenericToString.printCollection(internal_events) + ";\nend\n\n");
    }

    if (state_machines.size() != 0) {
      for (DSLStateMachine state_machine : state_machines) {
        string_builder.append(state_machine + "\n");
      }
      string_builder.append("\n");
    }

    return string_builder.toString();
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
    for (DSLStateMachine state_machine : state_machines) {
      for (DSLState state : state_machine) {
        for (DSLTransition transition : state) {
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
      }
    }
    return true;
  }

  @Override
  public void addStateMachine(DSLStateMachine state_machine) {
    state_machines.add(state_machine);
  }

  @Override
  public void build() {
    // TODO Auto-generated method stub
    throw new NotImplementedException();
  }

  @Override
  public Iterator<DSLStateMachine> iterator() {
    return state_machines.iterator();
  }

  @Override
  public EnumeratedVariable getVariable(String variable_name) {
    throw new NotImplementedException();
  }

  @Override
  public Collection<EnumeratedVariable> getExistingVariables() {
    return variables;
  }

  @Override
  public boolean containsSynchronisationEvent(SynchronisationEvent event) {
    return internal_events.contains(event);
  }

  @Override
  public boolean containsSynchronousEvent(String synchronous_event) {
    throw new NotImplementedException();
  }

  @Override
  public boolean containsVariable(EnumeratedVariable variable) {
    return variables.contains(variable);
  }

  @Override
  public boolean containsExternalEvent(ExternalEvent external_event) {
    return external_events.contains(external_event);
  }

  @Override
  public HashMap<EnumeratedVariable, LinkedList<DSLStateMachine>> getWritingStateMachines() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Used in the Graphical Simulator to load a list of external events from a
   * file.
   * 
   * @param file_name
   * @return
   */
  public LinkedList<ExternalEvent> loadScenario(String file_name)
      throws IOException {

    BufferedReader buff = new BufferedReader(new FileReader(file_name));
    LinkedList<ExternalEvent> result = new LinkedList<>();

    String line;
    while ((line = buff.readLine()) != null) {
      ExternalEvent read_event = null;
      for (ExternalEvent event : external_events) {
        if (event.toString().equals(line.trim())) {
          read_event = event;
          break;
        }
      }

      if (read_event == null) {
        buff.close();
        throw new Error("The event " + line + " does not exist in the model");
      }
      result.add(read_event);
    }

    buff.close();
    return result;
  }

}
