package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import abstractGraph.conditions.AndFormula;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.FormulaFactory;
import abstractGraph.conditions.NotFormula;
import abstractGraph.conditions.OrFormula;
import abstractGraph.conditions.Valuation;
import abstractGraph.conditions.Variable;
import abstractGraph.conditions.cnf.CNFFormula;
import abstractGraph.conditions.parser.BooleanExpressionFactory;

/**
 * Testing of the main functions of the abstractGraph.conditions package:
 * <ol>
 * <li>
 * the parsing done by {@link BooleanExpressionFactory}
 * 
 * </li>
 * <li>
 * the formulas construction done using {@link Formula} and its subclasses (
 * {@link AndFormula},{@link OrFormula},{@link NotFormula},{@link Variable}).
 * 
 * </li>
 * <li>
 * the conversion of Formulas into the {@link CNFFormula} (package cnf).
 * 
 * </li>
 * <li>
 * - the evaluation of formulas, using a {@link Valuation} environment, and the
 * equality between formulas.
 * 
 * </li>
 * </ol>
 * 
 */
public class AbstractGraph_Conditions_PackageTesting {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  static final String AND = Formula.AND;
  static final String OR = Formula.OR;
  static final String NOT = Formula.NOT;

  static final FormulaFactory factory = Formula.newDefaultFactory();

  /**
   * Testing of the {@link BooleanExpressionFactory#parse(String)} function.
   */
  @Test
  public void parserPackageTesting() {

    String input;
    Formula formula;

    /* Testing empty formulas */
    input = build("");
    formula = factory.parse(input);
    assertNull(formula);

    input = build(" ");
    formula = factory.parse(input);
    assertNull(formula);

    input = build("\t");
    formula = factory.parse(input);
    assertNull(formula);

    input = build("\n");
    formula = factory.parse(input);
    assertNull(formula);

    input = build("\r");
    formula = factory.parse(input);
    assertNull(formula);

    /* True and False */
    input = build("true");
    formula = factory.parse(input);
    System.out.println("formula " + formula);
    assertTrue(formula == Formula.TRUE);

    input = build("false");
    formula = factory.parse(input);
    assertTrue(formula == Formula.FALSE);

    /* Testing of simple AND, OR or NOT */
    input = "A " + AND + " B";
    formula = factory.parse(input);
    assertEquals(formula.toString(), input);

    input = "A " + OR + " B";
    formula = factory.parse(input);
    assertEquals(formula.toString(), input);

    input = "A " + OR + " B";
    formula = factory.parse(input);
    assertEquals(formula.toString(), "A " + OR + " B");

    input = "(" + NOT + " A)";
    formula = factory.parse(input);
    assertEquals(formula.toString(), input);

    input = "(" + NOT + " A) " + AND + " (B " + OR + " C)";
    formula = factory.parse(input);
    assertEquals(formula.toString(), input);

    /* For debug */
    // System.out.println(input);
    // System.out.println(formula);

    /* Testing of more complexe formula */
    input = build("A | B & C");
    formula = factory.parse(input);
    assertEquals(formula.toString(), input);

    /* Testing of priority of AND on OR */
    input = build("((A & C) | (A & D) | (B & C) | (B & D))");
    formula = factory.parse(input);
    assertEquals(formula.toString(), "A ET C OU A ET D OU B ET C OU B ET D");

    input = build("A & C | A & D | B & C | B & D");
    formula = factory.parse(input);
    assertEquals(formula.toString(), input);

    /* Testing of priority of () on AND */
    input = build("A & (C | A) & (D | B) & (C | B) & D");
    formula = factory.parse(input);
    assertEquals(formula.toString(), input);

    /* Testing NOT */
    input = build("A & (!C | A) & (!D | B) & (!C | B) & D");
    formula = factory.parse(input);
    assertEquals(formula.toString(),
        build("A & ((!C) | A) & ((!D) | B) & ((!C) | B) & D"));
  }

  static private String build(String s) {
    return s
        .replaceAll("!", NOT + " ")
        .replaceAll("&", AND)
        .replaceAll("\\|", OR);
  }

