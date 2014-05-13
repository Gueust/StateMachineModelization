package abstractGraph.conditions;

import java.util.HashSet;

public class AndFormula extends Formula {

  Formula p;
  Formula q;

  /**
   * Create p AND q.
   * 
   * @param p
   *          The left formula.
   * @param q
   *          The right formula.
   */
  public AndFormula(Formula p, Formula q) {
    this.p = p;
    this.q = q;
  }

  @Override
  public boolean eval(Valuation valuation) {
    return p.eval(valuation) && q.eval(valuation);
  }

  /**
   * An AND formula represents : p AND q. This function returns p.
   * 
   * @return The first formula p of this AND
   */
  public Formula getFirst() {
    return p;
  }

  /**
   * An OR formula represents : p AND q. This function returns q.
   * 
   * @return The second formula q of this AND
   */
  public Formula getSecond() {
    return q;
  }

  @Override
  public HashSet<Variable> allVariables(HashSet<Variable> vars) {
    p.allVariables(vars);
    q.allVariables(vars);
    return vars;
  }

  @Override
  public String toString() {
    return parenthesis(p) + " " + Formula.AND + " " + parenthesis(q);
  }

  private String parenthesis(Formula f) {
    String left;
    if (f instanceof OrFormula) {
      left = "(" + f.toString() + ")";
    } else {
      left = f.toString();
    }
    return left;
  }

}