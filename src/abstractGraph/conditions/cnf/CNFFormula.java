package abstractGraph.conditions.cnf;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import abstractGraph.conditions.AbstractValuation;
import abstractGraph.conditions.AndFormula;
import abstractGraph.conditions.False;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.NotFormula;
import abstractGraph.conditions.OrFormula;
import abstractGraph.conditions.True;
import abstractGraph.conditions.Variable;

/**
 * A conjunction of clauses (i.e. AND of clauses).
 */
public class CNFFormula extends Formula implements Collection<Clause> {

  Vector<Clause> clauses;

  public CNFFormula() {
    this.clauses = new Vector<Clause>();
  }

  public CNFFormula(Collection<Clause> clauses) {
    this.clauses = new Vector<Clause>(clauses);
  }

  public CNFFormula(Variable f) {
    clauses = new Vector<Clause>(1);
    clauses.add(new Clause(f));
  }

  @Override
  public boolean eval(AbstractValuation valuation) {
    for (Clause c : this) {
      if (!c.eval(valuation)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public HashSet<Variable> allVariables(HashSet<Variable> vars) {
    for (Clause c : clauses) {
      c.allVariables(vars);
    }
    return vars;
  }

  private static final Literal true_literal =
      new Literal(new Variable("true_var"));
  private static final Literal not_true_literal =
      new Literal(true_literal.getVariable(), true);

  /**
   * In order to get the formula solved in a SAT solver we need to write the CNF
   * formula in Dimacs format. To do so, we must give to each variable a unique
   * int indentifier that is within [1, n] where n is the number of variable.
   * 
   * @return An hashmap such that .get(var) is the unique int representing the
   *         Variable var
   */
  public HashMap<Variable, Integer> associativeMap() {
    HashMap<Variable, Integer> result = new HashMap<Variable, Integer>();
    for (Clause c : clauses) {
      c.associatveMap(result);
    }
    return result;
  }

  /**
   * Convert a formula using Or, And and Not operators to a conjunctive normal
   * form (CNF).
   * See <a href="http://en.wikipedia.org/wiki/Conjunctive_normal_form">.
   * 
   * The algorithm is an implementation from :
   * http://www.cs.jhu.edu/~jason/tutorials/convert-to-CNF.html
   * 
   * In the worst case scenario, the created formula is m^n where there is n
   * clauses of m literals.
   * 
   * @param f
   *          The general formula
   * @return An equivalent CNF formula
   */
  static public CNFFormula ConvertToCNF(Formula f) {
    if (f == null || f instanceof True)
      return new CNFFormula();

    if (f instanceof False) {
      CNFFormula result = new CNFFormula();
      result.add(new Clause(true_literal));
      result.add(new Clause(not_true_literal));
      return result;
    } else if (f instanceof Variable) {
      // this is a CNF formula consisting of 1 clause that contains 1 literal
      return new CNFFormula((Variable) f);
    } else if (f instanceof NotFormula) {
      /* The formula is of the form Not A */
      Formula A = ((NotFormula) f).getF();

      if (A instanceof False) {
        return new CNFFormula();
      } else if (A instanceof True) {
        /* We transform Not true into true_var & (NOT true_var) */
        CNFFormula result = new CNFFormula();
        result.add(new Clause(true_literal));
        result.add(new Clause(not_true_literal));
        return result;
      } else if (A instanceof Variable) {
        /* If f has the form ~A for some variable A, then return f. */
        CNFFormula result = new CNFFormula();
        result.add(new Clause(new Literal((Variable) A, true)));
        return result;
      } else if (A instanceof NotFormula) {
        /* If f has the form ~(~P), then return CONVERT(P). (double negation) */
        return ConvertToCNF(((NotFormula) A).getF());
      } else if (A instanceof AndFormula) {
        /*
         * If f has the form ~(P ^ Q), then return CONVERT(~P v ~Q). (de
         * Morgan's Law)
         */
        Formula p = ((AndFormula) A).getFirst();
        Formula q = ((AndFormula) A).getSecond();
        return ConvertToCNF(new OrFormula(new NotFormula(p), new NotFormula(q)));
      } else if (A instanceof OrFormula) {
        /*
         * If f has the form ~(P v Q), then return CONVERT(~P ^ ~Q).(de Morgan's
         * Law)
         */
        Formula p = ((OrFormula) A).getFirst();
        Formula q = ((OrFormula) A).getSecond();
        return ConvertToCNF(new AndFormula(new NotFormula(p), new NotFormula(q)));
      }
      System.err.println(A + " is an instance of " + A.getClass());
      throw new NotImplementedException();
    } else if (f instanceof AndFormula) {
      /*
       * If f has the form P ^ Q, then:
       * CNFFormula(concatenate(CONVERT(P).clauses, CONVERT(Q).clauses))
       */
      Formula p = ((AndFormula) f).getFirst();
      Formula q = ((AndFormula) f).getSecond();

      CNFFormula result = ConvertToCNF(p);
      result.addAll(ConvertToCNF(q));

      return result;
    } else if (f instanceof OrFormula) {
      /*
       * If f has the form P v Q, then:
       * CONVERT(P) must have the form P1 ^ P2 ^ ... ^ Pm, and
       * CONVERT(Q) must have the form Q1 ^ Q2 ^ ... ^ Qn,
       * where all the Pi and Qi are disjunctions of literals.
       * So we need a CNF formula equivalent to
       * (P1 ^ P2 ^ ... ^ Pm) v (Q1 ^ Q2 ^ ... ^ Qn).
       * So return (P1 v Q1) ^ (P1 v Q2) ^ ... ^ (P1 v Qn)
       * ^ (P2 v Q1) ^ (P2 v Q2) ^ ... ^ (P2 v Qn)
       * ...
       * ^ (Pm v Q1) ^ (Pm v Q2) ^ ... ^ (Pm v Qn)
       */
      Formula p = ((OrFormula) f).getFirst();
      Formula q = ((OrFormula) f).getSecond();
      CNFFormula converted_p = ConvertToCNF(p);
      CNFFormula converted_q = ConvertToCNF(q);

      CNFFormula result = new CNFFormula();
      for (Clause p_i : converted_p) {
        for (Clause q_i : converted_q) {
          Clause c = new Clause(p_i);
          c.addAll(q_i);
          result.add(c);
        }
      }
      return result;
    } else {
      throw new UnsupportedOperationException(
          "Only formulas using True, Or, And and Not operators can be converted"
              +
              " into a CNF formula. The formula was:\n" + f);
    }
  }

  @Override
  public String toString() {
    String s = "CNF Formula. Clauses are:\n";
    for (Clause clause : clauses) {
      s += clause.toString() + "\n";
    }
    return s;
  }

  public String testToString() {
    String s = "";
    boolean first = true;
    for (Clause c : clauses) {
      if (!first)
        s += " " + Formula.AND + " ";
      first = false;
      s += c.toString();
    }
    return s;
  }

  @Override
  public boolean add(Clause c) {
    assert (c != null);
    return clauses.add(c);
  }

  @Override
  public boolean addAll(Collection<? extends Clause> c) {
    assert (c != null);
    return this.clauses.addAll(c);
  }

  @Override
  public void clear() {
    this.clauses.clear();
  }

  @Override
  public boolean contains(Object o) {
    return this.clauses.contains(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return this.clauses.containsAll(c);
  }

  @Override
  public boolean isEmpty() {
    return this.clauses.isEmpty();
  }

  @Override
  public Iterator<Clause> iterator() {
    return this.clauses.iterator();
  }

  @Override
  public boolean remove(Object o) {
    return this.clauses.remove(o);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return this.clauses.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return this.clauses.retainAll(c);
  }

  @Override
  public int size() {
    return this.clauses.size();
  }

  @Override
  public Object[] toArray() {
    return this.clauses.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return this.clauses.toArray(a);
  }
}
