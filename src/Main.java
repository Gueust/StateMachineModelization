
import java.io.IOException;

import Graph.Events.SynchronisationEvent;
import Parser_Fichier_6lignes.Fichier6lignes;


public class Main {

  public static void main(String[] args) throws IOException {

    SynchronisationEvent syn = new SynchronisationEvent("SYN_AUTO_3");
    System.out.println("Event: " + syn.getPrefix());
    Fichier6lignes test = new Fichier6lignes("Automate_xxxx_xxxx.txt");
    while (test.get6Lines()){
    System.out.println(test);
    }
    

  }

}
