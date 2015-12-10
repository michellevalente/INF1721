import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * @author Carlos Mattoso, Gabriel Barros, Michelle Valente
 */
public class PreflowPush implements IMaximumFlow {

	/**
	 *  Finds the set of all vertices reachable from `source` in the final flow network.
	 */
	private Set<Integer> findPartition(Graph g, Integer source, int[][] f) {
		Queue<Integer> q = new LinkedList<>();
		Set<Integer> partS = new HashSet<>();

		q.add(source);
		partS.add(source);

		while(!q.isEmpty())
		{
			Integer current = q.remove();

			for(Graph.Edge e : g.adjacencies.get(current))
			{
				int next = e.target;

				/* Just check if we can go to the next vertex (i.e. there's a positive
				 * residual capacity and its in the same partition) and that it's not
				 * already been seen in the search.
				 */
				if (e.capacity - f[current][next] > 0 && !partS.contains(next)
					&& g.vertex_partition[current] == g.vertex_partition[next])
				{
					partS.add(next);
					q.add(next);
				}
			}
		}

		return partS;
	}

	/**
	 * assert e[u] > 0 and h[u] == h[v] + 1
	 * Δ = min(e[u], c[u][v] - f[u][v])
	 * f[u][v] += Δ
	 * f[v][u] -= Δ
	 * e[u] -= Δ
	 * e[v] += Δ
	 *
	 * @param g
	 * 		The graph.
	 * @param u
	 * 		Origin vertex.
	 * @param v
	 * 		Destination vertex.
	 * @param capacity
	 * 		Capacity of edge (u,v).
	 * @param e
	 * 		Excess flows.
	 * @param f
	 * 		Flow network.
	 */
	private void push(Graph g, int u, int v, double capacity, int[] e, int[][] f) {
		double delta = Math.min(e[u], capacity - f[u][v]);
		f[u][v] += delta;
		f[v][u] -= delta;
		e[u] -= delta;
		e[v] += delta;
	}

	/**
	 * relabel(u):
	 *   assert e[u] > 0 and h[u] <= h[v] ∀v such that f[u][v] < c[u][v]
	 *   h[u] = min(h[v] ∀v such that f[u][v] < c[u][v]) + 1
	 *
	 * @param g
	 * 		The graph.
	 * @param u
	 * 		Vertex to relabel.
	 * @param h
	 * 		Array of vertices' heights.
	 * @param f
	 * 		Flow network matrix.
	 */
	private void relabel(Graph g, int u, int[] h, int[][] f) {
		int minNewHeight = Integer.MAX_VALUE;

		// Check all neighbors of `u` within its partition.
		for(Graph.Edge e : g.adjacencies.get(u)) {
			int v = e.target;
			if (e.capacity - f[u][v] > 0
				&& g.vertex_partition[u] == g.vertex_partition[v])
				minNewHeight = Math.min(minNewHeight, h[e.target]);
		}

		h[u] = minNewHeight;
		if (minNewHeight != Integer.MAX_VALUE)
			h[u] = minNewHeight + 1;
	}

	private void discharge(Graph g, int u, int[] h, int[] seen, int[] e, int[][] f) {
		while (e[u] > 0) {
			int nNeighbors = g.adjacencies.get(u).size();

			/* Try to push all neighbors of `u` that belong to its partition,
			 * until all've been seen.
			 */
			if (seen[u] < nNeighbors) {
				Graph.Edge edge = g.adjacencies.get(u).get(seen[u]);
				int v = edge.target;

				if (edge.capacity - f[u][v] > 0 && h[u] > h[v]
					&& g.vertex_partition[u] == g.vertex_partition[v])
					push(g, u, v, edge.capacity, e, f);
				else
					seen[u]++;
			}
			else { // all neighbors of `u` have been checked
				relabel(g, u, h, f);
				seen[u] = 0; // `u` has been relabeled, so we can check its neighbors again.
			}
		}
	}

	@Override
    public Solution solve(Graph g, Integer source, Integer target, Integer p)
	{
		int n = g.adjacencies.size(); // Number of vertices in partition `p`.
		int[] seen = new int[n+1];
		int[][] flows = new int[n+1][n+1];
		int[] height = new int[n+1]; // heights
		int[] excess = new int[n+1]; // excess flows

		Queue<Integer> nodeList = new ConcurrentLinkedQueue<Integer>();

		final Set<Integer> partS; // all vertices reachable from S
		final Set<Integer> partT; // all vertices reachable from T
		Map<Integer, Set<Integer>> partition = new HashMap<>();

		// Create a preflow `f` that saturates all out-edges of `source`
		height[source] = n; // Longest path from source to sink is less than `n` long.
		excess[source] = Integer.MAX_VALUE; // Send as much flow as possible to neighbors of source.
		for (Graph.Edge edge : g.adjacencies.get(source)) {
			push(g, source, edge.target, edge.capacity, excess, flows);
		}

		// Insert all vertices in partition `p` into the list of nodes to be seen
		// (except for `source` and `target`.)
		for (int i = 1; i <= n; i++) {
			if (g.vertex_partition[i] == p && i != source && i != target)
				nodeList.add(i);
		}

		// Go over the list of nodes to be processed.
		Iterator<Integer> current = nodeList.iterator();
		while (current.hasNext()) {
			int u = current.next();
			int oldHeight = height[u];

			// A discharge only happens if `u` is still an active node.
			discharge(g, u, height, seen, excess, flows);

			/*
			 * If the height of a vertex `u` has increased, it means the discharge operation
			 * had to relabel `u` in order to zero its excess. So we go back to the start of
			 * the list to potentially discharge other vertices.
			 */
			if (height[u] > oldHeight) {
				nodeList.add(u);
				current.remove();
				current = nodeList.iterator();
			}
		}

		// Find the vertices that are reachable from `source`
		partS = findPartition(g, source, flows);

		/* Add all vertices from `source`s original partition but not in `partS` to
		 * the `target` partition i.e. `partT`.
		 */
		partT = g.partitions.get(g.vertex_partition[source]).stream()
					.filter(v -> !partS.contains(v)) // v ∉ partS
					.collect(Collectors.toSet()); // partT ∪ {v}

		partition.put(p, partS);
		partition.put(p + 1, partT);

		Solution s = new Solution();
		s.maximumFlow = excess[target]; //Arrays.stream(flows[source]).sum();
		s.partition = partition;

		return s;
	}
}
