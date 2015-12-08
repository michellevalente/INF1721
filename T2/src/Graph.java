import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Graph class.
 * @author Carlos Mattoso, Gabriel Barros, Michelle Valente
 */
public class Graph {
	/**
	 * Stores an (undirected) edge ready to be used in a residual graph.
	 */
	public final static class Edge {
		public int source, target;
		public int capacity;

		public Edge(int source, int target, int capacity) {
			this.source = source;
			this.target = target;
			this.capacity = capacity;
		}
	}

	public final Map<Integer, List<Edge>> adjacencies; // adjacency list
	public Map<Integer, Set<Integer>> partitions; // partition -> vertices
	public int[] vertex_partition; // vertex -> partition

	public Graph(Map<Integer, List<Edge>> relations, Map<Integer, Set<Integer>> partitions, int[] vertex_partition) {
		this.adjacencies = relations;
		this.partitions = partitions;
		this.vertex_partition = vertex_partition;
	}

	/**
	 * Shallow copy. Original objects are preserved.
	 *
	 * @param source
	 * 		Original graph to be copied.
	 */
	public Graph(Graph source) {
		this(source.adjacencies, source.partitions, source.vertex_partition);
	}

	/**
	 * Deep copy. Create a new version of each underlying object.
	 */
	public Graph deepCopy(Graph source) {
		Map<Integer, List<Edge>> adjacencies = new HashMap<>();
		Map<Integer, Set<Integer>> partitions = new HashMap<>();

		source.adjacencies.entrySet().forEach(vertexEdges -> {
			List<Edge> edgesCopy = new ArrayList<Edge>();

			vertexEdges.getValue().forEach(edge -> edgesCopy.add(edge));

			adjacencies.put(vertexEdges.getKey(), edgesCopy);
		});

		source.partitions.forEach((partitionIdx, vertices) ->
			partitions.put(partitionIdx, new HashSet<Integer>(vertices))
		);

		return new Graph(adjacencies, partitions, source.vertex_partition.clone());
	}
}