  /**
   * Testing of {@link CNFFormula#ConvertToCNF(Formula)}.
   */
  @Test
  public void CNFConverterTesting() {
    String input;
    Formula initial_formula;
    CNFFormula formula;

    /* Zero clause */
    input = build("");
    formula = CNFFormula.ConvertToCNF(factory.parse(input));
    assertEquals(formula.testToString(), "");

    input = build(" ");
    formula = CNFFormula.ConvertToCNF(factory.parse(input));
    assertEquals(formula.testToString(), "");

    input = build("\t");
    formula = CNFFormula.ConvertToCNF(factory.parse(input));
    assertEquals(formula.testToString(), "");

    input = build("\n");
    formula = CNFFormula.ConvertToCNF(factory.parse(input));
    assertEquals(formula.testToString(), "");

    input = build("\r");
    formula = CNFFormula.ConvertToCNF(factory.parse(input));
    assertEquals(formula.testToString(), "");

    /* true and false */
    initial_formula = Formula.TRUE;
    formula = CNFFormula.ConvertToCNF(initial_formula);
    assertTrue(initial_formula.equals(formula));

    initial_formula = Formula.FALSE;
    formula = CNFFormula.ConvertToCNF(initial_formula);
    System.out.println(initial_formula);
    System.out.println(formula);
    assertTrue(initial_formula.equals(formula));

    /* Not formulas */
    input = build("P");
    initial_formula = factory.parse(input);
    formula = CNFFormula.ConvertToCNF(initial_formula);
    assertTrue(initial_formula.equals(formula));

    input = build("!(P)");
    initial_formula = factory.parse(input);
    formula = CNFFormula.ConvertToCNF(initial_formula);
    assertTrue(initial_formula.equals(formula));

    input = build("!(P | Q)");
    initial_formula = factory.parse(input);
    formula = CNFFormula.ConvertToCNF(initial_formula);
    assertTrue(initial_formula.equals(formula));

    input = build("!(P & Q)");
    initial_formula = factory.parse(input);
    formula = CNFFormula.ConvertToCNF(initial_formula);
    assertTrue(initial_formula.equals(formula));

    /* Or formulas */
    input = build("(P | Q)");
    initial_formula = factory.parse(input);
    formula = CNFFormula.ConvertToCNF(initial_formula);
    assertTrue(initial_formula.equals(formula));

    /* And formulas */
    input = build("(P & Q)");
    initial_formula = factory.parse(input);
    formula = CNFFormula.ConvertToCNF(initial_formula);
    assertTrue(initial_formula.equals(formula));

    /* Only one clause */
    input = build("(A | B | C | D | E)");
    formula = CNFFormula.ConvertToCNF(factory.parse(input));
    assertEquals(formula.testToString(), input);

    input = build("A | B | C | D | E");
    formula = CNFFormula.ConvertToCNF(factory.parse(input));
    assertEquals(formula.testToString(), "(" + input + ")");

    /* Already CNF formulas */
    input = build("(A | B | C | D | E) & (C | D | E) & (A)");
    formula = CNFFormula.ConvertToCNF(factory.parse(input));
    assertEquals(formula.testToString(), input);

    /* Other formulas */
    input = build("(A & B | C)");
    formula = CNFFormula.ConvertToCNF(factory.parse(input));
    assertEquals(formula.testToString(), build("(A | C) & (B | C)"));

    input = build("(P & !Q) | (R & S) | (Q & R & !S)");
    formula = CNFFormula.ConvertToCNF(factory.parse(input));
    String result = "(P OU R OU Q) ET (P OU R OU R) ET (P OU R OU NON S) ET (P OU S OU Q) ET (P OU S OU R) ET (P OU S OU NON S) ET (NON Q OU R OU Q) ET (NON Q OU R OU R) ET (NON Q OU R OU NON S) ET (NON Q OU S OU Q) ET (NON Q OU S OU R) ET (NON Q OU S OU NON S)";
    assertEquals(formula.testToString(), result);
    // A shorter equivalent version is also
    // "(P OR  Q OR  S) AND  (P OR  R) AND  ((NOT Q) OR  R)"

  }

  /**
   * Testing of the two modes of the {@link FormulaFactory}.
   */
  @Test
  public void FormulaFactoryModes() {
    String input;
    Variable v1, v2, v3;
    FormulaFactory f;
    /*
     * Single variable mode reset test: we check that the variables are lost
     * between parsings.
     */
    f = new BooleanExpressionFactory(false);
    input = build("A");
    f.parse(input);
    v1 = f.getVariable("A");
    v2 = f.getVariable("A");
    assertTrue(v1 == v2);
    f.parse(input);
    v3 = f.getVariable("A");
    assertTrue(v1 != v3);

    // thrown.expect(NullPointerException.class);
    // thrown.expectMessage("does not exist");

    /* United model model */
    f = new BooleanExpressionFactory(true);
    input = build("A");
    f.parse(input);
    /* We test that variables are kept between formulas */
    v1 = f.getVariable("A");
    f.parse(input);
    v2 = f.getVariable("A");
    assertTrue(v1 == v2);
  }

