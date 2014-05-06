package abstractGraph.Conditions;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/**
 * A clause is a disjunction of literals (i.e. an OR over literals).
 */
public class Clause extends Formula implements Collection<Literal> {

  Vector<Literal> literals;

  public Clause() {
    literals = new Vector<Literal>();
  }

  public Clause(Variable f) {
    literals = new Vector<Literal>(1);
    literals.add(new Literal(f));
  }

  public Clause(Literal literal) {
    literals = new Vector<Literal>(1);
    literals.add(literal);
  }

  public Clause(Collection<Literal> literals) {
    literals = new Vector<Literal>(literals);
  }

  @Override
  public String toString() {
    String s = "(";
    boolean is_first = true;
    for (Literal l : literals) {
      if (is_first) {
        s += l.toString();
      } else {
        s += Formula.OR + " " + l.toString();
      }
      is_first = false;
    }
    s += ")";
    return s;
  }

  @Override
  public boolean add(Literal e) {
    return literals.add(e);
  }

  @Override
  public boolean addAll(Collection<? extends Literal> c) {
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
