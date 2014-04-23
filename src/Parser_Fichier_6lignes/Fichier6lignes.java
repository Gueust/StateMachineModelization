package Parser_Fichier_6lignes;

import java.io.BufferedReader;
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
  private String graph_name;
  private String event;
  private String condition;
  private String action;
  private String source_state;
  private String destination_state;
  private BufferedReader buff = null;

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

  /**
   * Constructor that read the file 'file_location'
   * 
   * @param file_location
   *          The file to open
   * @throws IOException
   *           In case of error, call the IOException
   */
  public Fichier6lignes(String file_location) throws IOException {
    buff = new BufferedReader(new FileReader(file_location));
  }

  /**
   * Function that will read the file 6 lines by 6 lines and affect the differents 
   * values of the transitions to the right variable.
   * @throws IOException
   */
  public void get6Lines() throws IOException {
    //Read the file line by line and affect the correct value to the correct variable
    graph_name = buff.readLine();
    source_state = buff.readLine();
    destination_state = buff.readLine();
    event = buff.readLine();
    condition = buff.readLine();
    action = buff.readLine();

    //Test that nether of the variables are equal to null
    if (graph_name == null || source_state == null ||
        destination_state == null || event == null ||
        condition == null || action == null) {
      throw new IOException("There has less than 6 lines to read");
    }
    
    //Remove the blanks at the beginning and the end of the string
    graph_name = graph_name.trim();
    source_state = source_state.trim();
    destination_state = destination_state.trim();
    event = event.trim();
    condition = condition.trim();
    action = action.trim();

    //Test that the event, condition and action end with the right key word. 
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
    String out = "graph_name: " + graph_name + "\n" +
        "source_state: " + source_state + "\n" +
        "destination_state: " + destination_state + "\n" +
        "Event: " + event + "\n" +
        "Condition: " + condition + "\n" +
        "Action: " + action + "\n";
    return out;
  }

  //getters and setters of the differents variables
  public String getGraphName() {
    return graph_name;
  }

  public void setGraphName(String graph_name) {
    this.graph_name = graph_name;
  }
  
  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }
  
  public String getCondition() {
    return condition;
  }

  public void setCondtion(String condition) {
    this.condition = condition;
  }
  
  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }
  
  public String getSourceState() {
    return source_state;
  }

  public void setSourceState(String source_state) {
    this.source_state = source_state;
  }
  
  public String getDestinationState() {
    return destination_state;
  }

  public void setDestinationState(String destination_state) {
    this.destination_state = destination_state;
  }
}
