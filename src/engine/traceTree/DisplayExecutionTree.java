package engine.traceTree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import utils.Pair;
import abstractGraph.AbstractGlobalState;
import abstractGraph.AbstractState;
import abstractGraph.AbstractStateMachine;
import abstractGraph.AbstractTransition;
import abstractGraph.events.ExternalEvent;
import engine.GraphSimulatorInterface;

@SuppressWarnings("serial")
public class DisplayExecutionTree<GS extends AbstractGlobalState<M, S, T, ?>, M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>>
    extends JFrame {

  final private JTree tree;
  private DefaultMutableTreeNode root_node;
  private final StateDisplayer<GS, M, S, T> state_displayer;

  DisplayExecutionTree(GraphSimulatorInterface<GS, M, S, T> graph_simulator,
      AbstractGlobalState<M, S, T, ?> root_state) {
    super("Tree viewer");
    pack();

    state_displayer = new StateDisplayer<>(graph_simulator);

    assert (root_state != null);

    root_node =
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
            (Pair<AbstractGlobalState<M, S, T, ?>, ExternalEvent>)
            ((DefaultMutableTreeNode) value).getUserObject();
        if (object.second == null) {
          return null;
        }
        return object.second.toString();
      }
    };
    tree.setCellRenderer(new MonRenderer<M, S, T>());

    tree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);

    tree.addTreeSelectionListener(new TreeSelectionListener() {
      @SuppressWarnings("unchecked")
      public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
            tree.getLastSelectedPathComponent();

        /* if nothing is selected */
        if (node == null)
          return;

        /* retrieve the node that was selected */
        Pair<?, ?> nodeInfo = (Pair<?, ?>) node.getUserObject();

        /* React to the node selection. */
        state_displayer
            .setText((AbstractGlobalState<M, S, T, ?>) nodeInfo.first);
      }
    });
    state_displayer.btnNewButton.addActionListener(new ActionListener() {

      @SuppressWarnings("unchecked")
      @Override
      public void actionPerformed(ActionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
            tree.getLastSelectedPathComponent();

        /* retrieve the node that was selected */
        Pair<?, ?> nodeInfo = (Pair<?, ?>) node.getUserObject();

        selectNodeWithSons((AbstractGlobalState<M, S, T, ?>) nodeInfo.first);

      }
    });

    getContentPane().add(new JScrollPane(tree));
    setLocationRelativeTo(null);
    // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setPreferredSize(new Dimension(800, 600));
    setMinimumSize(new Dimension(300, 400));
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

    assert (user_object != null);

    if (user_object.first.children_states == null) {
      return;
    }
    for (Pair<AbstractGlobalState<M, S, T, ?>, ExternalEvent> child : user_object.first.children_states) {
      DefaultMutableTreeNode node = new DefaultMutableTreeNode(child);
      root_state.add(node);

      // if (!user_object.first.has_been_display) {
      buildSubTree(node, depth - 1);
      // }
    }
  }

  /**
   * When we find a node that has already been printed, we do not print its
   * content.
   * Then, when we want to see the full trace, we want to find the first
   * occurence of a state to display its sons
   * 
   * @param state
   *          The state to look for.
   */
  @SuppressWarnings("unchecked")
  private void selectNodeWithSons(AbstractGlobalState<M, S, T, ?> state) {

    Enumeration<DefaultMutableTreeNode> en = root_node.depthFirstEnumeration();
    while (en.hasMoreElements()) {

      DefaultMutableTreeNode node = en.nextElement();
      Pair<AbstractGlobalState<M, S, T, ?>, ExternalEvent> object =
          (Pair<AbstractGlobalState<M, S, T, ?>, ExternalEvent>)
          ((DefaultMutableTreeNode) node).getUserObject();

      if (object.first.equals(state)) {
        if (node.getChildCount() == 0) {
          continue;
        }

        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();

        TreePath path = new TreePath(treeModel.getPathToRoot(node));
        tree.setSelectionPath(path);
        tree.scrollPathToVisible(path);
        return;
      }
    }

  }
}

@SuppressWarnings("serial")
class MonRenderer<M extends AbstractStateMachine<S, T>, S extends AbstractState<T>, T extends AbstractTransition<S>>
    extends DefaultTreeCellRenderer {

  private Icon not_safe_icon =
      new ImageIcon("src/engine/traceTree/icon_red_cross.gif", "");
  private Icon impossible_state_icon =
      new ImageIcon("src/engine/traceTree/impossible_state.gif", "");
  private Icon normal_icon =
      new ImageIcon("src/engine/traceTree/normal.png", "");

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
      if (!object.first.isSafe() || !object.first.isNotP7()) {
        setForeground(Color.RED);
        setIcon(not_safe_icon);
      } else {
        setForeground(Color.BLACK);
        setIcon(normal_icon);
      }
    } else {
      setForeground(Color.GRAY);
      setIcon(impossible_state_icon);
    }
    return result;
  }
}
