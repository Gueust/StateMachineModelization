package domainSpecificLanguage;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.management.openmbean.KeyAlreadyExistsException;

public class Enumeration {

  private Map<String, Byte> names = new LinkedHashMap<>();
  private byte id = -128;

  public Enumeration() {
  }

  public void add(String name) {
    if (name == null) {
      throw new NullPointerException("One cannot add a null value.");
    }
    if (id > 127) {
      throw new Error(
          "No more than 255 tokens can be added in an enumeration.");
    }
    Byte previous = names.put(name, id);
    id++;
    if (previous != null) {
      throw new KeyAlreadyExistsException();
    }
  }

  public Byte get(String name) {
    if (name == null) {
      throw new NullPointerException("One cannot add a null value.");
    }
    return names.get(name);
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    boolean first = true;
    for (String name : names.keySet()) {
      if (first) {
        first = false;
      } else {
        buffer.append(", ");
      }
      buffer.append(name);
    }
    return buffer.toString();
  }

}
