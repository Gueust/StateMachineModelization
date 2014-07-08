package solver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import abstractGraph.conditions.BooleanVariable;
import abstractGraph.conditions.cnf.CNFFormula;
import abstractGraph.conditions.cnf.Clause;
import abstractGraph.conditions.cnf.Literal;

/**
 * Binding with SAT4J solver to solve Sat problems.
 * 
 * 
 */
public class SAT4JSolver {

  /* TODO: we may want to try newUNSAT() */
  private static ISolver solver = SolverFactory.newDefault();

  /*
   * If a formula is satisfiable, we store here the information to print a model
   * for this formula
   */
  private int[] solution;
  private HashMap<Integer, BooleanVariable> id_to_var;

  /**
   * Check that a formula is satisfiable. It it is the case, one can retrieve
   * the result using #printModel()
   * Uses SAT4J solver. Documentation at :
   * <a href="http://www.sat4j.org/maven234/apidocs/index.html"/>
   * 
   * @param f
   *          The input CNF formula.
   * @throws TimeoutException
   * @return True if and only if the formula is satisfiable.
   */
  public boolean isSatisfiable(CNFFormula f) throws TimeoutException {

    solver.reset();

    int nb_clauses = f.size();
    HashMap<BooleanVariable, Integer> var_to_int = f.associativeMap();

    // prepare the solver to accept MAXVAR variables. MANDATORY for MAXSAT
    // solving
    // solver.newVar(MAXVAR);
    solver.setExpectedNumberOfClauses(nb_clauses);

    // Feed the solver using Dimacs format, using arrays of int
    // (best option to avoid dependencies on SAT4J IVecInt)
    Iterator<Clause> iterator = f.iterator();
    while (iterator.hasNext()) {
      Clause c = iterator.next();
      int[] clause = toIntClause(c, var_to_int);

      /*
       * The clause should not contain a 0, only integer (positive or negative)
       * with absolute values less or equal to MAXVAR:
       * e.g. int [] clause = {1,-3, 7}; is fine
       * int [] clause = {1, -3, 7, 0}; is not fine
       */
      try {
        /* Adapt Array to IVecInt */
        solver.addClause(new VecInt(clause));
      } catch (ContradictionException e) {
        return false;
      }
    }

    // we are done. Working now on the IProblem interface
    IProblem problem = solver;
    boolean res = problem.isSatisfiable();
    if (problem.isSatisfiable()) {
      solution = problem.model();
      id_to_var = invert(var_to_int);
    } else {
      solution = null;
      id_to_var = null;
    }

    return res;
  }

  /**
   * Used to print the literals that when true, will make the last given formula
   * true.It can be called ONLY if is {@link #isSatisfiable(CNFFormula)}
   * returned true.
   * 
   * @return The details of the solution to satisfy the last formula given to
   *         {@link #isSatisfiable(CNFFormula)}.
   */
  public String solution() {
    assert (solution != null && id_to_var != null);

    StringBuffer result = new StringBuffer(
        "The last given problem is satisfiable." +
            " A solution (true literals follows):\n");
    for (int i = 0; i < solution.length; i++) {
      int id = solution[i];
      BooleanVariable v = id_to_var.get(Math.abs(solution[i]));
      if (id > 0) {
        result.append(v.toString() + "\n");
      } else {
        result.append(new Literal(v, true).toString() + "\n");
      }
    }
    return result.toString();
  }

  /**
   * Print the literals that when true, satisfies the last formula given to
   * {@link #isSatisfiable(CNFFormula)}.
   * It also prints the formula given as argument.
   * 
   * @param f
   *          The formula to print (must be the last formula given to
   *          {@link #isSatisfiable(CNFFormula)}.
   */
  public void printModel(CNFFormula f) {
    System.err.println("The given problem is:\n");
    System.err.println(f);
    System.err.println(solution());
  }

  /**
   * Used to print a clause as the input format of Sat4J.
   * It need an int identifier for every variable.
   * 
   * @param c
   *          The clause to translate.
   * @param var_to_int
   *          The injective fonction that assign for every variable an integer
   *          identifier.
   * @return The equivalent array representing the clause (see DIMACS format)
   */
  private int[] toIntClause(Clause c, HashMap<BooleanVariable, Integer> var_to_int) {
    int[] result = new int[c.size()];
    int i = 0;
    for (Literal l : c) {
      int id = var_to_int.get(l.getVariable());
      if (l.isNegated()) {
        result[i] = -id;
      } else {
        result[i] = id;
      }
      i++;
    }
    return result;
  }

  /**
   * Invert a Hashmap considering it is an injective function (i.e. a one-to-one
   * function).
   * 
   * @param map
   *          An injective HashMap
   * @return The inverse HashMap
   */
  private HashMap<Integer, BooleanVariable> invert(HashMap<BooleanVariable, Integer> map) {
    HashMap<Integer, BooleanVariable> result =
        new HashMap<Integer, BooleanVariable>(map.size());
    for (Map.Entry<BooleanVariable, Integer> entry : map.entrySet()) {
      result.put(entry.getValue(), entry.getKey());
    }
    return result;
  }
}
