package genericLabeledGraph;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @param <N>
 *          The class of the nodes.
 * @param <L>
 *          The class of the label.
 */
public class Node<N, L> implements Iterable<Edge<N, L>> {

  public LinkedList<Edge<N, L>> transitions = new LinkedList<>();

  public Node() {

  }

  @Override
  public Iterator<Edge<N, L>> iterator() {
    return transitions.iterator();
  }
}
