package genericLabeledGraph;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * @param <N>
 *          The class of the nodes.
 * @param <L>
 *          The class of the label.
 */
public class Node<N, L> {

  public LinkedHashMap<L, LinkedList<Edge<N, L>>> transitions = new LinkedHashMap<>();

  public Node() {

  }

  public void add(Edge<N, L> hedge) {
    LinkedList<Edge<N, L>> list = transitions.get(hedge.label);
    if (list == null) {
      list = new LinkedList<Edge<N, L>>();
    }
    list.add(hedge);
  }

}