  /**
   * Testing the evaluation of function for formulas created by the
   * {@link BooleanExpressionFactory}, for both modes.
   */
  @Test
  public void formulaEvaluationTesting() {
    FormulaFactory f;

    f = new BooleanExpressionFactory(true);
    formulaEvaluationTest(f);

    f = new BooleanExpressionFactory(false);
    formulaEvaluationTest(f);

  }

  /**
   * We test the evaluation for a given FormulaFactory
   */
  public void formulaEvaluationTest(FormulaFactory f) {
    String input;
    Formula formula;
    Valuation valuation = new Valuation();

    /* Evaluation testing */

    /* Single variable */
    input = build("A");
    formula = f.parse(input);
    valuation.setValue(f.getVariable("A"), true);
    assertTrue(formula.eval(valuation));

    valuation.setValue(f.getVariable("A"), false);
    assertFalse(formula.eval(valuation));

    /* AND operator */
    input = build("A & B");
    formula = f.parse(input);
    valuation.setValue(f.getVariable("A"), true);
    valuation.setValue(f.getVariable("B"), true);
    assertTrue(formula.eval(valuation));

    valuation.setValue(f.getVariable("A"), true);
    valuation.setValue(f.getVariable("B"), false);
    assertFalse(formula.eval(valuation));

    valuation.setValue(f.getVariable("A"), false);
    valuation.setValue(f.getVariable("B"), true);
    assertFalse(formula.eval(valuation));

    valuation.setValue(f.getVariable("A"), false);
    valuation.setValue(f.getVariable("B"), false);
    assertFalse(formula.eval(valuation));

    /* OR operator */
    input = build("A | B");
    formula = f.parse(input);
    valuation.setValue(f.getVariable("A"), true);
    valuation.setValue(f.getVariable("B"), true);
    assertTrue(formula.eval(valuation));

    valuation.setValue(f.getVariable("A"), true);
    valuation.setValue(f.getVariable("B"), false);
    assertTrue(formula.eval(valuation));

    valuation.setValue(f.getVariable("A"), false);
    valuation.setValue(f.getVariable("B"), true);
    assertTrue(formula.eval(valuation));

    valuation.setValue(f.getVariable("A"), false);
    valuation.setValue(f.getVariable("B"), false);
    assertFalse(formula.eval(valuation));

    /* One single complex example to test the interactions */
    input = build("(P & !Q) | (R & S) | (Q & R & !S)");
    formula = f.parse(input);

    /* First 8 first tests */
    valuation.setValue(f.getVariable("P"), true);
    valuation.setValue(f.getVariable("Q"), true);
    valuation.setValue(f.getVariable("R"), true);
    valuation.setValue(f.getVariable("S"), true);
    assertTrue(formula.eval(valuation));

    valuation.setValue(f.getVariable("P"), true);
    valuation.setValue(f.getVariable("Q"), true);
    valuation.setValue(f.getVariable("R"), true);
    valuation.setValue(f.getVariable("S"), false);
    assertTrue(formula.eval(valuation));

    valuation.setValue(f.getVariable("P"), true);
    valuation.setValue(f.getVariable("Q"), true);
    valuation.setValue(f.getVariable("R"), false);
    valuation.setValue(f.getVariable("S"), true);
    assertFalse(formula.eval(valuation));

    valuation.setValue(f.getVariable("P"), true);
    valuation.setValue(f.getVariable("Q"), true);
    valuation.setValue(f.getVariable("R"), false);
    valuation.setValue(f.getVariable("S"), false);
    assertFalse(formula.eval(valuation));

    valuation.setValue(f.getVariable("P"), true);
    valuation.setValue(f.getVariable("Q"), false);
    valuation.setValue(f.getVariable("R"), true);
    valuation.setValue(f.getVariable("S"), true);
    assertTrue(formula.eval(valuation));

    valuation.setValue(f.getVariable("P"), true);
    valuation.setValue(f.getVariable("Q"), false);
    valuation.setValue(f.getVariable("R"), true);
    valuation.setValue(f.getVariable("S"), false);
    assertTrue(formula.eval(valuation));

    valuation.setValue(f.getVariable("P"), true);
    valuation.setValue(f.getVariable("Q"), false);
    valuation.setValue(f.getVariable("R"), false);
    valuation.setValue(f.getVariable("S"), true);
    assertTrue(formula.eval(valuation));

    valuation.setValue(f.getVariable("P"), true);
    valuation.setValue(f.getVariable("Q"), false);
    valuation.setValue(f.getVariable("R"), false);
    valuation.setValue(f.getVariable("S"), false);
    assertTrue(formula.eval(valuation));

    /* Second 8 tests (first variable is false */
    valuation.setValue(f.getVariable("P"), false);
    valuation.setValue(f.getVariable("Q"), true);
    valuation.setValue(f.getVariable("R"), true);
    valuation.setValue(f.getVariable("S"), true);
    assertTrue(formula.eval(valuation));

    valuation.setValue(f.getVariable("P"), false);
    valuation.setValue(f.getVariable("Q"), true);
    valuation.setValue(f.getVariable("R"), true);
    valuation.setValue(f.getVariable("S"), false);
    assertTrue(formula.eval(valuation));

    valuation.setValue(f.getVariable("P"), false);
    valuation.setValue(f.getVariable("Q"), true);
    valuation.setValue(f.getVariable("R"), false);
    valuation.setValue(f.getVariable("S"), true);
    assertFalse(formula.eval(valuation));

    valuation.setValue(f.getVariable("P"), false);
    valuation.setValue(f.getVariable("Q"), true);
    valuation.setValue(f.getVariable("R"), false);
    valuation.setValue(f.getVariable("S"), false);
    assertFalse(formula.eval(valuation));

    valuation.setValue(f.getVariable("P"), false);
    valuation.setValue(f.getVariable("Q"), false);
    valuation.setValue(f.getVariable("R"), true);
    valuation.setValue(f.getVariable("S"), true);
    assertTrue(formula.eval(valuation));

    valuation.setValue(f.getVariable("P"), false);
    valuation.setValue(f.getVariable("Q"), false);
    valuation.setValue(f.getVariable("R"), true);
    valuation.setValue(f.getVariable("S"), false);
    assertFalse(formula.eval(valuation));

    valuation.setValue(f.getVariable("P"), false);
    valuation.setValue(f.getVariable("Q"), false);
    valuation.setValue(f.getVariable("R"), false);
    valuation.setValue(f.getVariable("S"), true);
    assertFalse(formula.eval(valuation));

    valuation.setValue(f.getVariable("P"), false);
    valuation.setValue(f.getVariable("Q"), false);
    valuation.setValue(f.getVariable("R"), false);
    valuation.setValue(f.getVariable("S"), false);
    assertFalse(formula.eval(valuation));
  }

