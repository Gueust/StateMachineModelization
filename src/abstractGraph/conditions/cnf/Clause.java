package abstractGraph.conditions.cnf;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import abstractGraph.conditions.EnumeratedVariable;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.conditions.valuation.AbstractValuation;

/**
 * A clause is a disjunction of literals (i.e. an OR over literals).
 */
public class Clause extends Formula implements Collection<Literal> {

  Vector<Literal> literals;

  public Clause() {
    this.literals = new Vector<Literal>();
  }

  public Clause(BooleanVariable f) {
    this.literals = new Vector<Literal>(1);
    this.literals.add(new Literal(f));
  }

  public Clause(Literal literal) {
    this.literals = new Vector<Literal>(1);
    this.literals.add(literal);
  }

  public Clause(Collection<Literal> literals) {
    this.literals = new Vector<Literal>(literals);
  }

  @Override
  public boolean eval(AbstractValuation valuation) {
    for (Literal l : this) {
      if (l.eval(valuation)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public HashSet<EnumeratedVariable> allVariables(
      HashSet<EnumeratedVariable> vars) {
    for (Literal l : literals) {
      vars.add(l.getVariable());
    }
    return vars;
  }

  /**
   * @see abstractGraph.conditions.cnf.CNFFormula#associatveMap()
   */
  void associatveMap(HashMap<BooleanVariable, Integer> result) {
    for (Literal l : literals) {
      Integer i = result.get(l.getVariable());
      if (i == null) {
        result.put(l.getVariable(), result.size() + 1);
      }
    }
  }

  @Override
  public String toString() {
    String s = "(";
    boolean is_first = true;
    for (Literal l : literals) {
      if (is_first) {
        s += l.toString();
      } else {
        s += " " + Formula.OR + " " + l.toString();
      }
      is_first = false;
    }
    s += ")";
    return s;
  }

  @Override
  public boolean add(Literal e) {
    assert (e != null);
    return literals.add(e);
  }

  @Override
  public boolean addAll(Collection<? extends Literal> c) {
    assert (c != null);
    return literals.addAll(c);
  }

  @Override
  public void clear() {
    literals.clear();
  }

  @Override
  public boolean contains(Object o) {
    return literals.contains(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return literals.containsAll(c);
  }

  @Override
  public boolean isEmpty() {
    return literals.isEmpty();
  }

  @Override
  public Iterator<Literal> iterator() {
    return literals.iterator();
  }

  @Override
  public boolean remove(Object o) {
    return literals.remove(o);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return literals.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return literals.retainAll(c);
  }

  @Override
  public int size() {
    return literals.size();
  }

  @Override
  public Object[] toArray() {
    return literals.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return literals.toArray(a);
  }
}
