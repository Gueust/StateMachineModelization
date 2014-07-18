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
      addVerification(new InitializationProperties());
      addVerification(new TautologyFromStateZero());
      addVerification(new DeterminismChecker());
    }
  }

  static class WarningVerifier extends Verifier {
    public WarningVerifier() {
      super();
      addVerification(new CoherentVariablesWriting());
      addVerification(new NoUselessVariables());
      addVerification(new WrittenAtLeastOnceChecker());
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

  /**
   * Execute default set of verifications on the given model.
   * 
   * @param model
   * @param verbose
   */
  public static void verifyModel(Model model, boolean verbose) {

    if (model == null) {
      return;
    }
    Verifier default_verifier = Verifier.DEFAULT_VERIFIER;

    boolean is_ok = !default_verifier.checkAll(model, verbose);
    System.out.println();
    if (is_ok) {
      System.out
          .println("*** FAILURE WHEN TESTING IMPERATIVE PROPERTIES ***\n");
    } else {
      System.out.println("*** IMPERATIVE PROPERTIES VERIFIED ***");
    }
    System.out.println();

    Verifier warning_verifier = Verifier.WARNING_VERIFIER;
    if (!warning_verifier.check(model, verbose)) {
      System.out
          .println("*** Some additionnal properties are not verified ***");
    } else {
      System.out.println("*** All other properties verifier ***");
    }
    System.out.println();
  }

  /**
   * @see Verifier#verifyModel(Model, boolean)
   * @param model
   *          The model on which to run the structural verifications.
   */
  public static void verifyModel(Model model) {
    verifyModel(model, true);
  }
}
