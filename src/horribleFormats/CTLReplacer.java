package horribleFormats;

import graph.GraphFactoryAEFD;
import graph.Model;
import graph.StateMachine;
import graph.conditions.aefdParser.AEFDFormulaFactory;
import graph.conditions.aefdParser.GenerateFormulaAEFD;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import parserAEFDFormat.Fichier6lignes;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.conditions.EnumeratedVariable;

/**
 * Replace all the ctl found in a condition field of a transition that doesn't
 * begin from a state 0.
 * Create the different CTL graph associated to those CTL.
 * 
 */
public class CTLReplacer {
  LinkedHashMap<String, String> pairs_of_ctl = new LinkedHashMap<String, String>();

  public CTLReplacer(String file_name, String target_name, boolean isProof)
      throws IOException {
    this(file_name, target_name, isProof, null);
  }

  /**
   * rewrite in the file target_name the model from file_name with replacing all
   * the CTL found in the field condition by their IND and create the associated
   * ctl's graph if the model isn't a proof model.
   * 
   * @param file_name
   * @param target_name
   * @param isProof
   * @throws IOException
   */
  public CTLReplacer(String file_name, String target_name, boolean isProof,
      String pairs_of_variables)
      throws IOException {

    GraphFactoryAEFD graph_factory = new GraphFactoryAEFD(pairs_of_variables);
    GenerateFormulaAEFD generator =
        ((AEFDFormulaFactory) graph_factory.getFactory()).generator_of_formula;

    assert generator != null;

    Model model = graph_factory.buildModel(file_name, file_name);
    model.build();
    HashMap<EnumeratedVariable, Collection<StateMachine>> writing_state_machine =
        model.getWritingStateMachines();
    Boolean beginning = true;
    BufferedWriter writer = new BufferedWriter(new FileWriter(
        target_name));
    Fichier6lignes parser = new Fichier6lignes(file_name);
    int i = 1;
    while (parser.get6Lines()) {
      i = i + 6;
      if (!beginning) {
        writer.write("\n");
      } else {
        beginning = false;
      }
      writer.write(parser.getGraphName() + "\n"
          + parser.getSourceState() + "\n"
          + parser.getDestinationState() + "\n");

      String event = parser.getEvent();
      event = event.replaceAll("[a-zA-Z0-9_]*_non_Bloque OU", "");
      event = event.replaceAll("[a-zA-Z0-9_]*_Bloque OU", "");

      writer.write(event + " Evenement\n");

      String condition = "";
      condition = parser.getCondition();
      if (!parser.getSourceState().equals("0")) {
        condition = parser.getCondition();

        /*
         * These variables are not used during exploration, just during tests.
         */
        condition = condition.replaceAll("CMD_",
            "IND_");
        condition = condition.replaceAll("[a-zA-Z0-9_]*_non_Bloque",
            "true");
        condition = condition.replaceAll("[a-zA-Z0-9_]*_Bloque",
            "false");
        int index_CTL;
        index_CTL = condition.indexOf("CTL_");
        while (index_CTL >= 0) {
          String ctl_name;
          if (condition.indexOf(" ", index_CTL) < 0) {
            ctl_name = condition.substring(index_CTL);
          } else {
            ctl_name = condition.substring(index_CTL, condition.indexOf(
                " ", index_CTL));
          }

          while (ctl_name.trim().endsWith(")") || ctl_name.trim().endsWith("(")) {
            ctl_name = ctl_name.substring(0, ctl_name.length() - 1);
          }

          if (generator.isPositive(ctl_name)) {
            pairs_of_ctl.put(ctl_name, generator.getOppositeName(ctl_name));
          } else if (generator.isNegative(ctl_name)) {
            pairs_of_ctl.put(generator
                .getOppositeName(ctl_name),
                ctl_name);
          }
          String new_ctl_name = ctl_name.replaceAll(
              "CTL_", "IND_");

          if (writing_state_machine.containsKey(new BooleanVariable(
              new_ctl_name, -2))) {
            pairs_of_ctl.remove(ctl_name);
            pairs_of_ctl.remove(generator
                .getOppositeName(ctl_name));
          }
          condition = condition.replaceAll(ctl_name,
              new_ctl_name);
          index_CTL = condition.indexOf("CTL_", index_CTL);
        }
      }
      writer.write((condition.trim() + " Condition").trim() + "\n"
          + (parser.getAction().trim() + " Action").trim());
    }

    if (!isProof) {
      if (pairs_of_ctl.size() != 0) {
        writer.write("\n");
      }
      writer.flush();
      model = graph_factory.buildModel(target_name, file_name);
      model.build();

      Iterator<Entry<String, String>> entry_iterator = pairs_of_ctl
          .entrySet()
          .iterator();

      HashSet<String> variable_already_written = new HashSet<String>();
      boolean first = true;
      while (entry_iterator.hasNext()) {
        Entry<String, String> entry = entry_iterator.next();
        if (!writing_state_machine.containsKey(model
            .getVariable(entry.getKey().replaceAll("CTL_", "IND_")))) {
          variable_already_written.add(entry.getKey());
          if (first) {
            first = false;
          } else {
            writer.write("\n");
          }

          String result = graph_factory.generateAutomateForCTL(entry
              .getKey(),
              entry.getValue());
          writer.write(result);
        }
      }

      writer.flush();
      model = graph_factory.buildModel(target_name, file_name);
      model.build();

      Iterator<EnumeratedVariable> variable_iterator = model
          .iteratorExistingVariables();
      while (variable_iterator.hasNext()) {
        BooleanVariable variable = (BooleanVariable) variable_iterator.next();
        if (!writing_state_machine.containsKey(variable)
            && !variable_already_written.contains(variable.getVarname())
            && !variable.getVarname().startsWith("CTL_")) {
          if (first) {
            first = false;
          } else {
            writer.write("\n");
          }

          writer.write(
              graph_factory
                  .generateAutomateForCTL(
                      variable.getVarname().replaceAll("IND_", "CTL_"),
                      generator
                          .getOppositeName(variable.getVarname())
                          .replaceAll("IND_", "CTL_")));
        }
      }
    }
    writer.close();
  }
}
