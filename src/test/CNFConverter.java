package test;

import static org.junit.Assert.*;

import org.junit.Test;

import abstractGraph.Conditions.CNFFormula;
import abstractGraph.Conditions.Clause;
import abstractGraph.Conditions.Formula;
import abstractGraph.Conditions.Variable;

public class CNFConverter {

  static final String AND = Formula.AND;
  static final String OR = Formula.OR;
  static final String NOT = Formula.NOT;

  @Test
  public void parsingTest() {

    String input;
    Formula formula;

    /* Testing empty formulas */
    input = build("");
    formula = Formula.parse(input);
    assertNull(formula);

    input = build(" ");
    formula = Formula.parse(input);
    assertNull(formula);

    input = build("\t");
    formula = Formula.parse(input);
    assertNull(formula);

    input = build("\n");
    formula = Formula.parse(input);
    assertNull(formula);

    input = build("\r");
    formula = Formula.parse(input);
    assertNull(formula);

    /* Testing of simple AND, OR or NOT */
    input = "A " + AND + " B";
    formula = Formula.parse(input);
    assertEquals(formula.toString(), input);

    input = "A " + OR + " B";
    formula = Formula.parse(input);
    assertEquals(formula.toString(), input);

    input = "A " + OR + " B";
    formula = Formula.parse(input);
    assertEquals(formula.toString(), "A " + OR + " B");

    input = "(" + NOT + " A)";
    formula = Formula.parse(input);
    assertEquals(formula.toString(), input);

    input = "(" + NOT + " A) " + AND + " (B " + OR + " C)";
    formula = Formula.parse(input);
    assertEquals(formula.toString(), input);

    /* For debug */
    // System.out.println(input);
    // System.out.println(formula);

    /* Testing of more complexe formula */
    input = build("A | B & C");
    formula = Formula.parse(input);
    assertEquals(formula.toString(), input);

    /* Testing of priority of AND on OR */
    input = build("((A & C) | (A & D) | (B & C) | (B & D))");
    formula = Formula.parse(input);
    assertEquals(formula.toString(), "A ET C OU A ET D OU B ET C OU B ET D");

    input = build("A & C | A & D | B & C | B & D");
    formula = Formula.parse(input);
    assertEquals(formula.toString(), input);

    /* Testing of priority of () on AND */
    input = build("A & (C | A) & (D | B) & (C | B) & D");
    formula = Formula.parse(input);
    assertEquals(formula.toString(), input);

    /* Testing NOT */
    input = build("A & (!C | A) & (!D | B) & (!C | B) & D");
    formula = Formula.parse(input);
    assertEquals(formula.toString(),
        build("A & ((!C) | A) & ((!D) | B) & ((!C) | B) & D"));
  }

  static private String build(String s) {
    return s
        .replaceAll("!", NOT + " ")
        .replaceAll("&", AND)
        .replaceAll("\\|", OR);
  }

  @Test
  public void CNFConverterTesting() {
    String input;
    CNFFormula formula;

    /* Zero clause */
    input = build("");
    formula = CNFFormula.ConvertToCNF(Formula.parse(input));
    assertEquals(formula.testToString(), "");

    input = build(" ");
    formula = CNFFormula.ConvertToCNF(Formula.parse(input));
    assertEquals(formula.testToString(), "");

    input = build("\t");
    formula = CNFFormula.ConvertToCNF(Formula.parse(input));
    assertEquals(formula.testToString(), "");

    input = build("\n");
    formula = CNFFormula.ConvertToCNF(Formula.parse(input));
    assertEquals(formula.testToString(), "");

    input = build("\r");
    formula = CNFFormula.ConvertToCNF(Formula.parse(input));
    assertEquals(formula.testToString(), "");

    /* Only one clause */
    input = build("(A | B | C | D | E)");
    formula = CNFFormula.ConvertToCNF(Formula.parse(input));
    assertEquals(formula.testToString(), input);

    input = build("A | B | C | D | E");
    formula = CNFFormula.ConvertToCNF(Formula.parse(input));
    assertEquals(formula.testToString(), "(" + input + ")");
    // while(true){}

    /* Already CNF formulas */
    input = build("(A | B | C | D | E) & (C | D | E) & (A)");
    formula = CNFFormula.ConvertToCNF(Formula.parse(input));
    assertEquals(formula.testToString(), input);

    /* Other formulas */
    input = build("(A & B | C)");
    formula = CNFFormula.ConvertToCNF(Formula.parse(input));
    System.out.println(formula.testToString());
    assertEquals(formula.testToString(), build("(A | C) & (B | C)"));

    input = build("(P & !Q) | (R & S) | (Q & R & !S)");
    formula = CNFFormula.ConvertToCNF(Formula.parse(input));
    String result = "(P OU R OU Q) ET (P OU R OU R) ET (P OU R OU NON S) ET (P OU S OU Q) ET (P OU S OU R) ET (P OU S OU NON S) ET (NON Q OU R OU Q) ET (NON Q OU R OU R) ET (NON Q OU R OU NON S) ET (NON Q OU S OU Q) ET (NON Q OU S OU R) ET (NON Q OU S OU NON S)";
    assertEquals(formula.testToString(), result);
    // A shorter equivalent version is also
    // "(P OR  Q OR  S) AND  (P OR  R) AND  ((NOT Q) OR  R)"

  }
}
