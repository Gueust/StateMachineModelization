import graph.GraphFactoryAEFD;

/*
 * Ce fichier permet de générer des automates à CTL automatiquement à partir
 * du nom du CTL actif et inactif.
 */
public class CTLAutomatonGeneration {

  public static void main(String[] args) {

    /*
     * Ce code permet d'écrire sur la sortie standard un automate à CTL.
     * Il est indispensable d'écrire d'abord le CTL actif, puis la version
     * inactive.
     */
    System.out.println(GraphFactoryAEFD.generateAutomateForCTL(
        "CTL_PdAn_I_V1_Actif",
        "CTL_PdAn_I_V1_Inactif"));

  }
}
