import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Graph class.
 * @author Carlos Mattoso, Gabriel Barros, Michelle Valente
 */
public class Graph {
	/**
	 * Stores an edge ready to be used in a residual graph.
	 */
	public final class Edge {
		public int source, target;
		public double capacity, flow;

		public Edge(int source, int target, double capacity, double flow) {
			this.source = source;
			this.target = target;
			this.capacity = capacity;
			this.flow = flow;
		}
	}

	public final Map<Integer, List<Edge>> adjacencies; // adjacency list
	public Map<Integer, List<Integer>> partitions; // partition -> vertices
	public Integer[] vertex_partition; // vertice -> partition

	public Graph(Map<Integer, List<Edge>> relations, Map<Integer, List<Integer>> partitions) {
		this.adjacencies = relations;
		this.partitions = partitions;
	}

	/**
	 * Shallow copy. Original objects are preserved.
	 *
	 * @param source
	 * 		Original graph to be copied.
	 */
	public Graph(Graph source) {
		this(source.adjacencies, source.partitions);
	}

	/**
	 * Deep copy. Create a new version of each underlying object.
	 */
	public Graph deepCopy(Graph source) {
		Map<Integer, List<Edge>> adjacencies = new HashMap<>();
		Map<Integer, List<Integer>> partitions = new HashMap<>();

		source.adjacencies.entrySet().forEach(vertexEdges -> {
			List<Edge> edgesCopy = new ArrayList<Edge>();

			vertexEdges.getValue().forEach(edge -> edgesCopy.add(edge));

			adjacencies.put(vertexEdges.getKey(), edgesCopy);
		});

		source.partitions.forEach((partitionIdx, vertices) ->
			partitions.put(partitionIdx, new ArrayList<Integer>(vertices))
		);

		return new Graph(adjacencies, partitions);
	}
}
