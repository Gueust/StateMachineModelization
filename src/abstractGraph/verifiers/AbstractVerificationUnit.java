package abstractGraph.verifiers;

import abstractGraph.AbstractModel;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Allows to confront the model to a self-consistent verification.
 * 
 * The API is composed of 2 functions :
 * <ol>
 * <li>
 * the {@link #check(AbstractModel, boolean)} does the usual verification
 * process
 * provided by the verification unit. In particular, this usual method can
 * whether stop when encountering a counter example, or keep the verification to
 * find more counter examples.
 * Using the verbose mode display the result (i.e. a success message when it the
 * verification passes, or the details of the error(s) detected).
 * </li>
 * <li>
 * the {@link #checkAll(AbstractModel, boolean)} method does the explicit
 * verification
 * without ending the process when founding a counter-example: it tries to find
 * the highest number of counter examples before exiting.
 * This method is optional and may not be implemented, depending on the
 * verification (checking all the errors may be a waste of time when finding a
 * single non-conformity is enough).
 * In that case it will raise a {@link NotImplementedException}.
 * </li>
 * </ol>
 * 
 * The usual way to use this class is to call
 * {@link #check(AbstractModel, boolean)} which is the default behavior, while
 * {@link #checkAll(AbstractModel, boolean)} may be
 * used for debugging purposes.
 * 
 * 
 */
public abstract class AbstractVerificationUnit<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>> {

  /**
   * Apply the current verification on the AbstractModel<M, S, T> `m`.
   * 
   * @param m
   *          The model on which to apply the verification.
   * @param verbose
   *          Prints the status of the checking before returning the boolean
   *          result.
   *          It is equivalent to:
   *          <p><blockquote><pre>
   *          if (check(m, false)) {
   *          System.out.println(successMessage());
   *          } else {
   *          System.out.println(errorMessage());
   *          }
   *          </pre></blockquote><p>
   * @return True if the verification succeed.
   */
  abstract public boolean check(AbstractModel<M, S, T> m, boolean verbose);

  public boolean check(AbstractModel<M, S, T> m) {
    return check(m, false);
  }

  /**
   * {@inheritDoc #check(AbstractModel<M, S, T>, boolean)}
   * 
   * Apply the current verification on the AbstractModel<M, S, T> `m`, and try
   * to show the most
   * possible counter example if their exists.
   * 
   * @throws Exception
   * 
   */
  abstract public boolean checkAll(AbstractModel<M, S, T> m, boolean verbose)
      throws NotImplementedException;

  public boolean checkAll(AbstractModel<M, S, T> m)
      throws NotImplementedException {
    return checkAll(m, false);
  }

  /**
   * This function can be called ONLY if the
   * {@link #check(AbstractModel, boolean)} or
   * {@link #checkAll(AbstractModel, boolean)} function have been called first
   * and that
   * it was not set to verbose.
   * 
   * @return The message in case of failure of the verification.
   */
  abstract public String errorMessage();

  /**
   * This function can be called ONLY if the
   * {@link #check(AbstractModel, boolean)} or
   * {@link #checkAll(AbstractModel, boolean)} function have been called first
   * and that
   * it was not set to verbose.
   * 
   * @return The message in case of success of the verification.
   */
  abstract public String successMessage();

}