  /**
   * Testing of {@link Formula#equals(Object)}.
   */
  @Test
  public void FormulaEqualsTesting() {
    String input;
    Formula f1, f2;

    /* Obviously false equalities */

    input = build("A");
    f1 = factory.parse(input);
    input = build("B");
    f2 = factory.parse(input);
    assertFalse(f1.equals(f2));

    input = build("A");
    f1 = factory.parse(input);
    input = build("A & B");
    f2 = factory.parse(input);
    assertFalse(f1.equals(f2));

    input = build("A & B");
    f1 = factory.parse(input);
    input = build("A | B");
    f2 = factory.parse(input);
    assertFalse(f1.equals(f2));

    input = build("A & B");
    f1 = factory.parse(input);
    input = build("A | B");
    f2 = factory.parse(input);
    assertFalse(f1.equals(f2));

    /* Obviously verified equalities */
    input = build("A & B");
    f1 = factory.parse(input);
    input = build("B & A");
    f2 = factory.parse(input);
    assertTrue(f1.equals(f2));

    /* Verified equalities */
    input = build("(P & !Q) | (R & S) | (Q & R & !S)");
    f1 = factory.parse(input);
    input = build("(P OR  Q OR  S) AND  (P OR  R) AND  ((NOT Q) OR  R)");
    f2 = factory.parse(input);
    assertTrue(f1.equals(f2));

    /* Not equal */
    input = build("(P & !Q) | (R & S) | (Q & R & !S)");
    f1 = factory.parse(input);
    input = build("(S OR  Q OR  S) AND  (P OR  R) AND  ((NOT Q) OR  R)");
    f2 = factory.parse(input);
    assertFalse(f1.equals(f2));

  }
}
