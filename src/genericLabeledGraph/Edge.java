package genericLabeledGraph;

/**
 * 
 * @param <N>
 *          The class of the nodes.
 * @param <L>
 *          The class of the label on the edges.
 */
public class Edge<N, L> {

  public final N from, to;
  public final L label;

  public Edge(N from, N to, L label) {
    this.from = from;
    this.to = to;
    this.label = label;
  }

  public boolean equals(Edge<N, L> hedge) {
    if (hedge.from.equals(from) && hedge.to.equals(to)
        && hedge.label.equals(label)) {
      return true;
    } else {
      return false;
    }
  }
}
