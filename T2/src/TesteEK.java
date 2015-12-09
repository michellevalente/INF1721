import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class to Edmonds Karp
 *
 *
 * @author Carlos Mattoso, Gabriel Barros, Michelle Valente
 */
public class TesteEK {

	private static void addEdgeToVertex(int u, Graph.Edge e, Graph g) {
	    List<Graph.Edge> neighs = g.adjacencies.get(u);
	    if(neighs == null) {
	      neighs = new ArrayList<>();
	      g.adjacencies.put(u, neighs);
	    }
	    neighs.add(e);
	}

	private static void addEdge(Graph g, int u, int v, int capacity) {
	  Graph.Edge forward = new Graph.Edge(u, v, capacity);
	  Graph.Edge backwards = new Graph.Edge(v, u, capacity);
	  addEdgeToVertex(u, forward, g);
	  addEdgeToVertex(v, backwards, g);
	}
	public static void main(String[] args){

		Map<Integer, List<Graph.Edge>> adjacencies = new HashMap<>();
		Map<Integer, Set<Integer>> partitions = new HashMap<>();
		int[] vertex_partition = new int[7];

		Graph g = new Graph(adjacencies, partitions, vertex_partition);
		addEdge(g, 0, 1, 3);
		addEdge(g, 0, 3, 3);
		addEdge(g, 1, 2, 4);
		addEdge(g, 2, 1, 3);
		addEdge(g, 2, 3, 1);
		addEdge(g, 2, 4, 2);
		addEdge(g, 3, 4, 2);
		addEdge(g, 3, 5, 6);
		addEdge(g, 4, 6, 1);
		addEdge(g, 5, 6, 9);
		addEdge(g, 5, 2, 1);

		Set<Integer> vertices = new HashSet<Integer>();
		for(int i = 0; i < 7; i++)
		{
			g.vertex_partition[i] = 0;
			vertices.add(i);
		}

		g.partitions.put(0,vertices);

		IMaximumFlow flowSolver = new EdmondsKarp();
		IMaximumFlow.Solution solution = flowSolver.solve(g,0,6,0);

		System.out.printf("Max flow: %f\n", solution.maximumFlow);
		System.out.printf("Partition 1:\n");

		for(int v : solution.partition.get(0)) {
		  System.out.print(v);
		  System.out.print(" ");
		}
		System.out.println();

		System.out.printf("Partition 2:\n");
		for(int v : solution.partition.get(1)) {
		  System.out.print(v);
		  System.out.print(" ");
		}
		System.out.println();

	}
}