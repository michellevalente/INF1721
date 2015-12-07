import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Given an input graph finds the K most distant clusters.
 */
public class Clusterizer {
	/**
	 * Stores the value of the cut separating partitions of indices `original`
	 * and `created`. `created` is the index of the new partition producing by
	 * splitting the vertices in `original`.
	 */
	public class PartitionPair {
		int original, created;
		double cutValue;
	}

	/**
	 * Takes in an implementation of a maximum flow algorithm and
	 * uses it to find the `k` most distant clusters that exist in
	 * the graph `g`.
	 *
	 * As a side effect, modifies the input graph by partitioning
	 * its vertices.
	 * @param g
	 * 		Graph on which to find clusters.
	 * @param totalK
	 * 		Number of clusters to be found.
	 * @param flowSolver
	 * 		Implementation of maximum flow algorithm.
	 * @return
	 * 		List of partitions with the values of their cuts.
	 */
	public List<PartitionPair> findClusters(Graph g, int totalK, IMaximumFlow flowSolver) {
		/*
		- Input:  G = {V, E}, K (número de partições a serem feitas)
		- Output: as K partições e os máximos cortes mínimos

		K_atual = K_ini = 1

		Enquanto houver menos de `K` grupos:
		  forEach: (K_ini .. K_atual) -> i {
		    forEach: (stream de pares (u,v), u != v, (u,v) pertence ao grupo `i`) -> par (u,v) {
		      Usar o algoritmo de fluxo máximo entre `u` e `v`
		    } reduce {
		      Escolher a partição de máximo corte mínimo (fluxo máximo == corte mínimo)
		      =>> Aqui faremos `flowSolver.solve(g, s, t)`
		    }

		    output: melhor partição encontrada (partição de máximo corte mínimo)
		  } reduce {
		    Cada grupo elegeu uma nova partição
		    Escolha a melhor partição que tem o maior corte mínimo
		  }

		  K_atual++
		  Reparticione os vértices dada a partição escolhida

		Apresente o output pro poggi ficar feliz
		*/
		int k = 1; // last assigned cluster index - initially all together

		while (k <= totalK) {
			List<IMaximumFlow.Solution> partitions = new ArrayList<>(k);
			int[] clustersIds = new int[k];

			for(int i = 1; i <= k; i++)
				clustersIds[i-1] = i;

			// Each cluster elects its optimal partition.
			Arrays.stream(clustersIds).parallel().forEach(clusterId -> {
				// Execute the flow algorithm for each pair of vertices in `clusterIdx`
				for (Integer source : g.partitions.get(clusterId)) {

				}
			});

			// Choose the top partition out of all elected.
			double maxFlow = 0.0;
			int originalPartition = 1;
			IMaximumFlow.Solution optimalPartition = null;
			for (int i = 1; i <= k; i++) {
				IMaximumFlow.Solution partition = partitions.get(i);

				if (partition.maximumFlow > maxFlow) {
					maxFlow = partition.maximumFlow;
					optimalPartition = partition;
					originalPartition = i;
				}
			}

			// Repartition the graph.
			for (Map.Entry<Integer, List<Integer>> partition : optimalPartition.partition.entrySet()) {
				/* In splitting, keep a partition with the original index and the new one
				 * with the index of `k+1`.
				 */
				int partitionIndex = partition.getKey();
				if (partitionIndex != originalPartition)
					partitionIndex = k+1;

				g.partitions.put(partitionIndex, partition.getValue());
			};

			k++;
		}

		return null;
	}
}
