import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Edmonds Karp max flow algorithm class.
 *
 * @author Carlos Mattoso, Gabriel Barros, Michelle Valente
 */
public class EdmondsKarp implements IMaximumFlow{

	private double bfs(Graph g, Integer source, Integer target, double[][] f)
	{
		Queue<Integer> q = new LinkedList<>();
		Map<Integer, Integer>  previous = new HashMap<>();
		Map<Integer, Double>  currentCapacity = new HashMap<>();

		// Initialize the state for all vertices in `source`s partition.
		// Aqui na verdade a gente opera apenas sobre os vértices da partição em que source se encontra inicialmente.
		for (int v : g.partitions.get(g.vertex_partition[source])) {
			previous.put(v, null);
			currentCapacity.put(v, Double.MAX_VALUE);
		}

		previous.put(source, source);

		q.add(source);

		while(!q.isEmpty())
		{
			Integer current = q.remove();

			for(Graph.Edge e : g.adjacencies.get(current))
			{
				Integer next = e.target;
				if(e.capacity - f[current][next] > 0 && previous.get(next) == null
					&& g.vertex_partition[current] == g.vertex_partition[next])
				{
					previous.put(next, current);
					currentCapacity.put(next, Math.min( currentCapacity.get(current),e.capacity - f[current][next]));

					if(next != target)
					{
						q.add(next);
					}
					else // backtrack from `target` to `source`
					{
						Integer v = next; // v == target

						while(previous.get(v) != v)
						{
							Integer u = previous.get(v); // u -> v

							for(Graph.Edge edge : g.adjacencies.get(u))
							{
								if (edge.target == v)
									f[u][v] += currentCapacity.get(target);
							}

							for(Graph.Edge edge : g.adjacencies.get(v))
							{
								// No caso da edge reversa consideramos que o fluxo tornou-se negativo.
								// Isso cria um grafo residual implícito.
								if(edge.target == u)
									 f[v][u] = -f[u][v];
							}

							v = u;
						}

						return currentCapacity.get(target);
					}
				}
			}
		}
		return 0;
	}

	/**
	 *  Finds the set of all vertices reachable from `source` in the final flow network.
	 */
	private Set<Integer> findPartition(Graph g, Integer source, double[][] f) {
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
				if (e.capacity - f[current][next] > 0 && !partS.contains(next))
				{
					partS.add(next);
					q.add(next);
				}
			}
		}

		return partS;
	}

	@Override
    public Solution solve(Graph g, Integer source, Integer target, Integer p)
	{
		double maxFlow = 0.0;
		int n = g.partitions.get(p).size();
		double[][] f = new double[n][n];
		final Set<Integer> partS; // all vertices reachable from S
		final Set<Integer> partT; // all vertices reachable from T
		// Mudei pra set pq temos que adicionar quem não pertence a S a T. Set busca mais rápido que lista.
		Map<Integer, Set<Integer>> partition = new HashMap<>();

		while (true)
		{
			double flow = bfs(g, source, target, f);
			System.out.printf("flow: %f\n", flow);
			if (flow == 0)
				break;
			maxFlow += flow;
		}

		// Find the vertices that are reachable from `source`
		// Achar as partições deve ser feito após terminar o loop acima. partS tem todo mundo
		// que dá pra alcançar de `source` e partT todo mundo alcançável de `target`. Como
		// algumas arestas são saturadas nem todo mundo é alcançado.
		partS = findPartition(g, source, f);

		/* Add all vertices from `source`s original partition but not in `partS` to
		 * the `target` partition i.e. `partT`.
		 */
		partT = g.partitions.get(g.vertex_partition[source]).stream()
			.filter(v -> !partS.contains(v)) // v ∉ partS
			.collect(Collectors.toSet()); // partT ∪ {v}

		partition.put(p, partS);
		partition.put(p + 1, partT);

		Solution s = new Solution();
		s.maximumFlow = maxFlow;
		s.partition = partition;

		return s;
	}
}