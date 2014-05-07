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

import abstractGraph.conditions.Variable;
import abstractGraph.conditions.cnf.CNFFormula;
import abstractGraph.conditions.cnf.Clause;
import abstractGraph.conditions.cnf.Literal;

public class SAT4JSolver {

  final int MAXVAR = 1000000;
  final int NBCLAUSES = 500000;

  private static ISolver solver = SolverFactory.newDefault();

  /**
   * Check that a formula is NOT satisfiable. It it is the case, it crashes and
   * return an error message.
   * Uses SAT4J solver. Documentation at :
   * <a href="http://www.sat4j.org/maven234/apidocs/index.html"/>
   * 
   * @param f
   *          The input CNF formula.
   * @throws TimeoutException
   */
  public void checkFormulaUNSAT(CNFFormula f) throws TimeoutException {

    int nb_clauses = f.size();
    HashMap<Variable, Integer> var_to_int = f.associativeMap();

    // prepare the solver to accept MAXVAR variables. MANDATORY for MAXSAT
    // solving
    solver.newVar(MAXVAR);
    solver.setExpectedNumberOfClauses(nb_clauses);

    // Feed the solver using Dimacs format, using arrays of int
    // (best option to avoid dependencies on SAT4J IVecInt)
    Iterator<Clause> iterator = f.iterator();
    boolean is_satisfiable = true;
    while (iterator.hasNext()) {
      Clause c = iterator.next();
      int[] clause = toIntClause(c, var_to_int);

      /*
       * the clause should not contain a 0, only integer (positive or negative)
       * with absolute values less or equal to MAXVAR:
       * e.g. int [] clause = {1,-3, 7}; is fine
       * int [] clause = {1, -3, 7, 0}; is not fine
       */
      try {
        /* adapt Array to IVecInt */
        solver.addClause(new VecInt(clause));
      } catch (ContradictionException e) {
        is_satisfiable = false;
      }
    }

    // we are done. Working now on the IProblem interface
    IProblem problem = solver;
    is_satisfiable = is_satisfiable & problem.isSatisfiable();
    if (is_satisfiable) {
      int[] solution = problem.model();
      HashMap<Integer, Variable> id_to_var = invert(var_to_int);

      System.err.println("The given problem is satisfiable :");
      System.err.println(f);
      System.err.println("A solution (true literals follows)");
      for (int i = 0; i < solution.length; i++) {
        System.err.println(id_to_var.get(i).toString());
      }
    }
    solver.reset();
  }

  private int[] toIntClause(Clause c, HashMap<Variable, Integer> var_to_int) {
    int[] result = new int[c.size()];
    int i = 0;
    for (Literal l : c) {
      int id = var_to_int.get(l.getVariable());
      if (l.IsNegated()) {
        result[i] = -id;
      } else {
        result[i] = id;
      }
      i++;
    }
    return result;
  }

  private HashMap<Integer, Variable> invert(HashMap<Variable, Integer> map) {
    HashMap<Integer, Variable> result =
        new HashMap<Integer, Variable>(map.size());
    for (Map.Entry<Variable, Integer> entry : map.entrySet()) {
      result.put(entry.getValue(), entry.getKey());
    }
    return result;
  }
}
