import java.util.List;
import java.util.Map;

/**
 * Specification for algorithm that finds the maximum flow on graph `g`.
 */
public interface IMaximumFlow {
	/**
	 * Contains the partition and the value of the maximum flow.
	 */
	public class Solution {
		Map<Integer, List<Graph.Vertex>> partition;
		double maximumFlow;
	}

	/**
	 * Find the maximum
	 * @param g
	 * 		Graph on which to find maximum flow.
	 * @param source
	 * 		Source vertex.
	 * @param target
	 * 		Target (aka sink) vertex.
	 * @return
	 * 		The partition due to the maximum flow on `g` from `source` to `target`.
	 */
	public Solution solve(Graph g, Graph.Vertex source, Graph.Vertex target);
}
