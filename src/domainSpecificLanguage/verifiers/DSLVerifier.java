package domainSpecificLanguage.verifiers;

import graph.Model;

import java.util.LinkedList;

import domainSpecificLanguage.graph.DSLState;
import domainSpecificLanguage.graph.DSLStateMachine;
import domainSpecificLanguage.graph.DSLTransition;
import abstractGraph.AbstractModel;
import abstractGraph.verifiers.AbstractVerificationUnit;
import abstractGraph.verifiers.CoherentVariablesWriting;
import abstractGraph.verifiers.DeterminismChecker;
import abstractGraph.verifiers.NoUselessVariables;
import abstractGraph.verifiers.SingleWritingChecker;
import abstractGraph.verifiers.WrittenAtLeastOnceChecker;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * A DSLVerifier groups several verification unit. A verification unit is a
 * single coherent test on a model.
 * 
 */
public class DSLVerifier {

  protected LinkedList<AbstractVerificationUnit<DSLStateMachine, DSLState, DSLTransition>> verification_units;

  /*
   * To be able to create a default verifier filled with all the useful
   * verifiers
   */
  static class DefaultVerifier extends DSLVerifier {
    public DefaultVerifier() {
      super();
      addVerification(new SingleWritingChecker<DSLStateMachine, DSLState, DSLTransition>());
      addVerification(new DeterminismChecker<DSLStateMachine, DSLState, DSLTransition>());
    }
  }

  static class WarningVerifier extends DSLVerifier {
    public WarningVerifier() {
      super();
      addVerification(new NoUselessVariables<DSLStateMachine, DSLState, DSLTransition>());
      addVerification(new WrittenAtLeastOnceChecker<DSLStateMachine, DSLState, DSLTransition>());
    }
  }

  public final static DSLVerifier DEFAULT_VERIFIER = new DefaultVerifier();
  public final static DSLVerifier WARNING_VERIFIER = new WarningVerifier();

  public DSLVerifier() {
    verification_units = new LinkedList<AbstractVerificationUnit<DSLStateMachine, DSLState, DSLTransition>>();
  }

  public void addVerification(
      AbstractVerificationUnit<DSLStateMachine, DSLState, DSLTransition> unit) {
    verification_units.add(unit);
  }

  /**
   * 
   * @param m
   * @param verbose
   *          True prints the results of the intermediary verifications.
   * @return True if the model verifies all the registered verification units.
   */
  public boolean check(
      AbstractModel<DSLStateMachine, DSLState, DSLTransition> m, boolean verbose) {
    if (verbose) {
      printHeader(m);
    }
    boolean result = true;
    for (AbstractVerificationUnit<DSLStateMachine, DSLState, DSLTransition> unit : verification_units) {
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
  public boolean check(AbstractModel<DSLStateMachine, DSLState, DSLTransition> m) {
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
  public boolean checkAll(
      AbstractModel<DSLStateMachine, DSLState, DSLTransition> m, boolean verbose)
      throws NotImplementedException {
    if (verbose) {
      printHeader(m);
    }
    boolean result = true;
    for (AbstractVerificationUnit<DSLStateMachine, DSLState, DSLTransition> unit : verification_units) {
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
  public boolean checkAll(
      AbstractModel<DSLStateMachine, DSLState, DSLTransition> m)
      throws NotImplementedException {
    return checkAll(m, true);
  }

  private void printHeader(
      AbstractModel<DSLStateMachine, DSLState, DSLTransition> m) {
    System.out.println("Checking of the " + m.getModelName() + " model.");
  }

  /**
   * Execute default set of verifications on the given model.
   * 
   * @param model
   * @param verbose
   */
  public void verifyModel(
      AbstractModel<DSLStateMachine, DSLState, DSLTransition> model,
      boolean verbose) {

    if (model == null) {
      return;
    }
    DSLVerifier default_verifier = DEFAULT_VERIFIER;

    boolean is_ok = !default_verifier.checkAll(model, verbose);
    System.out.println();
    if (is_ok) {
      System.out
          .println("*** FAILURE WHEN TESTING IMPERATIVE PROPERTIES ***\n");
    } else {
      System.out.println("*** IMPERATIVE PROPERTIES VERIFIED ***");
    }
    System.out.println();

    DSLVerifier warning_verifier = WARNING_VERIFIER;
    if (!warning_verifier.check(model, verbose)) {
      System.out
          .println("*** Some additionnal properties are not verified ***");
    } else {
      System.out.println("*** All other properties verifier ***");
    }
    System.out.println();
  }

  /**
   * @see DSLVerifier#verifyModel(Model, boolean)
   * @param model
   *          The model on which to run the structural verifications.
   */
  public void verifyModel(
      AbstractModel<DSLStateMachine, DSLState, DSLTransition> model) {
    verifyModel(model, true);
  }
}
