package cs1501_p4;
import java.util.ArrayList;

import java.io.*;

public class NetAnalysis implements NetAnalysis_Inter {
    private int V;
    private int E;
    // Adjacency list to represent graph and the edges that exist.
    private ArrayList<Integer>[] adj;
    private ArrayList<Integer>[] lengths;
    private ArrayList<String>[] cableType;
    private ArrayList<Integer>[] bandwidths;

    public NetAnalysis(String filename) {
        E = 0;
        V = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String networkData;
            V = Integer.parseInt(br.readLine());
            adj = (ArrayList<Integer>[]) new ArrayList[V];
            lengths = (ArrayList<Integer>[]) new ArrayList[V];
            cableType = (ArrayList<String>[]) new ArrayList[V];
            bandwidths = (ArrayList<Integer>[]) new ArrayList[V];

            for (int i = 0; i < V; i++) {
                adj[i] = new ArrayList<Integer>();
                lengths[i] = new ArrayList<Integer>();
                cableType[i] = new ArrayList<String>();
                bandwidths[i] = new ArrayList<Integer>();
            }
            while ((networkData = br.readLine()) != null) {
                E++;
                String[] edgeData = networkData.split(" ");
                adj[Integer.parseInt(edgeData[0])].add(Integer.parseInt(edgeData[1]));
                adj[Integer.parseInt(edgeData[1])].add(Integer.parseInt(edgeData[0]));
                cableType[Integer.parseInt(edgeData[0])].add(edgeData[2]);
                cableType[Integer.parseInt(edgeData[1])].add(edgeData[2]);
                bandwidths[Integer.parseInt(edgeData[0])].add(Integer.parseInt(edgeData[3]));
                bandwidths[Integer.parseInt(edgeData[1])].add(Integer.parseInt(edgeData[3]));
                lengths[Integer.parseInt(edgeData[0])].add(Integer.parseInt(edgeData[4]));
                lengths[Integer.parseInt(edgeData[1])].add(Integer.parseInt(edgeData[4]));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("An IO Exception has occured.");
        }
    }
    /**
     * Find the lowest latency path from vertex `u` to vertex `w` in the graph
     *
     * @param u Starting vertex
     * @param w Destination vertex
     *
     * @return ArrayList<Integer> A list of the vertex id's representing the
     *         path (should start with `u` and end with `w`)
     *         Return `null` if no path exists
     */
    public ArrayList<Integer> lowestLatencyPath(int u, int w) {
        ArrayList<Integer> path = new ArrayList<Integer>();
        double[] latencyTo = new double[V];
        int[] prev = new int[V];
        int[] visited = new int[V];

        for (int i = 0; i < V; i++) {
            latencyTo[i] = Double.POSITIVE_INFINITY;
            prev[i] = 0;
            visited[i] = 0;
        }
        // Priority queue sorted by lowest latency
        IndexableMinPQ pq = new IndexableMinPQ<Double>(V);
        pq.insert(u, getLatency(u, u));
        latencyTo[u] = 0.0;
        while (!pq.isEmpty()) {
            int v = pq.deleteMin();
            while (visited[v] == 1) {
                if (!pq.isEmpty()) v = pq.deleteMin();
                else break;
            }
            visited[v] = 1;
            for (int i = 0; i < adj[v].size(); i++) {
                if (visited[adj[v].get(i)] == 0 && latencyTo[v] + getLatency(v, adj[v].get(i)) < latencyTo[adj[v].get(i)]) {
                    pq.insert(adj[v].get(i), latencyTo[v] + getLatency(v, adj[v].get(i)));
                    latencyTo[adj[v].get(i)] = latencyTo[v] + getLatency(v, adj[v].get(i)); 
                    prev[adj[v].get(i)] = v;
                }
            }
        }

        int cur = w;
        path.add(cur);
        while (cur != u) {
            path.add(prev[cur]);
            cur = prev[cur];
        }
        for(int i = 0; i < path.size()/2; i++) {
            int temp = path.get(i);
            path.set(i, path.get(path.size()-i-1));
            path.set((path.size()-i-1), temp);
        }

        return path;
    }

    /**
     * Find the bandwidth available along a given path through the graph
     * (the minimum bandwidth of any edge in the path). Should throw an
     * `IllegalArgumentException` if the specified path is not valid for
     * the graph.
     *
     * @param ArrayList<Integer> A list of the vertex id's representing the
     *                           path
     *
     * @return int The bandwidth available along the specified path
     */
    public int bandwidthAlongPath(ArrayList<Integer> p) throws IllegalArgumentException {
        int minBandwidth = Integer.MAX_VALUE;
        for (int i = 0; i < p.size()-1; i++) {
            if (p.get(i) >= adj.length || adj[p.get(i)].contains(p.get(i+1)) == false) {
                throw new IllegalArgumentException("No such path.");
            }
            if (bandwidths[p.get(i)].get(adj[p.get(i)].indexOf(p.get(i+1))) < minBandwidth) {
                minBandwidth = bandwidths[p.get(i)].get(adj[p.get(i)].indexOf(p.get(i+1)));
            }
        }
        return minBandwidth;
    }

