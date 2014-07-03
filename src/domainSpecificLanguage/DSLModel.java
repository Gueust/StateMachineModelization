package domainSpecificLanguage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import domainSpecificLanguage.graph.DSLTransition;

public class DSLModel {

  protected Set<Enumeration> enumerations = new HashSet<>();
  protected Set<Template> templates = new HashSet<>();
  protected Set<DSLTransition> transitions = new HashSet<>();

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

  public void addTemplate(Template template) {
    templates.add(template);
  }

  public void addTransition(DSLTransition node) {
    transitions.add(node);
  }

  @Override
  public String toString() {
    return "DSLModel [enumerations=" + enumerations + ", templates="
        + templates + ", transitions=" + transitions + "]";
  }

}
