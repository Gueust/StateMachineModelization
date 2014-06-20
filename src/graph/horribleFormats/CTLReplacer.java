package graph.horribleFormats;

import graph.GraphFactoryAEFD;
import graph.conditions.aefdParser.GenerateFormulaAEFD;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import parserAEFDFormat.Fichier6lignes;

/**
 * Replace all the ctl found in a condition field of a transition that doesn't
 * begin from a state 0.
 * Create the different CTL graph associated to those CTL.
 * 
 */
public class CTLReplacer {
  LinkedHashMap<String, String> pairs_of_ctl = new LinkedHashMap<String, String>();

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
  public CTLReplacer(String file_name, String target_name, boolean isProof)
      throws IOException {
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
          + parser.getDestinationState() + "\n"
          + parser.getEvent() + " Evenement\n"
          );
      String condition = "";
      condition = parser.getCondition();
      if (!parser.getSourceState().equals("0")) {
        condition = parser.getCondition();
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
            ctl_name = ctl_name.substring(0, ctl_name.length() - 2);
          }

          if (GenerateFormulaAEFD.isPositive(ctl_name)) {
            pairs_of_ctl.put(ctl_name, GenerateFormulaAEFD
                .getOppositeName(ctl_name));
          } else if (GenerateFormulaAEFD
              .isNegative(ctl_name)) {
            pairs_of_ctl.put(GenerateFormulaAEFD
                .getOppositeName(ctl_name),
                ctl_name);
          }
          String new_ctl_name = ctl_name.replaceAll(
              "CTL_", "IND_");
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
      Iterator<Entry<String, String>> entry_iterator = pairs_of_ctl
          .entrySet()
          .iterator();
      while (entry_iterator.hasNext()) {
        Entry<String, String> entry = entry_iterator.next();
        writer.write(GraphFactoryAEFD.generateAutomateForCTL(entry.getKey(),
            entry.getValue()));
        if (entry_iterator.hasNext()) {
          writer.write("\n");
        }
      }
    }
    writer.close();
  }
}
