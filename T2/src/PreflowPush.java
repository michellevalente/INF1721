import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Carlos Mattoso, Gabriel Barros, Michelle Valente
 */
public class PreflowPush implements IMaximumFlow {

	/**
	 *  Finds the set of all vertices reachable from `source` in the final flow network.
	 */
	private Set<Integer> findPartition(Graph g, Integer source, double[][] f) {
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

				// Just check if we can go to the next vertex and that it's not already seen
				if (e.capacity - f[current][next] > 0 && !partS.contains(next))
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
	private void push(Graph g, int u, int v, double capacity, double[] e, double[][] f) {
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
	private void relabel(Graph g, int u, int[] h, double[][] f) {
		int minNewHeight = Integer.MAX_VALUE;

		for(Graph.Edge e : g.adjacencies.get(u)) {
			if (e.capacity - f[u][e.target] > 0)
				minNewHeight = Math.min(minNewHeight, h[e.target]);
		}

		if (minNewHeight != Integer.MAX_VALUE)
			h[u] = minNewHeight + 1;
	}

	@Override
    public Solution solve(Graph g, Integer source, Integer target, Integer p)
	{
		int n = g.partitions.get(p).size(); // Number of vertices in partition `p`.
		double[][] flows = new double[n][n];
		int[] height = new int[n]; // heights
		double[] excess = new double[n]; // excess flows
		Set<Integer> activeVertices = new HashSet<>(); // active vertices i.e. e(u) > 0

		final Set<Integer> partS; // all vertices reachable from S
		final Set<Integer> partT; // all vertices reachable from T
		Map<Integer, Set<Integer>> partition = new HashMap<>();

		// Preprocess
		Arrays.fill(height, 0); // h[v] = 0 ∀v ∈ (V_p \ {s})
		Arrays.fill(excess, 0.0); // initially no vertex has flows

		// Create a preflow `f` that saturates all out-edges of `source`
		height[source] = n; // Longest path from source to sink is less than `n` long.
		excess[source] = Double.MAX_VALUE; // Send as much flow as possible to neighbors of source.
		for (Graph.Edge edge : g.adjacencies.get(source)) {
			push(g, source, edge.target, edge.capacity, excess, flows);
			activeVertices.add(edge.target); // initially, only the neighbors of `source`s have positive excess flows
		}


		while (activeVertices.size() > 0) {
			int u = activeVertices.iterator().next();

			// try to Push.
			boolean pushed = false;
			for(Graph.Edge edge : g.adjacencies.get(u)) {
				int v = edge.target;

				if (excess[u] > 0 && height[u] == height[v] + 1) {
					push(g, u, v, edge.capacity, excess, flows);
					pushed = true;

					// If any flow was pushed onto `v`, then it is now active.
					if (excess[v] > 0)
						activeVertices.add(v);
				}
			}

			// If couldn't push, Relabel the vertex.
			if (!pushed && excess[u] > 0)
				relabel(g, u, height, flows);


			// If the vertex now has 0 excess, it's no longer active
			if (excess[u] == 0)
				activeVertices.remove(u);
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
		s.maximumFlow = excess[target];
		s.partition = partition;

		return s;
	}
}
