import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		int[] vertex_partition = new int[8];

		Graph g = new Graph(adjacencies, partitions, vertex_partition);
		addEdge(g, 1, 2, 3);
		addEdge(g, 1, 4, 3);
		addEdge(g, 2, 3, 4);
		addEdge(g, 3, 1, 3);
		addEdge(g, 3, 4, 1);
		addEdge(g, 3, 5, 2);
		addEdge(g, 4, 5, 2);
		addEdge(g, 4, 6, 6);
		addEdge(g, 5, 7, 1);
		addEdge(g, 6, 7, 9);
		addEdge(g, 6, 3, 1);

		Set<Integer> vertices = new HashSet<Integer>();
		for(int i = 1; i <= 7; i++)
		{
			g.vertex_partition[i] = 1;
			vertices.add(i);
		}
		g.partitions.put(1, vertices);

		IMaximumFlow flowSolver = new EdmondsKarp();
		IMaximumFlow.Solution solution = flowSolver.solve(g,1,7,1);

		System.out.printf("Max flow: %f\n", solution.maximumFlow);
		System.out.printf("Partition 1:\n");

		for(int v : solution.partition.get(1)) {
		  System.out.print(v);
		  System.out.print(" ");
		}
		System.out.println();

		System.out.printf("Partition 2:\n");
		for(int v : solution.partition.get(2)) {
		  System.out.print(v);
		  System.out.print(" ");
		}
		System.out.println();

	}
}