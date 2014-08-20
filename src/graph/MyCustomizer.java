package graph;

import graph.conditions.aefdParser.AEFDFormulaFactory;
import graph.conditions.aefdParser.GenerateFormulaAEFD;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.conditions.CustomToString;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.NotFormula;
import abstractGraph.conditions.cnf.Literal;

/* Do not print NOT IND_A_Actif but IND_A_Inactif */
public class MyCustomizer extends CustomToString {

  private GenerateFormulaAEFD generator =
      new GenerateFormulaAEFD(new AEFDFormulaFactory(true), null);

  public MyCustomizer() {
  }

  /**
   * 
   * @param generator
   *          A generator that contains the database of the pairs of variables.
   */
  public MyCustomizer(GenerateFormulaAEFD generator) {
    this.generator = generator;
  }

  @Override
  public String toString(Formula f) {
    return super.toString(f);
  }

  @Override
  public String toString(Literal l) {
    BooleanVariable variable = ((Literal) l).getVariable();

    boolean is_negated = ((Literal) l).isNegated();
    if (is_negated) {
      String result = generator.getOppositeName(variable.toString());
      // System.out.println("Opposite of " + variable + " is " + result);
      assert result != null : "Impossible to find the opposite of "
          + variable.toString()
          + ". It is likely that you have creating a customizer without any "
          + "database for the pairs of variables.";
      return result;
    } else {
      return variable.toString();
    }
  }

  @Override
  public String toString(NotFormula f) {
    Formula A = f.getF();
    if (A instanceof BooleanVariable) {
      return generator.getOppositeName(A.toString());
    }
    return super.toString(f);
  }
}