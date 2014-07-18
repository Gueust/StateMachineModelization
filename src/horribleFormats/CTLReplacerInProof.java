package horribleFormats;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import parserAEFDFormat.Fichier6lignes;

public class CTLReplacerInProof {

  public CTLReplacerInProof(String file_name, String target_name)
      throws IOException {

    BufferedWriter writer = new BufferedWriter(new FileWriter(
        target_name));
    boolean beginning = true;
    Fichier6lignes parser = new Fichier6lignes(file_name);
    while (parser.get6Lines()) {
      if (!beginning) {
        writer.write("\n");
      } else {
        beginning = false;
      }
      if (parser.getSourceState().equals("0")) {
        writer.write(parser.getGraphName() + "\n"
            + parser.getSourceState() + "\n"
            + parser.getDestinationState() + "\n"
            + (parser.getEvent() + " Evenement") + "\n"
            + (parser.getCondition()
                .replaceAll("[a-zA-Z0-9_]*_non_Bloque", "true")
                .replaceAll("[a-zA-Z0-9_]*_Bloque", "false") + " Condition")
                .trim() + "\n"
            + (parser.getAction() + " Action").trim());
      } else {
        writer.write(parser.getGraphName()
            + "\n"
            + parser.getSourceState()
            + "\n"
            + parser.getDestinationState()
            + "\n"
            + (parser.getEvent() + " Evenement").replaceAll("CTL_", "IND_")
            + "\n"
            + (parser.getCondition()
                .replaceAll("[a-zA-Z0-9_]*_non_Bloque", "true")
                .replaceAll("[a-zA-Z0-9_]*_Bloque", "false") + " Condition")
                .trim()
                .replaceAll("CTL_", "IND_") + "\n"
            + (parser.getAction() + " Action").trim());
      }
    }
    writer.close();
  }
}
