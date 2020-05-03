import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {

    private final Digraph graph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph g) {
        this.graph = new Digraph(g);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        return process(
            new BreadthFirstDirectedPaths(graph, v),
            new BreadthFirstDirectedPaths(graph, w)
        )[0];
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        return process(
                new BreadthFirstDirectedPaths(graph, v),
                new BreadthFirstDirectedPaths(graph, w)
        )[1];
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException();

        return process(
                new BreadthFirstDirectedPaths(graph, v),
                new BreadthFirstDirectedPaths(graph, w)
        )[0];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException();

        return process(
                new BreadthFirstDirectedPaths(graph, v),
                new BreadthFirstDirectedPaths(graph, w)
        )[1];
    }

    private int[] process(BreadthFirstDirectedPaths bfs1, BreadthFirstDirectedPaths bfs2) {
        // length, ancestor
        int[] result = {-1, -1};

        for (int i = 0; i < graph.V(); i++) {
            if (!bfs1.hasPathTo(i) || !bfs2.hasPathTo(i)) continue;

            int length = bfs1.distTo(i) + bfs2.distTo(i);

            if (result[0] == -1 || result[0] > length) {
                result[0] = length;
                result[1] = i;
            }
        }

        return result;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}