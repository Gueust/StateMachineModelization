import horribleFormats.XLSFormatParser;

import java.io.IOException;

/**
 * Permet de générer un fichier 6 lignes à partir d'un fichier excel contenant
 * les transitions au format de sortie des outils existants.
 */
public class ExcelFormatToAEDFFormat {

  public static void main(String[] args) throws IOException {
    XLSFormatParser.ParseXLSFormat("Nurieux/Nurieux_Donnees.xls",
        "Nurieux/generated_nurieux.txt");
    System.out.println("Finished");
  }
}
