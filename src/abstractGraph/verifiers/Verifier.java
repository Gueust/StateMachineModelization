package abstractGraph.verifiers;

import graph.Model;

import java.util.LinkedList;

import abstractGraph.AbstractModel;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * A Verifier groups several verification unit. A verification unit is a
 * single coherent test on a model.
 * 
 */
public class Verifier<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> {

  protected LinkedList<AbstractVerificationUnit<M, S, T>> verification_units;

  /*
   * To be able to create a default verifier filled with all the useful
   * verifiers
   */
  static class DefaultVerifier<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>>
      extends Verifier<M, S, T> {
    public DefaultVerifier() {
      super();
      addVerification(new SingleWritingChecker<M, S, T>());
      addVerification(new InitializationProperties<M, S, T>());
      addVerification(new TautologyFromStateZero<M, S, T>());
      addVerification(new DeterminismChecker<M, S, T>());
    }
  }

  static class WarningVerifier<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>>
      extends Verifier<M, S, T> {
    public WarningVerifier() {
      super();
      addVerification(new CoherentVariablesWriting<M, S, T>());
      // addVerification(new NoUselessVariables<M, S, T>());
      addVerification(new WrittenAtLeastOnceChecker<M, S, T>());
    }
  }

  public static <M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> Verifier<M, S, T> getDefaultVerifier() {
    return new DefaultVerifier<M, S, T>();
  }

  public static <M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> Verifier<M, S, T> getWarningVerifier() {
    return new WarningVerifier<M, S, T>();
  }

  public Verifier() {
    verification_units = new LinkedList<AbstractVerificationUnit<M, S, T>>();

  }

  public void addVerification(AbstractVerificationUnit<M, S, T> unit) {
    verification_units.add(unit);
  }

  /**
   * 
   * @param m
   * @param verbose
   *          True prints the results of the intermediary verifications.
   * @return True if the model verifies all the registered verification units.
   */
  public boolean check(AbstractModel<M, S, T> m, boolean verbose) {
    if (verbose) {
      printHeader(m);
    }
    boolean result = true;
    for (AbstractVerificationUnit<M, S, T> unit : verification_units) {
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
  public boolean check(AbstractModel<M, S, T> m) {
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
  public boolean checkAll(AbstractModel<M, S, T> m, boolean verbose)
      throws NotImplementedException {
    if (verbose) {
      printHeader(m);
    }
    boolean result = true;
    for (AbstractVerificationUnit<M, S, T> unit : verification_units) {
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
  public boolean checkAll(AbstractModel<M, S, T> m)
      throws NotImplementedException {
    return checkAll(m, true);
  }

  private void printHeader(AbstractModel<M, S, T> m) {
    System.out.println("Checking of the " + m.getModelName() + " model.");
  }

  /**
   * Execute default set of verifications on the given model.
   * 
   * @param model
   * @param verbose
   */
  public void verifyModel(AbstractModel<M, S, T> model, boolean verbose) {

    if (model == null) {
      return;
    }
    Verifier<M, S, T> default_verifier = getDefaultVerifier();

    boolean is_ok = !default_verifier.checkAll(model, verbose);
    System.out.println();
    if (is_ok) {
      System.out
          .println("*** FAILURE WHEN TESTING IMPERATIVE PROPERTIES ***\n");
    } else {
      System.out.println("*** IMPERATIVE PROPERTIES VERIFIED ***");
    }
    System.out.println();

    Verifier<M, S, T> warning_verifier = getWarningVerifier();
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
  public void verifyModel(AbstractModel<M, S, T> model) {
    verifyModel(model, true);
  }
}
