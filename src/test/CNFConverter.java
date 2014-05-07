package test;

import static org.junit.Assert.*;

import org.junit.Test;

import abstractGraph.Conditions.Formula;

public class CNFConverter {

  static final String AND = Formula.AND;
  static final String OR = Formula.OR;
  static final String NOT = Formula.NOT;

  @Test
  public void parsingTest() {

    String input;
    Formula formula;

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
  }

  static private String build(String s) {
    return s
        .replaceAll("!", NOT + " ")
        .replaceAll("&", AND)
        .replaceAll("\\|", OR);
  }
}
