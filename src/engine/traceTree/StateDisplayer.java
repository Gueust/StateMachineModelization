package engine.traceTree;

import javax.swing.JFrame;

import java.awt.Dimension;

import javax.swing.JTextPane;

import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;

import engine.GraphSimulatorInterface;
import javax.swing.JScrollPane;

/**
 * A window that display the content of the clicked state
 * 
 */
@SuppressWarnings("serial")
public class StateDisplayer<GS extends AbstractGlobalState<M, S, T, ?>, M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>>
    extends JFrame {

  JTextPane textPane;
  public JButton btnNewButton;
  private GraphSimulatorInterface<GS, M, S, T> simulator;

  public StateDisplayer(GraphSimulatorInterface<GS, M, S, T> graph_simulator) {

    simulator = graph_simulator;

    btnNewButton = new JButton("Get the same state with children");

    JScrollPane scrollPane = new JScrollPane();
    GroupLayout groupLayout = new GroupLayout(getContentPane());
    groupLayout.setHorizontalGroup(
        groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(
                groupLayout.createSequentialGroup()
                    .addGroup(
                        groupLayout.createParallelGroup(Alignment.LEADING)
                            .addComponent(btnNewButton)
                            .addGroup(
                                groupLayout.createSequentialGroup()
                                    .addGap(10)
                                    .addComponent(scrollPane,
                                        GroupLayout.DEFAULT_SIZE, 200,
                                        Short.MAX_VALUE)))
                    .addContainerGap())
        );
    groupLayout.setVerticalGroup(
        groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(
                groupLayout.createSequentialGroup()
                    .addComponent(btnNewButton)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 222,
                        Short.MAX_VALUE)
                    .addContainerGap())
        );

    textPane = new JTextPane();
    scrollPane.setViewportView(textPane);
    textPane.setEditable(false);
    getContentPane().setLayout(groupLayout);
    setPreferredSize(new Dimension(400, 300));
    setAlwaysOnTop(true);
    setTitle("Display of the states. Click on a state in the tree to see its details here.");
    pack();
    setLocationRelativeTo(null);
    setVisible(true);

  }

  @SuppressWarnings("unchecked")
  public void setText(AbstractGlobalState<M, S, T, ?> state) {
    textPane.setText(simulator.globalStateToString((GS) state));
  }
}