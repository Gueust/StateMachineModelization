package test;

import static org.junit.Assert.*;

import org.junit.Test;

import abstractGraph.Conditions.Formula;

public class CNFConverter {

  @Test
  public void parsingTest() {

    final String AND = Formula.AND;
    final String OR = Formula.OR;
    final String NOT = Formula.NOT;

    String input;
    Formula formula;

    input = "A " + AND + " B";
    formula = Formula.parse(input);
    assertEquals(input, formula.toString());

    input = "(A " + OR + " B)";
    formula = Formula.parse(input);
    assertEquals(input, formula.toString());

    input = "(" + NOT + " A)";
    formula = Formula.parse(input);
    assertEquals(input, formula.toString());

    input = "(" + NOT + " A) " + AND + " (B " + OR + " C)";
    formula = Formula.parse(input);
    assertEquals(input, formula.toString());

    /* For debug */
    // System.out.println(input);
    // System.out.println(formula);
  }
}
