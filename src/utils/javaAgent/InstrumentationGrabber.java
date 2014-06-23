package utils.javaAgent;

import java.lang.instrument.Instrumentation;

/**
 * Agent call-back that stores the {@link Instrumentation} provided by the JVM.
 * 
 * <p>Not to be used directly.
 * From http://code.google.com/p/memory-measurer/source/browse/trunk/src/
 * objectexplorer/MemoryMeasurer.java
 * under Apache License 2.0
 */
public class InstrumentationGrabber {
  private static volatile Instrumentation instrumentation;

  public static void premain(String agentArgs, Instrumentation inst) {
    if (InstrumentationGrabber.instrumentation != null)
      throw new AssertionError("Already initialized");
    InstrumentationGrabber.instrumentation = inst;
  }

  private static void checkSetup() {
    if (instrumentation == null) {
      throw new IllegalArgumentException(
          "Instrumentation is not setup properly. "
              + "You have to pass -javaagent:path/to/object-explorer.jar to the java interpreter");
    }
  }

  static Instrumentation instrumentation() {
    checkSetup();
    return instrumentation;
  }
}