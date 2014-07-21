import domainSpecificLanguage.gui.DSLHomePage;

/* Lance l'interface graphique du simulateur utilisant le DSL */
public class LaunchDSLSimulatorGUI {

  public static void main(String[] args) {
    DSLHomePage home_page = new DSLHomePage();
    home_page.pack();
    home_page.setLocationRelativeTo(null);
    home_page.setVisible(true);
  }
}
