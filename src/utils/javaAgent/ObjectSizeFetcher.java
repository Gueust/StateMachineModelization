package utils.javaAgent;

import java.lang.instrument.Instrumentation;

public class ObjectSizeFetcher {
  private static final Instrumentation instrumentation =
      InstrumentationGrabber.instrumentation();

  public static long getObjectSize(Object o) {
    return instrumentation.getObjectSize(o);
  }
}