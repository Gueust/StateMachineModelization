package utils;

import java.util.Collection;

public class GenericToString {

  public static <E> String printCollection(Collection<E> collection,
      String delimiter) {
    StringBuffer string_buffer = new StringBuffer();
    boolean first = true;
    for (E e : collection) {
      if (first) {
        first = false;
      } else {
        string_buffer.append(delimiter);
      }
      string_buffer.append(e);
    }
    return string_buffer.toString();
  }

  public static <E> String printCollection(Collection<E> collection) {
    return printCollection(collection, ", ");
  }
}
