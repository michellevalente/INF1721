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
	public final Map<Vertex, Edge> relations;

	public Graph(List<Vertex> vertices, List<Edge> edges, Map<Vertex, Edge> relations) {
		this.vertices = vertices;
		this.edges = edges;
		this.relations = relations;
	}

	/**
	 * Shallow copy. Original objects are preserved.
	 *
	 * @param source
	 * 		Original graph to be copied.
	 */
	public Graph(Graph source) {
		this(source.vertices, source.edges, source.relations);
	}

	/**
	 * Deep copy. Create a new version of each underlying object.
	 */
	public Graph deepCopy(Graph source) {
		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Edge> edges = new ArrayList<Edge>();
		Map<Vertex, Edge> relations = new HashMap<Vertex, Edge>();
		Map<Integer, Vertex> indexToVertex = new HashMap<Integer, Vertex>();

		for (Vertex v : source.vertices) {
			Vertex vCopy = new Vertex(v.index, v.partition);
			vertices.add(vCopy);

			indexToVertex.put(vCopy.index, vCopy);
		}

		for (Edge e : source.edges) {
			Vertex sourceVertex = indexToVertex.get(e.source.index),
				   targetVertex = indexToVertex.get(e.target.index);

			Edge eCopy = new Edge(sourceVertex, targetVertex, e.capacity, e.flow);
			edges.add(eCopy);

			relations.put(sourceVertex, eCopy);
			relations.put(targetVertex, eCopy);
		}

		return new Graph(vertices, edges, relations);
	}
}
