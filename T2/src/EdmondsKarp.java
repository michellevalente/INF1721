import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Edmonds Karp max flow algorithm class.
 *
 * @author Carlos Mattoso, Gabriel Barros, Michelle Valente
 */
public class EdmondsKarp extends IMaximumFlow {

	private int bfs(Graph g, int source, int target, int[][] f)
	{
		Queue<Integer> q = new LinkedList<>();
		int n = g.adjacencies.size();
		Integer[] previous = new Integer[n+1];
		int[] currentCapacity = new int[n+1];

		// Initialize the state for all vertices in `source`s partition.
		// Aqui na verdade a gente opera apenas sobre os vértices da partição em que source se encontra inicialmente.
		Arrays.fill(previous, null);
		Arrays.fill(currentCapacity, Integer.MAX_VALUE);

		previous[source] = source;
		q.add(source);

		while(!q.isEmpty())
		{
			int current = q.remove();

			for(Graph.Edge e : g.adjacencies.get(current))
			{
				int next = e.target;

				if(e.capacity - f[current][next] > 0 && previous[next] == null
					&& g.vertex_partition[current] == g.vertex_partition[next])
				{
					previous[next] = current;
					currentCapacity[next] = Math.min(currentCapacity[current], e.capacity - f[current][next]);

					if(next != target)
					{
						q.add(next);
					}
					else // backtrack from `target` to `source`
					{
						int v = next; // v == target
						int df = currentCapacity[target];

						while(previous[v] != v)
						{
							int u = previous[v]; // u -> v

							f[u][v] += df;
							f[v][u] -= df;

							v = u;
						}

						return df;
					}
				}
			}
		}

		return 0;
	}

	@Override
    public Solution solve(Graph g, Integer source, Integer target)
	{
		int maxFlow = 0;
		int n = g.adjacencies.size();
		int[][] f = new int[n+1][n+1];

		while (true)
		{
			double flow = bfs(g, source, target, f);
			//System.out.printf("flow: %f\n", flow);
			if (flow == 0)
				break;
			maxFlow += flow;
		}

		return repartition(g, source, maxFlow, f);
	}
}