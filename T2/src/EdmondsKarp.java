import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;

/**
 * Edmonds Karp max flow algorithm class.
 * @author Carlos Mattoso, Gabriel Barros, Michelle Valente
 */
public class EdmondsKarp implements IMaximumFlow{

	private double bfs(Graph g, Integer source, Integer target, double[][] f, List<Integer> part1)
	{
		Queue<Integer> q = new LinkedList<>();
		Map<Integer, Integer>  previous = new HashMap<>();
		Map<Integer, Double>  currentCapacity = new HashMap<>();

		int n = g.adjacencies.size();
		for (int i = 1; i <= n; i++) {
			previous.put(i, null);
			currentCapacity.put(i, Double.MAX_VALUE);
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
					else
					{
						Integer v = next;
						part1.add(v);

						while(previous.get(v) != v)
						{
							Integer u = previous.get(v);
							part1.add(u);

							for(Graph.Edge edge : g.adjacencies.get(u))
							{
								if(edge.target == v )
									f[u][v] +=  currentCapacity.get(target);
							}
							for(Graph.Edge edge : g.adjacencies.get(v))
							{
								if(edge.target == u )
									f[v][u] +=  currentCapacity.get(target);
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

	@Override
    public Solution solve(Graph g, Integer source, Integer target, Integer p)
	{
		double maxFlow = 0.0;
		int n = g.adjacencies.size();
		double[][] f = new double[n][n];
		List<Integer> part1 = new ArrayList<Integer>();
		List<Integer> part2 = new ArrayList<Integer>();
		Map<Integer, List<Integer>> partition = new HashMap<>(); 

		while(true)
		{
			double flow = bfs(g, source, target, f, part1);

			for(Integer v : g.adjacencies.keySet())
			{
				if(!part1.contains(v))
				{
					part2.add(v);
					g.vertex_partition[v] = p;
				}
				else
				{
					g.vertex_partition[v] = p++;
				}

			}

			if(flow == 0)
				break;

			maxFlow+= flow;
		}

		partition.put(p, part1);
		partition.put(p++, part2);
		Solution s = new Solution();

		s.maximumFlow = maxFlow;
		s.partition = partition;

		return s;
	}
}

