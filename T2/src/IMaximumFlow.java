import java.util.Map;
import java.util.Set;

/**
 * Specification for algorithm that finds the maximum flow on graph `g`.
 */
public interface IMaximumFlow {
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
	public Solution solve(Graph g, Integer source, Integer target, Integer p);
}
