import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.TreeMap;

public class WordNet {

    private final SAP sap;
    private int v = 0;
    private final TreeMap<String, ArrayList<Integer>> nounsByName = new TreeMap<>();
    private final ArrayList<String> nounsList = new ArrayList<>();

    private Digraph graph;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException();

        parseSynsets(synsets);
        parseHypernyms(hypernyms);

        DirectedCycle cycle = new DirectedCycle(graph);

        if (cycle.hasCycle())
            throw new IllegalArgumentException();

        sap = new SAP(graph);
    }

    private void parseSynsets(String filepath) {
        In in = new In(filepath);
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] fields = line.split(",");
            int id = Integer.parseInt(fields[0]);
            String[] nouns = fields[1].split(" ");

            for (String noun : nouns) {
                if (nounsByName.containsKey(noun)) {
                    ArrayList<Integer> ids = nounsByName.get(noun);
                    if (ids.add(id))
                        nounsByName.put(noun, ids);
                } else {
                    ArrayList<Integer> ids = new ArrayList<>();
                    if (ids.add(id))
                        nounsByName.put(noun, ids);
                }
            }

            nounsList.add(fields[1]);

            v++;
        }
    }

    private void parseHypernyms(String filepath) {
        graph = new Digraph(v);
        In in = new In(filepath);

        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] fields = line.split(",");
            int id = Integer.parseInt(fields[0]);

            for (int i = 1; i < fields.length; i++) {
                String hypernym = fields[i];
                graph.addEdge(id, Integer.parseInt(hypernym));
            }
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounsByName.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new NullPointerException();

        return nounsByName.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException();

        return sap.length(nounsByName.get(nounA), nounsByName.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException();

        int ancestor = sap.ancestor(nounsByName.get(nounA), nounsByName.get(nounB));
        return nounsList.get(ancestor);
    }
}
