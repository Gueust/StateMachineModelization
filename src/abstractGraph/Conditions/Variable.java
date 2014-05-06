package abstractGraph.Conditions;

public class Variable extends Formula {

  String varname;

  public Variable(String s) {
    varname = s;
  }

  @Override
  public String toString() {
    return varname;
  }
}
