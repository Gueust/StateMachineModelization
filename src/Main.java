import engine.GraphSimulator;
import graph.GraphFactoryAEFD;
import graph.Model;
import graph.State;
import graph.StateMachine;
import graph.Transition;
import graph.verifiers.Verifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import utils.TeeOutputStream;
import abstractGraph.conditions.FormulaFactory;
import abstractGraph.conditions.Variable;
import abstractGraph.conditions.cnf.Literal;
import abstractGraph.events.Actions;
import abstractGraph.events.Events;
import abstractGraph.events.ExternalEvent;
import abstractGraph.events.VariableChange;

public class Main {

  public static void main(String[] args) throws Exception {

    /* Launching logging */
    launchLogging("verification_tools_logfile.txt");

    DateFormat date_format = DateFormat.getTimeInstance();
    System.out.println("Execution launched at " +
        date_format.format(new Date()) + "\n");

    long startTime = System.nanoTime();

    GraphFactoryAEFD graph_factory = new GraphFactoryAEFD();

    String functional_model = "PN/PN_SAL_N_Fonct_Auto.txt";
    String proof_model = "PN/PN_SAL_N_Preuv_Auto.txt";

    Model model = graph_factory
        .buildModel(functional_model, functional_model);
    model.build();
    Model proof = graph_factory.buildModel(proof_model, proof_model);
    proof.build();

    GraphSimulator simulator = new GraphSimulator(model);
    simulator.checkCompatibility();

    /* Initialization */
    for (StateMachine machine : model) {
      simulator.getGlobalState().setState(machine, machine.getState("0"));
    }

    verifyModel(model);
    verifyModel(proof);

    LinkedList<ExternalEvent> initialization_events = new LinkedList<ExternalEvent>();
    HashMap<String, String> pairs_of_ctl = model.regroupCTL();
    for (Entry<String, String> pair : pairs_of_ctl.entrySet()) {

      initialization_events.add(new ExternalEvent(pair.getKey()));
    }
    System.out.println("Pair of CTLs:" + pairs_of_ctl.toString());

    System.out.println(initialization_events);

    simulator.executeAll(initialization_events);

    long estimatedTime = System.nanoTime() - startTime;

    printFullPeakMemoryUsage();

    System.out.println("Execution took " + estimatedTime / 1000000000.0 + "s");

    // generateAutomateForCTL(graph_factory.getFactory(),
    // "CTL_Zone2_Libre", "CTL_Zone2_Occupee");

  }

  private static void verifyModel(Model model) {
    verifyModel(model, true);
  }

  private static void verifyModel(Model model, boolean verbose) {

    Verifier default_verifier = Verifier.DEFAULT_VERIFIER;

    boolean is_ok = !default_verifier.checkAll(model, verbose);
    System.out.println();
    if (is_ok) {
      System.out
          .println("*** FAILURE WHEN TESTING IMPERATIVE PROPERTIES ***\n");
    } else {
      System.out.println("*** IMPERATIVE PROPERTIES VERIFIED ***");
    }
    System.out.println();

    Verifier warning_verifier = Verifier.WARNING_VERIFIER;
    if (!warning_verifier.check(model, verbose)) {
      System.out
          .println("*** Some additionnal properties are not verified ***");
    } else {
      System.out.println("*** All other properties verifier ***");
    }
    System.out.println();
  }

