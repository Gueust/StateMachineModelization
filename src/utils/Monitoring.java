package utils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;

public class Monitoring {

  public static String getAdress(Object object) {
    return // object.getClass().getName() +
    '@' + Integer.toHexString(System.identityHashCode(object));
  }

  /**
   * Print the total peak memory usage.
   */
  public static void printPeakMemoryUsage() {
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
  public static void printFullPeakMemoryUsage() {
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
