package graph.verifiers;

import graph.Model;

public abstract class AbstractVerificationUnit {

  /**
   * Apply the current verification on the Model `m`.
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
  abstract public boolean check(Model m, boolean verbose);

  public boolean check(Model m) {
    return check(m, false);
  }

  /**
   * @return The message in case of failure of the verification.
   */
  abstract public String errorMessage();

  abstract public String successMessage();

}
