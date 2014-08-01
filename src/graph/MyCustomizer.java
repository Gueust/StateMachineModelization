package graph;

import graph.conditions.aefdParser.GenerateFormulaAEFD;
import abstractGraph.conditions.BooleanVariable;
import abstractGraph.conditions.CustomToString;
import abstractGraph.conditions.Formula;
import abstractGraph.conditions.NotFormula;
import abstractGraph.conditions.cnf.Literal;

/* Do not print NOT IND_A_Actif but IND_A_Inactif */
public class MyCustomizer extends CustomToString {
  @Override
  public String toString(Formula f) {
    return super.toString(f);
  }

  @Override
  public String toString(Literal l) {
    BooleanVariable variable = ((Literal) l).getVariable();
    boolean is_negated = ((Literal) l).isNegated();
    if (is_negated) {
      return GenerateFormulaAEFD.getOppositeName(variable.toString());
    } else {
      return variable.toString();
    }
  }

  @Override
  public String toString(NotFormula f) {
    Formula A = f.getF();
    if (A instanceof BooleanVariable) {
      return GenerateFormulaAEFD.getOppositeName(A.toString());
    }
    return super.toString(f);
  }
}