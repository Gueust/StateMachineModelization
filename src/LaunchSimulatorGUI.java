import gui.HomePage;

/* Lance l'interface graphique du simulateur */
public class LaunchSimulatorGUI {

  public static void main(String[] args) {
    HomePage home_page = new HomePage();
    home_page.pack();
    home_page.setLocationRelativeTo(null);
    home_page.setVisible(true);
  }
}
