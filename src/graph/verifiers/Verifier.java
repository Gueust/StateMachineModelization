package graph.verifiers;

import graph.Model;

import java.util.LinkedList;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * A Verifier groups several verification unit. A verification unit is a
 * single coherent test on a model.
 * 
 */
public class Verifier {

  protected LinkedList<AbstractVerificationUnit> verification_units;

  /*
   * To be able to create a default verifier filled with all the useful
   * verifiers
   */
  static class DefaultVerifier extends Verifier {
    public DefaultVerifier() {
      super();
      addVerification(new SingleWritingChecker());
      addVerification(new DeterminismChecker());
    }
  }

  static class WarningVerifier extends Verifier {
    public WarningVerifier() {
      super();
      addVerification(new CoherentVariablesWriting());
      addVerification(new NoUselessVariables());
    }
  }

  public static final Verifier DEFAULT_VERIFIER =
      new DefaultVerifier();

  public static final Verifier WARNING_VERIFIER =
      new WarningVerifier();

  public Verifier() {
    verification_units = new LinkedList<AbstractVerificationUnit>();

  }

  public void addVerification(AbstractVerificationUnit unit) {
    verification_units.add(unit);
  }

  /**
   * 
   * @param m
   * @param verbose
   *          True prints the results of the intermediary verifications.
   * @return True if the model verifies all the registered verification units.
   */
  public boolean check(Model m, boolean verbose) {
    if (verbose) {
      printHeader(m);
    }
    boolean result = true;
    for (AbstractVerificationUnit unit : verification_units) {
      boolean tmp = unit.check(m, verbose);
      result = result & tmp;
    }
    return result;
  }

  /**
   * Simply {@link #check(Model, boolean)} using verbose = true;
   * 
   * {@inheritDoc #check(Model, boolean)}
   */
  public boolean check(Model m) {
    return check(m, true);
  }

  /**
   * Same as {@link #check(Model, boolean)} but does not stop on the first error
   * found: it will continue and try to find all the errors.
   * 
   * @param m
   * @param verbose
   *          True prints the results of the intermediary verifications.
   * @return True if the model verifies all the registered verification units.
   * @throws NotImplementedException
   *           This function is not mandatory.
   */
  public boolean checkAll(Model m, boolean verbose)
      throws NotImplementedException {
    if (verbose) {
      printHeader(m);
    }
    boolean result = true;
    for (AbstractVerificationUnit unit : verification_units) {
      boolean tmp = unit.checkAll(m, verbose);
      result = result & tmp;
    }
    return result;
  }

  /**
   * Simply {@link #checkAll(Model, boolean)} using verbose = true;
   * 
   * {@inheritDoc #checkAll(Model, boolean)}
   * 
   * @throws NotImplementedException
   *           This function is not mandatory.
   */
  public boolean checkAll(Model m) throws NotImplementedException {
    return checkAll(m, true);
  }

  private void printHeader(Model m) {
    System.out.println("Checking of the " + m.getModelName() + " model.");
  }
}
