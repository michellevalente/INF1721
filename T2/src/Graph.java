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
	 * Stores a vertex's index and its partition index.
	 */
	public final class Vertex {
		public int index, partition;

		public Vertex(int index, int partition) {
			this.index = index;
			this.partition = partition;
		}
	}

	/**
	 * Stores an edge ready to be used in a residual graph.
	 */
	public final class Edge {
		public Vertex source, target;
		public double capacity, flow;

		public Edge(Vertex source, Vertex target, double capacity, double flow) {
			this.source = source;
			this.target = target;
			this.capacity = capacity;
			this.flow = flow;
		}
	}

	public final List<Edge> edges;
	public final List<Vertex> vertices;
	public final Map<Vertex, List<Edge>> adjacencies;
	public final Map<Integer, List<Vertex>> partitions;

	public Graph(List<Vertex> vertices, List<Edge> edges, Map<Vertex, List<Edge>> relations,
			     Map<Integer, List<Vertex>> partitions) {
		this.vertices = vertices;
		this.edges = edges;
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
		this(source.vertices, source.edges, source.adjacencies, source.partitions);
	}

	/**
	 * Deep copy. Create a new version of each underlying object.
	 */
	public Graph deepCopy(Graph source) {
		List<Vertex> vertices = new ArrayList<>();
		List<Edge> edges = new ArrayList<>();
		Map<Vertex, List<Edge>> adjacencies = new HashMap<>();
		Map<Integer, List<Vertex>> partitions = new HashMap<>();

		Map<Integer, Vertex> indexToVertex = new HashMap<>();

		for (Vertex v : source.vertices) {
			Vertex vCopy = new Vertex(v.index, v.partition);
			vertices.add(vCopy);

			// Assume the vertex's `partition` attribute is consistent.
			List<Vertex> partition = partitions.get(vCopy.partition);
			if (partition != null)
				partition.add(vCopy);
			else {
				partition = new ArrayList<Vertex>();

				partition.add(vCopy);

				partitions.put(vCopy.partition, partition);
			}

			indexToVertex.put(vCopy.index, vCopy);
		}

		for (Edge e : source.edges) {
			Vertex sourceVertex = indexToVertex.get(e.source.index),
				   targetVertex = indexToVertex.get(e.target.index);

			Edge eCopy = new Edge(sourceVertex, targetVertex, e.capacity, e.flow);
			edges.add(eCopy);

			addEdge(adjacencies, sourceVertex, eCopy);
			addEdge(adjacencies, targetVertex, eCopy);
		}

		return new Graph(vertices, edges, adjacencies, partitions);
	}

	private void addEdge(Map<Vertex, List<Edge>> adjacencies, Vertex v, Edge e) {
		List<Edge> adjacencyList = adjacencies.get(v);

		if (adjacencyList != null)
			adjacencyList.add(e);
		else {
			adjacencyList = new ArrayList<Edge>();
			adjacencyList.add(e);
			adjacencies.put(v, adjacencyList);
		}
	}
}
