import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Specification for algorithm that finds the maximum flow on graph `g`.
 */
public abstract class IMaximumFlow {
	/**
	 * Contains the partition and the value of the maximum flow.
	 */
	public static class Solution {
		Map<Integer, Set<Integer>> partition;
		int maximumFlow;

		public Solution(int maxFlow, Map<Integer, Set<Integer>> partition) {
			this.maximumFlow = maxFlow;
			this.partition = partition;
		}

		public Solution() {
			this(Integer.MAX_VALUE, null);
		}
	}

	/**
	 * Find the maximum flow in g.
	 *
	 * @param g
	 * 		Graph on which to find maximum flow.
	 * @param source
	 * 		Source vertex.
	 * @param target
	 * 		Target (aka sink) vertex.
	 * @return
	 * 		The partition due to the maximum flow on `g` from `source` to `target`.
	 */
	public abstract Solution solve(Graph g, Integer source, Integer target);

	/**
	 * Given a flow network `f` and the `source` vertex, repartitions the graph `g`
	 * into the sets of vertices reachable and unreachable from `source`.
	 *
	 * @param g
	 * 		The original graph.
	 * @param source
	 * 		The source vertex.
	 * @param p
	 * 		The partition to which `source` belongs.
	 * @param maximumFlow
	 * 		The maximum flow found in `f`.
	 * @param f
	 * 		Flow network.
	 * @return
	 * 		The found partitions and the maximum flow.
	 */
	protected Solution repartition(Graph g, int source, int maximumFlow, int[][] f) {
		final Set<Integer> partS; // all vertices reachable from S
		final Set<Integer> partT; // all vertices reachable from T
		Map<Integer, Set<Integer>> partition = new HashMap<>(); // partition -> vertices mappings

		// Find the vertices that are reachable from `source`
		partS = findSourcePartition(g, source, f);

		/* Add all vertices from `source`s original partition but not in `partS` to
		 * the `target` partition i.e. `partT`.
		 */
		partT = g.partitions.get(g.vertex_partition[source]).stream()
					.filter(v -> !partS.contains(v)) // v ∉ partS
					.collect(Collectors.toSet()); // partT ∪ {v}

		int p = g.vertex_partition[source];
		partition.put(p, partS);
		partition.put(p + 1, partT);

		Solution s = new Solution();
		s.maximumFlow = maximumFlow; //Arrays.stream(flows[source]).sum();
		s.partition = partition;

		return s;
	}

	/**
	 * Finds the set of all vertices reachable from `source` in the final flow network.
	 *
	 * @param g
	 * 		The original graph.
	 * @param source
	 * 		The source vertex.
	 * @param f
	 * 		The flow network.
	 * @return
	 * 		The set of vertices reachable from `source`.
	 */
	private Set<Integer> findSourcePartition(Graph g, int source, int[][] f) {
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

}
