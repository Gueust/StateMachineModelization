import horribleFormats.HAF;

import java.io.IOException;

/**
 * Ce fichier permet de générer le format de sortie d'ACIFER à partir d'un
 * fichier 6 lignes.
 * Aucune spécification n'existant, cela a été fait à partir d'un exemple, et
 * aucune garantie forte n'est donnée sur la correction.
 * Il peut rester des modifications manuelles à faire, en particulier dans le
 * cas d'utilisation de "NON" dans les formules du format 6 lignes.
 * 
 */
public class AEFDFormatToACIFERFormat {

  public static void main(String[] args) throws IOException {
    HAF.toHAF("examples/PN à SAL Cas3 avec IND_DTP.txt",
        "PN à SAL Cas3 avec IND_DTP.dco");
  }
}
