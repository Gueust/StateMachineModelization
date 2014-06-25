package genericLabeledGraph;

/**
 * 
 * @param <N>
 *          The class of the nodes.
 * @param <L>
 *          The class of the label on the edges.
 */
public class Edge<N, L> {

  public N from, to;
  public L label;

  public Edge(N from, N to, L label) {
    this.from = from;
    this.to = to;
    this.label = label;
  }
}
