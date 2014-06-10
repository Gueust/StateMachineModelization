import engine.GraphSimulator;
import graph.GraphFactoryAEFD;
import graph.Model;
import graph.StateMachine;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import utils.TeeOutputStream;
import abstractGraph.events.ExternalEvent;

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

}
