package domainSpecificLanguage.DSLValuation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.openmbean.KeyAlreadyExistsException;

public class Enumeration {

  private String enumeration_name;

  private Map<String, Byte> names = new LinkedHashMap<>();
  private byte id = -128;

  public Enumeration(String enumeration_name) {
    this.enumeration_name = enumeration_name;
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

  public Byte getByte(String name) {
    if (name == null) {
      throw new NullPointerException("One cannot add a null value.");
    }
    return names.get(name);
  }

  public String getOption(byte value) {
    for (Entry<String, Byte> entry : names.entrySet()) {
      if (entry.getValue() == value) {
        return entry.getKey();
      }
    }
    throw new Error("The name associated to " + value
        + " does not exist in " + this);
  }

  public String getName() {
    return enumeration_name;
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer("enumeration " + enumeration_name
        + " = ");
    boolean first = true;
    for (String name : names.keySet()) {
      if (first) {
        first = false;
      } else {
        buffer.append(", ");
      }
      buffer.append(name);
    }
    buffer.append(";\n");
    return buffer.toString();
  }

  public static final byte TRUE = 1;
  public static final byte FALSE = 0;

  public static String getBool(byte value) {
    if (value == FALSE) {
      return "false";
    } else if (value == TRUE) {
      return "true";
    } else {
      throw new Error(
          "Impossible case. A bool value can only be true(1) of false(0), and not "
              + value);
    }
  }

  public static byte getByteFromBool(String value) {
    if (value.equals("true")) {
      return TRUE;
    } else if (value.equals("false")) {
      return FALSE;
    } else {
      throw new Error(
          "Impossible case. A bool string must be true or false and not "
              + value);
    }
  }

}
