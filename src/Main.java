import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import graph.GraphFactoryAEFD;
import graph.Model;
import graph.verifiers.Verifier;

public class Main {

  public static void main(String[] args) throws Exception {

    /* Launching logging */
    launchLogging("logfile.txt");

    DateFormat date_format = DateFormat.getTimeInstance();
    System.out.println("Execution launched at " +
        date_format.format(new Date()) + "\n");

    long startTime = System.nanoTime();

    GraphFactoryAEFD test =
        new GraphFactoryAEFD("PN/PN_SAL_N_Fonct_Auto.txt");
    Model model = test.buildModel("Testing model");
    System.out.println(model);
    Verifier default_verifier = Verifier.DEFAULT_VERIFIER;

    default_verifier.check(model);

    long estimatedTime = System.nanoTime() - startTime;

    printFullPeakMemoryUsage();

    System.out.println("Execution took " + estimatedTime / 1000000000.0 + "s");
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
