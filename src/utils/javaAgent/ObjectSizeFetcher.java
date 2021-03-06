package utils.javaAgent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Stack;

public class ObjectSizeFetcher {
  private static final Instrumentation instrumentation =
      InstrumentationGrabber.instrumentation();

  public static long getObjectSize(Object o) {
    return instrumentation.getObjectSize(o);
  }

  /* From : http://www.javaspecialists.eu/archive/Issue142.html */
  /**
   * Returns deep size of object, recursively iterating over
   * its fields and superclasses.
   */
  public static long deepSizeOf(Object obj) {
    Map<Object, Object> visited = new IdentityHashMap<Object, Object>();
    Stack<Object> stack = new Stack<Object>();
    stack.push(obj);

    long result = 0;
    do {
      result += internalSizeOf(stack.pop(), stack, visited);
    } while (!stack.isEmpty());
    return result;
  }

  /**
   * Returns true if this is a well-known shared flyweight.
   * For example, interned Strings, Booleans and Number objects
   */
  private static boolean isSharedFlyweight(Object obj) {
    // optimization - all of our flyweights are Comparable
    if (obj instanceof Comparable) {
      if (obj instanceof Enum) {
        return true;
      } else if (obj instanceof String) {
        return (obj == ((String) obj).intern());
      } else if (obj instanceof Boolean) {
        return (obj == Boolean.TRUE || obj == Boolean.FALSE);
      } else if (obj instanceof Integer) {
        return (obj == Integer.valueOf((Integer) obj));
      } else if (obj instanceof Short) {
        return (obj == Short.valueOf((Short) obj));
      } else if (obj instanceof Byte) {
        return (obj == Byte.valueOf((Byte) obj));
      } else if (obj instanceof Long) {
        return (obj == Long.valueOf((Long) obj));
      } else if (obj instanceof Character) {
        return (obj == Character.valueOf((Character) obj));
      }
    }
    return false;
  }

  private static boolean skipObject(Object obj, Map<Object, Object> visited) {
    return obj == null
        || visited.containsKey(obj)
        || isSharedFlyweight(obj);
  }

  /* From : http://www.javaspecialists.eu/archive/Issue142.html */
  private static long internalSizeOf(
      Object obj, Stack<Object> stack, Map<Object, Object> visited) {
    if (skipObject(obj, visited)) {
      return 0;
    }

    Class<?> clazz = obj.getClass();
    if (clazz.isArray()) {
      addArrayElementsToStack(clazz, obj, stack);
    } else {
      // add all non-primitive fields to the stack
      while (clazz != null) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
          if (!Modifier.isStatic(field.getModifiers())
              && !field.getType().isPrimitive()) {
            field.setAccessible(true);
            try {
              stack.add(field.get(obj));
            } catch (IllegalAccessException ex) {
              throw new RuntimeException(ex);
            }
          }
        }
        clazz = clazz.getSuperclass();
      }
    }
    visited.put(obj, null);
    return getObjectSize(obj);
  }

  private static void addArrayElementsToStack(
      Class<?> clazz, Object obj, Stack<Object> stack) {
    if (!clazz.getComponentType().isPrimitive()) {
      int length = Array.getLength(obj);
      for (int i = 0; i < length; i++) {
        stack.add(Array.get(obj, i));
      }
    }
  }
}