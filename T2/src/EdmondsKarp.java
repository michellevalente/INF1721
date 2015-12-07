import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Edmonds Karp max flow algorithm class.
 * @author Carlos Mattoso, Gabriel Barros, Michelle Valente
 */
public class EdmondsKarp implements IMaximumFlow{

	private double bfs(Graph g, Integer source, Integer target)
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

				if(e.capacity - e.flow > 0 && previous.get(next) == null)
				{
					previous.put(next, current);
					currentCapacity.put(next, Math.min( currentCapacity.get(current),e.capacity - e.flow));

					if(next != target)
					{
						q.add(next);
					}
					else
					{
						Integer v = next;
						while(previous.get(v) != v)
						{
							final Integer u = previous.get(v), prev = v;

							g.adjacencies.get(u).stream()
								.filter(edge -> edge.target == prev)
								.forEach(edge -> edge.flow += currentCapacity.get(target));

							g.adjacencies.get(v).stream()
								.filter(edge -> edge.target == u)
								.forEach(edge -> edge.flow += currentCapacity.get(target));

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
    public Solution solve(Graph g, Integer source, Integer target)
	{
		double maxFlow = 0.0;
		while(true)
		{
			double flow = bfs(g, source, target);

			if(flow == 0)
				break;

			maxFlow+= flow;
		}

		Solution s = new Solution();

		return s;
	}
}

