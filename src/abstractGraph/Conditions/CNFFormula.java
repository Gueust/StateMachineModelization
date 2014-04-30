package abstractGraph.Conditions;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public class CNFFormula extends Formula implements Collection<Clause> {

  private Vector<Clause> clauses;

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

  /**
   * Convert a formula using Or, And and Not operators to a conjunctive normal
   * form (CNF).
   * See <a href="http://en.wikipedia.org/wiki/Conjunctive_normal_form">.
   * 
   * The algorithm is an implementation from :
   * http://www.cs.jhu.edu/~jason/tutorials/convert-to-CNF.html
   * 
   * @param f
   *          The general formula
   * @return An equivalent CNF formula
   */
  static public CNFFormula ConvertToCNF(Formula f) {
    if (f instanceof Variable) {
      // this is a CNF formula consisting of 1 clause that contains 1 literal
      return new CNFFormula((Variable) f);
    } else if (f instanceof NotFormula) {
      /* The formula is of the form Not A */
      Formula A = ((NotFormula) f).f;

      if (A instanceof Variable) {
        /* If f has the form ~A for some variable A, then return f. */
        CNFFormula result = new CNFFormula();
        result.add(new Clause(new Literal((Variable) A, true)));
        return result;
      } else if (A instanceof NotFormula) {
        /* If f has the form ~(~P), then return CONVERT(P). (double negation) */
        return ConvertToCNF(((NotFormula) A).f);
      } else if (A instanceof AndFormula) {
        /*
         * If f has the form ~(P ^ Q), then return CONVERT(~P v ~Q). (de
         * Morgan's Law)
         */
        Formula p = ((AndFormula) A).p;
        Formula q = ((AndFormula) A).q;
        return ConvertToCNF(new OrFormula(new NotFormula(p), new NotFormula(q)));
      } else if (f instanceof OrFormula) {
        /*
         * If f has the form ~(P v Q), then return CONVERT(~P ^ ~Q).(de Morgan's
         * Law)
         */
        Formula p = ((OrFormula) A).p;
        Formula q = ((OrFormula) A).q;
        return ConvertToCNF(new AndFormula(new NotFormula(p), new NotFormula(q)));
      }
      return null;
    } else if (f instanceof AndFormula) {
      /*
       * If f has the form P ^ Q, then:
       * CNFFormula(concatenate(CONVERT(P).clauses, CONVERT(Q).clauses))
       */
      Formula p = ((AndFormula) f).p;
      Formula q = ((AndFormula) f).q;

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
      Formula p = ((OrFormula) f).p;
      Formula q = ((OrFormula) f).q;
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
          "Only formulas using Or, And and Not operators can be converted" +
              " into a CNF formula");
    }
  }

  @Override
  public boolean add(Clause c) {
    return clauses.add(c);
  }

  @Override
  public boolean addAll(Collection<? extends Clause> c) {
    return this.clauses.addAll(clauses);
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
