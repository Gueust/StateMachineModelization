package engine;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import utils.Pair;
import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import abstractGraph.events.ExternalEvent;

@SuppressWarnings("serial")
public class DisplayExecutionTree<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>>
    extends JFrame {

  private JTree tree;

  DisplayExecutionTree(AbstractGlobalState<M, S, T, ?> root_state) {
    super("Tree viewer");
    pack();

    DefaultMutableTreeNode root_node =
        new DefaultMutableTreeNode(
            new Pair<AbstractGlobalState<M, S, T, ?>, ExternalEvent>(
                root_state, null));

    buildSubTree(root_node, 10);

    tree = new JTree(root_node) {

      @Override
      public String convertValueToText(Object value,
          boolean selected,
          boolean expanded,
          boolean leaf,
          int row,
          boolean hasFocus) {

        @SuppressWarnings("unchecked")
        Pair<AbstractGlobalState<M, S, T, ?>, ExternalEvent> object =
            (Pair<AbstractGlobalState<M, S, T, ?>, ExternalEvent>) ((DefaultMutableTreeNode) value)
                .getUserObject();
        if (object.second == null) {
          return null;
        }
        return object.second.toString();
      }
    };
    tree.setCellRenderer(new MonRenderer<M, S, T>());
    getContentPane().add(new JScrollPane(tree));
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setVisible(true);
  }

  private void buildSubTree(DefaultMutableTreeNode root_state,
      int depth) {
    if (depth == 0) {
      return;
    }

    @SuppressWarnings("unchecked")
    Pair<AbstractGlobalState<M, S, T, ?>, ExternalEvent> user_object =
        (Pair<AbstractGlobalState<M, S, T, ?>, ExternalEvent>) root_state
            .getUserObject();

    for (Pair<AbstractGlobalState<M, S, T, ?>, ExternalEvent> child : user_object.first.children_states) {
      DefaultMutableTreeNode node = new DefaultMutableTreeNode(child);
      root_state.add(node);

      // if (!user_object.first.has_been_display) {
      buildSubTree(node, depth - 1);
      // }
    }
  }
}

@SuppressWarnings("serial")
class MonRenderer<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>>
    extends DefaultTreeCellRenderer {

  private Icon not_safe_icon =
      new ImageIcon("src/engine/icon_red_cross.gif", "");
  private Icon impossible_state_icon =
      new ImageIcon("src/engine/impossible_state.gif", "");
  private Icon normal_icon =
      new ImageIcon("src/engine/normal.png", "");

  @Override
  public Component getTreeCellRendererComponent(final JTree tree, Object value,
      boolean sel, boolean expanded, boolean leaf, int row,
      boolean hasFocus) {

    @SuppressWarnings("unchecked")
    Pair<AbstractGlobalState<M, S, T, ?>, ExternalEvent> object =
        (Pair<AbstractGlobalState<M, S, T, ?>, ExternalEvent>) ((DefaultMutableTreeNode) value)
            .getUserObject();

    /*
     * String content;
     * if (object.second == null) {
     * content = "";
     * } else {
     * content = object.second.toString();
     * }
     */

    Component result = super.getTreeCellRendererComponent(tree, value, sel,
        expanded,
        leaf, row,
        hasFocus);

    if (object.first.isLegal()) {
      setForeground(Color.BLACK);
      setIcon(normal_icon);
    } else {
      setForeground(Color.GRAY);
      setIcon(impossible_state_icon);
    }

    if (!object.first.isSafe() || !object.first.isNotP7()) {
      setForeground(Color.RED);
      setIcon(not_safe_icon);
    }
    return result;
  }
}