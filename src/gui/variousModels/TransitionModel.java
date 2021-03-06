package gui.variousModels;

import graph.Model;
import graph.StateMachine;
import graph.Transition;

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import domainSpecificLanguage.graph.DSLModel;
import domainSpecificLanguage.graph.DSLStateMachine;
import domainSpecificLanguage.graph.DSLTransition;
import abstractGraph.AbstractTransition;
import utils.Pair;

@SuppressWarnings("serial")
public class TransitionModel extends AbstractTableModel {

  private String[] columns = {
      "Automate", "Source", "Dest.", "Evts.", "Guard", "Actions"
  };

  private static final int AUTOMATON = 0;
  private static final int SOURCE = 1;
  private static final int DESTINATION = 2;
  private static final int EVENTS = 3;
  private static final int GUARD = 4;
  private static final int ACTIONS = 5;

  private HashMap<Integer, Pair<String, AbstractTransition<?>>> transitions =
      new HashMap<Integer, Pair<String, AbstractTransition<?>>>();

  public TransitionModel() {
  }

  public void addModel(Model m) {
    if (m == null) {
      return;
    }

    for (StateMachine machine : m) {
      Iterator<Transition> it = machine.iteratorTransitions();
      while (it.hasNext()) {
        AbstractTransition<?> transition = it.next();
        transitions.put(new Integer(transitions.size()),
            new Pair<String, AbstractTransition<?>>(machine.getName(),
                transition));
      }
    }
  }

  public void addModel(DSLModel m) {
    if (m == null) {
      return;
    }

    for (DSLStateMachine machine : m) {
      Iterator<DSLTransition> it = machine.iteratorTransitions();
      while (it.hasNext()) {
        AbstractTransition<?> transition = it.next();
        transitions.put(new Integer(transitions.size()),
            new Pair<String, AbstractTransition<?>>(machine.getName(),
                transition));
      }
    }
  }

  @Override
  public int getColumnCount() {
    return columns.length;
  }

  @Override
  public int getRowCount() {
    return transitions.size();
  }

  @Override
  public String getColumnName(int col) {
    return columns[col];
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    Pair<String, AbstractTransition<?>> pair = transitions.get(rowIndex);
    String state_machine = pair.getFirst();
    AbstractTransition<?> transition = pair.getSecond();
    if (transition == null) {
      return "ERROR";
    }

    switch (columnIndex) {
    case AUTOMATON:
      return state_machine;
    case SOURCE:
      return transition.getSource().getId();
    case DESTINATION:
      return transition.getDestination().getId();
    case EVENTS:
      return transition.getEvents();
    case GUARD:
      return transition.getCondition();
    case ACTIONS:
      return transition.getActions();
    default:
      return "ERROR";
    }
  }

}
