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

/**
 * A window that display the content of the clicked state
 * 
 */
@SuppressWarnings("serial")
public class StateDisplayer<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>>
    extends JFrame {
  JTextPane textPane;
  public JButton btnNewButton;

  public StateDisplayer() {

    textPane = new JTextPane();
    textPane.setEditable(false);

    btnNewButton = new JButton("Get the same state with children");
    GroupLayout groupLayout = new GroupLayout(getContentPane());
    groupLayout.setHorizontalGroup(
        groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout
                .createSequentialGroup()
                .addGroup(groupLayout
                    .createParallelGroup(Alignment.LEADING)
                    .addComponent(btnNewButton)
                    .addComponent(textPane, GroupLayout.DEFAULT_SIZE,
                        384, Short.MAX_VALUE))
                .addGap(0))
        );
    groupLayout.setVerticalGroup(
        groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout
                .createSequentialGroup()
                .addComponent(btnNewButton)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(textPane, GroupLayout.DEFAULT_SIZE, 244,
                    Short.MAX_VALUE)
                .addGap(0))
        );
    getContentPane().setLayout(groupLayout);
    setPreferredSize(new Dimension(400, 300));
    setAlwaysOnTop(true);
    setTitle("Display of the states. Click on a state in the tree to see its details here.");
    pack();
    setVisible(true);

  }

  public void setText(AbstractGlobalState<M, S, T, ?> state) {
    textPane.setText(state.toString());
  }

}