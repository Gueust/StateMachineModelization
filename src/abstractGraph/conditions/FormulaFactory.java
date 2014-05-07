package abstractGraph.conditions;

import org.antlr.v4.runtime.misc.Nullable;

public interface FormulaFactory {

  public Formula parse(String expression, boolean view_tree);

  public @Nullable Formula parse(String expression);
}
