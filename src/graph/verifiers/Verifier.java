package graph.verifiers;

import java.util.LinkedList;

import graph.Model;

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
      addVerification(new NoConcurrentWriting());
      addVerification(new DeterminismChecker());
    }
  }

  public static final Verifier DEFAULT_VERIFIER =
      new DefaultVerifier();

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
}