    /**
     * Return `true` if the graph is connected considering only copper links
     * `false` otherwise
     *
     * @return boolean Whether the graph is copper-only connected
     */
    public boolean copperOnlyConnected() {
        // 0 if unvisited, 1 if visited
        if (V == 0) return true;
        int[] visited = new int[V];
        int numVisited = 0;
        IndexableMinPQ<Double> pq = new IndexableMinPQ<Double>(V);
        for (int i = 0; i < visited.length; i++) {
            visited[i] = 0;
        }
        pq.insert(0, 0.0);
        while (!pq.isEmpty()) {
            int v = pq.deleteMin();
            if (visited[v] == 0) numVisited++;
            visited[v] = 1;
            for (int i = 0; i < adj[v].size(); i++) {
                if (visited[adj[v].get(i)] == 0) {
                    if (cableType[v].get(adj[v].indexOf(adj[v].get(i))).equals("copper")) {
                        pq.insert(adj[v].get(i), 0.0);
                    }
                }
            }
        }
        if (numVisited == V) return true;
        return false;
    }

    /**
     * Return `true` if the graph would remain connected if any two vertices in
     * the graph would fail, `false` otherwise
     *
     * @return boolean Whether the graph would remain connected for any two
     *         failed vertices
     */
    public boolean connectedTwoVertFail() {
        for (int i = 0; i < adj.length; i++) {
            if (adj[i].size() < 3) return false;
        }
        return true;
    }

    /**
     * Find the lowest average (mean) latency spanning tree for the graph
     * (i.e., a spanning tree with the lowest average latency per edge). Return
     * it as an ArrayList of STE edges.
     *
     * Note that you do not need to use the STE class to represent your graph
     * internally, you only need to use it to construct return values for this
     * method.
     *
     * @return ArrayList<STE> A list of STE objects representing the lowest
     *         average latency spanning tree
     *         Return `null` if the graph is not connected
     */
    public ArrayList<STE> lowestAvgLatST() {
        ArrayList<Integer> path = new ArrayList<Integer>();

        double[] latencyTo = new double[V];
        int[] prev = new int[V];
        int[] visited = new int[V];
        int numVisited = 0;

        for (int i = 0; i < V; i++) {
            latencyTo[i] = Double.POSITIVE_INFINITY;
            prev[i] = 0;
        }
        // Priority queue sorted by lowest latency
        IndexableMinPQ pq = new IndexableMinPQ<Double>(V);
        pq.insert(0, getLatency(0, 0));
        latencyTo[0] = 0.0;
        while (numVisited < V) {
            int v = pq.deleteMin();
            while (visited[v] == 1) {
                v = pq.deleteMin();
            }
            path.add(v);
            visited[v] = 1;
            numVisited++;
            for (int i = 0; i < adj[v].size(); i++) {
                if (visited[adj[v].get(i)] == 0 && getLatency(v, adj[v].get(i)) < latencyTo[adj[v].get(i)]) {
                    pq.insert(adj[v].get(i), getLatency(v, adj[v].get(i)));
                    latencyTo[adj[v].get(i)] = getLatency(v, adj[v].get(i));
                    prev[adj[v].get(i)] = v; 
                }
            }
        }
        
        ArrayList<STE> edgePath = new ArrayList<STE>();

        for (int i = 1; i < path.size(); i++) {
            edgePath.add(new STE(prev[path.get(i)], path.get(i)));
        }

        return edgePath;
    }


    // Helper methods
    private int getSpeed(int v1, int v2) {
        if (cableType[v1].get(adj[v1].indexOf(v2)).equals("optical")) return 230000000;
        else if (cableType[v1].get(adj[v1].indexOf(v2)).equals("copper")) return 200000000;
        return 0;
    } 

    private int getLength(int v1, int v2) {
        return lengths[v1].get(adj[v1].indexOf(v2));
    }

    private double getLatency(int v1, int v2) {
        if (v1 == v2) return 0.0;
        return (double)getLength(v1, v2) / (double)getSpeed(v1, v2);
    }

    // Test helper methods
    public double sumLatencyOnPath(ArrayList<Integer> p) {
        double totalLatency = 0.0;
        
        for (int i = 0; i < p.size()-1; i++) {
            if (p.get(i) >= adj.length || adj[p.get(i)].contains(p.get(i+1)) == false) {
                throw new IllegalArgumentException("No such path.");
            }
            totalLatency += getLatency(p.get(i), p.get(i+1));
        }
        return totalLatency;
    }

    public double sumLatencyOnEdgeList(ArrayList<STE> p) {
        double totalLatency = 0.0;
        for (int i = 0; i < p.size(); i++) {
            if (p.get(i).u >= adj.length || adj[p.get(i).u].contains(p.get(i).w) == false) {
                throw new IllegalArgumentException("No such path.");
            }
            totalLatency += getLatency(p.get(i).u, p.get(i).w);
        }
        return totalLatency;
    }
}