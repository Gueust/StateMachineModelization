package abstractGraph.conditions;

import abstractGraph.conditions.cnf.CNFFormula;
import abstractGraph.conditions.cnf.Clause;
import abstractGraph.conditions.cnf.Literal;

public class CustomToString {

  public static final CustomToString INSTANCE = new CustomToString();
  protected String NOT;
  protected String OR;
  protected String AND;

  protected CustomToString() {
    NOT = "NON";
    OR = "OU";
    AND = "ET";
  }

  public String toString(Formula formula) {
    if (formula instanceof Literal) {
      return toString((Literal) formula);
    } else if (formula instanceof Clause) {
      return toString((Clause) formula);
    } else if (formula instanceof CNFFormula) {
      return toString((CNFFormula) formula);
    } else if (formula instanceof AndFormula) {
      return toString((AndFormula) formula);
    } else if (formula instanceof OrFormula) {
      return toString((OrFormula) formula);
    } else if (formula instanceof NotFormula) {
      return toString((NotFormula) formula);
    } else if (formula instanceof EnumeratedVariable) {
      return toString((EnumeratedVariable) formula);
    } else if (formula instanceof True) {
      return toString((True) formula);
    } else if (formula instanceof False) {
      return toString((False) formula);
    } else if (formula instanceof EnumerationEqualityFormula) {
      return toString((EnumerationEqualityFormula) formula);
    } else {
      throw new Error("Invalid: " + formula.getClass());
    }
  }

  public String toString(Literal formula) {
    if (formula.isNegated()) {
      return NOT + " " + this.toString(formula.getVariable());
    } else {
      return this.toString(formula.getVariable());
    }
  }

  public String toString(Clause formula) {
    String s = "(";
    boolean is_first = true;
    for (Literal l : formula) {
      if (is_first) {
        s += this.toString(l);
      } else {
        s += " " + OR + " " + this.toString(l);
      }
      is_first = false;
    }
    s += ")";
    return s;
  }

  public String toString(CNFFormula formula) {
    String s = "CNF Formula. Clauses are:\n";
    for (Clause clause : formula) {
      s += this.toString(clause) + "\n";
    }
    return s;
  }

  public String toString(AndFormula formula) {
    Formula p = formula.getFirst();
    Formula q = formula.getSecond();
    return parenthesisAND(p) + " " + AND + " "
        + parenthesisAND(q);
  }

  public String toString(OrFormula formula) {
    Formula p = formula.getFirst();
    Formula q = formula.getSecond();
    return this.toString(p) + " " + OR + " " + this.toString(q);
  }

  public String toString(NotFormula formula) {
    if (formula.getF() instanceof BooleanVariable) {
      return "(" + NOT + " " + this.toString(formula.getF()) + ")";
    } else {
      return "(" + NOT + " (" + this.toString(formula.getF()) + ") )";
    }
  }

  public String toString(EnumeratedVariable formula) {
    return formula.getVarname();
  }

  public String toString(True formula) {
    return formula.toString();
  }

  public String toString(False formula) {
    return formula.toString();
  }

  private String toString(EnumerationEqualityFormula formula) {
    return formula.toString();
  }

  private String parenthesisAND(Formula f) {
    String left;
    if (f instanceof OrFormula) {
      left = "(" + this.toString(f) + ")";
    } else {
      left = this.toString(f);
    }
    return left;
  }

}