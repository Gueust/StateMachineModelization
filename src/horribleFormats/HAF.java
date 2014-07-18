package horribleFormats;

import graph.GraphFactoryAEFD;
import graph.Model;
import graph.MyCustomizer;
import graph.State;
import graph.StateMachine;
import graph.Transition;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import abstractGraph.conditions.Formula;
import abstractGraph.events.SingleEvent;

/* Horrible ACIFER Format*/
public class HAF {

  private static final String NEWLINE = "\r\n";

  static MyCustomizer customizer = new MyCustomizer();

  public static String toHAF(Model model) {
    StringBuilder builder = new StringBuilder();

    builder.append(
        "############################################" + NEWLINE +
            "Decompilation d'un fichier '.bin' -- Poste:Nurieux" + NEWLINE +
            " --- Date de compilation  : 01/09/08 - Heure : 09:04:37 --- "
            + NEWLINE +
            " --- Date de décompilation: 01/09/08 - Heure : 09:04:42 --- "
            + NEWLINE +
            "############################################" + NEWLINE +
            NEWLINE +
            " Application :Bourg_Bellegarde" + NEWLINE +
            " Poste       :PN" + NEWLINE +
            " Version       :0.2_2" + NEWLINE +
            " Date       :01/09/08" + NEWLINE +
            " Heure       :09:04:37" + NEWLINE +
            NEWLINE
        );

    boolean is_first_machine = true;
    for (StateMachine machine : model) {
      Iterator<Transition> t = machine.iteratorTransitions();
      int nb_transitions = 0;
      while (t.hasNext()) {
        t.next();
        nb_transitions++;
      }
      int nb_states = 0;
      for (@SuppressWarnings("unused")
      State s : machine) {
        nb_states++;
      }

      if (is_first_machine) {
        is_first_machine = false;
      } else {
        builder.append(NEWLINE + NEWLINE);
      }
      builder.append(
          "============================================================"
              + NEWLINE +
              "Automate : ZONE1_" + machine.getName() + NEWLINE +
              "Version  : 0.86" + NEWLINE +
              "Nbr transitions : " + nb_transitions + NEWLINE +
              "Nbr places : " + nb_states + NEWLINE +
              "Marquage : 1" + NEWLINE +
              "Domaine : 0" + NEWLINE +
              NEWLINE +
              "--------------------" + NEWLINE +
              "Place : 0  UNUSED FIELD" + NEWLINE +
              NEWLINE +
              "--------------------");

      for (State state : machine) {
        for (Transition transition : state) {

          builder.append(NEWLINE + NEWLINE);

          String from = transition.getSource().getId();
          String to = transition.getDestination().getId();

          String condition = "Pas de condition";
          if (transition.getCondition() != Formula.TRUE) {
            condition = transition.getCondition().toString(customizer);
            condition = condition.replace(" OU ", " + ");
            condition = condition.replace(" ET ", " * ");
          }

          String events =
              transition.getEvents().toString(customizer).replace(";", "");

          StringBuilder action_builder = new StringBuilder();
          boolean is_first_event = true;
          for (SingleEvent single_event : transition
              .getActions()
              .getCollection()) {
            if (is_first_event) {
              is_first_event = false;
            } else {
              action_builder.append(" , ");
            }

            action_builder.append(single_event.toString(customizer));
          }
          String actions = action_builder.toString();

          builder.append(
              "Transition: " + from + "_" + to + NEWLINE +
                  "Evenement : " + events + NEWLINE +
                  "Condition : " + condition + NEWLINE +
                  "Action : " + actions);
        }

      }
    }
    return builder.toString();
  }

  public static void toHAF(String filename, String destination)
      throws IOException {
    GraphFactoryAEFD graph_factory = new GraphFactoryAEFD();

    Model model = graph_factory.buildModel(filename, filename);
    model.build();

    BufferedWriter out = new BufferedWriter(new FileWriter(destination));
    out.append(toHAF(model));
    out.close();
  }

}