  /**
   * Duplicate the standard output into the given file. If its size is greater
   * than 10Mo, it will empty the file. It will write at the end of the file
   * otherwise.
   * 
   * @param logfile_name
   */
  private static void launchLogging(String logfile_name) {
    File file = new File(logfile_name);
    try {
      /* We make sure than we have no more than 10Mo */
      FileOutputStream fos;
      if (file.length() > 10000000) {
        fos = new FileOutputStream(file);
      } else {
        fos = new FileOutputStream(file, true);
      }

      /* We want to print in the standard "System.out" and in "file" */
      TeeOutputStream myOut = new TeeOutputStream(System.out, fos);
      PrintStream ps = new PrintStream(myOut);
      System.setOut(ps);

      /* Same for System.err */
      TeeOutputStream myErr = new TeeOutputStream(System.err, fos);
      PrintStream ps_err = new PrintStream(myErr);
      System.setErr(ps_err);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Print the total peak memory usage.
   */
  static void printPeakMemoryUsage() {
    List<MemoryPoolMXBean> pools = ManagementFactory
        .getMemoryPoolMXBeans();
    for (MemoryPoolMXBean pool : pools) {
      MemoryUsage peak = pool.getPeakUsage();
      System.out.printf("Peak %s memory used: %,d%n", pool.getName(),
          peak.getUsed());
      System.out.printf("Peak %s memory reserved: %,d%n", pool.getName(),
          peak.getCommitted());
    }
  }

  /**
   * Print the detail of the peak memory usage of all java threads.
   */
  static void printFullPeakMemoryUsage() {
    List<MemoryPoolMXBean> pools = ManagementFactory
        .getMemoryPoolMXBeans();
    long total_used = 0, total_commited = 0;

    for (MemoryPoolMXBean pool : pools) {
      MemoryUsage peak = pool.getPeakUsage();
      total_used += peak.getUsed();
      total_commited += peak.getCommitted();
    }
    System.out.println();
    System.out.printf("Total peak memory used: %,d%n", total_used);
    System.out.printf("Total peak memory reserved: %,d%n", total_commited);
  }

  private static void generateAutomateForCTL(
      FormulaFactory factory,
      String CTL_pos,
      String CTL_neg) {

    String variable_name =
        CTL_pos.substring(CTL_pos.indexOf('_') + 1, CTL_pos.length());
    variable_name = variable_name.substring(0, variable_name.lastIndexOf('_'));

    String positive_suffix =
        CTL_pos.substring(CTL_pos.lastIndexOf('_') + 1, CTL_pos.length());
    String negative_suffix =
        CTL_neg.substring(CTL_neg.lastIndexOf('_') + 1, CTL_neg.length());

    String IND_actif_name = "IND_" + variable_name + "_" + positive_suffix;
    String IND_inactif_name = "IND_" + variable_name + "_" + negative_suffix;

    StateMachine machine = new StateMachine("GRAPH_" + IND_actif_name);

    State init_state = machine.addState("0");
    State positive_state = machine.addState("1");
    State negative_state = machine.addState("2");

    Events events;
    Actions actions;
    Variable variable = factory.getVariable(IND_actif_name);

    /* Transition from 0 to 1 */
    events = new Events();
    events.addEvent(new ExternalEvent(CTL_pos));
    actions = new Actions();
    actions.add(new VariableChange(new Literal(variable)));
    machine.addTransition(init_state, positive_state,
        events, null, actions);

    /* Transition from 0 to 2 */
    events = new Events();
    events.addEvent(new ExternalEvent(CTL_neg));
    actions = new Actions();
    actions.add(new VariableChange(new Literal(variable, true)));
    machine.addTransition(init_state, negative_state,
        events, null, actions);

    /* Transition from 1 to 2 */
    events = new Events();
    events.addEvent(new ExternalEvent(CTL_neg));
    actions = new Actions();
    actions.add(new VariableChange(new Literal(variable, true)));
    machine.addTransition(positive_state, negative_state,
        events, null, actions);

    /* Transition from 2 to 1 */
    events = new Events();
    events.addEvent(new ExternalEvent(CTL_pos));
    actions = new Actions();
    actions.add(new VariableChange(new Literal(variable)));
    machine.addTransition(negative_state, positive_state,
        events, null, actions);

    System.out.println(machine);

    Iterator<Transition> trans_it = machine.iteratorTransitions();
    while (trans_it.hasNext()) {
      System.out.println(GraphFactoryAEFD.writeTransition(machine, trans_it
          .next()));
    }
  }
}
