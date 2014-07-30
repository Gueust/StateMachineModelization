package abstractGraph.conditions;

import java.util.HashSet;

import abstractGraph.conditions.valuation.AbstractValuation;

/**
 * Boolean variables can be seen as Enumerated variables.
 * The equivalence is done using the TRUE and FALSE constants that are the
 * enumerated values the booleans true and false.
 */

public class BooleanVariable extends EnumeratedVariable {

  public static final byte TRUE = 1;
  public static final byte FALSE = 0;

  /**
   * @param s
   *          The name of the variable.
   * @param identifier
   *          The unique identifier of this variable. It SHOULD be >=0. Negative
   *          numbers are reserved for internal use.
   */
  public BooleanVariable(String s, int identifier) {
    super(s, identifier);
    this.identifier = identifier;
  }

  public String getOptionFromByte(byte value) {
    if (value == TRUE) {
      return "true";
    } else {
      return "false";
    }
  }

  @Override
  public boolean eval(AbstractValuation valuation) {
    return valuation.getValue(this);
  }

  @Override
  public HashSet<EnumeratedVariable> allVariables(
      HashSet<EnumeratedVariable> vars) {
    vars.add(this);
    return vars;
  }

  public static String getStringFromByte(byte value) {
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

  public static byte getByteFromString(String value) {
    if (value.equals("true")) {
      return TRUE;
    } else if (value.equals("false")) {
      return FALSE;
    } else {
      throw new IllegalArgumentException(
          "Impossible case. A bool string must be true or false and not "
              + value);
    }
  }

  public static boolean getBooleanFromByte(byte value) {
    if (value == TRUE) {
      return true;
    } else if (value == FALSE) {
      return false;
    } else {
      throw new Error(
          "Impossible case. A bool byte must be true(1) or false(0) and not "
              + value);
    }
  }

  public static byte getByteFromBool(Boolean value) {
    if (value) {
      return TRUE;
    } else {
      return FALSE;
    }
  }
}
