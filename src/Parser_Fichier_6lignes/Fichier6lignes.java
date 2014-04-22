package Parser_Fichier_6lignes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Parser_Fichier_6lignes is a class that will read a text file and get the
 * different parameters of the transitions written in the file.
 * This class will at each call of the function get6lines() put the values of
 * the parameters of the transition of the file in the variables graph_name,
 * event, condition, action, source_state, destination_source
 */
public class Fichier6lignes {
  // Declaration of the variables.
  public String graph_name;
  public String event;
  public String condition;
  public String action;
  public String source_state;
  public String destination_state;
  public BufferedReader buff = null;

  /**
   * Verify that `input` ends with `tested_string`.
   * 
   * @param input
   *          The string to test
   * @param tested_string
   *          The key word that must terminate `inout`
   * @return True if the condition is verified
   */
  private boolean checkSuffix(String input, String tested_string) {
    String temp = input.substring(input.length() - tested_string.length());
    return temp.equals(tested_string);
  }

  public Fichier6lignes(String file_location) throws IOException {
    buff = new BufferedReader(new FileReader(file_location));
  }

  public void get6Lines() throws IOException {

    graph_name = buff.readLine();
    source_state = buff.readLine();
    destination_state = buff.readLine();
    event = buff.readLine();
    condition = buff.readLine();
    action = buff.readLine();

    if (graph_name == null || source_state == null ||
        destination_state == null || event == null ||
        condition == null || action == null) {
      throw new IOException("There has less than 6 lines to read");
    }
    graph_name = graph_name.trim();
    source_state = source_state.trim();
    destination_state = destination_state.trim();
    event = event.trim();
    condition = condition.trim();
    action = action.trim();

    if (!checkSuffix(event, "Evenement")) {
      System.out.println("Error, event expected");
      System.exit(-1);
    }
    event = event.substring(0, event.length() - 9).trim();

    if (!checkSuffix(condition, "Condition")) {
      System.out.println("Error, condition expected");
      System.exit(-1);
    }
    condition = condition.substring(0, condition.length() - 9).trim();

    if (!checkSuffix(action, "Action")) {
      System.out.println("Error, action expected");
      System.exit(-1);
    }
    action = action.substring(0, action.length() - 6).trim();
  }

  @Override
  public String toString() {
    String out = "Event: " + event + "\n" +
        "Condition: " + condition + "\n" +
        "Action: " + action + "\n" +
        "source_state: " + source_state + "\n" +
        "destination_state: " + destination_state + "\n" +
        "graph_name: " + graph_name;
    return out;
  }

}
