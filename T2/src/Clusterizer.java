import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Given an input graph finds the K most distant clusters.
 * @author Carlos Mattoso, Gabriel Barros, Michelle Valente
 */
public class Clusterizer {
	/**
	 * Stores the value of the cut separating partitions of indices `original`
	 * and `created`. `created` is the index of the new partition producing by
	 * splitting the vertices in `original`.
     */
	public class PartitionOrder {
		int original, originalSize, created, createdSize, cutValue;

		public PartitionOrder(int original, int created, int originalSize, int createdSize, int cutValue) {
	        this.original = original;
	        this.created = created;
	        this.originalSize = originalSize;
	        this.createdSize = createdSize;
	        this.cutValue = cutValue;
        }

		@Override
		public String toString() {
			return String.format(
				"Partitioned %d [%d] into {%d [%d], %d [%d]} with cut value %d.",
					original, originalSize + createdSize,
					original, originalSize,
					created, createdSize,
					cutValue);
		}
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
     public List<PartitionOrder> findClusters(Graph g, int totalK, IMaximumFlow flowSolver) {
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
    	List<PartitionOrder> partitioningOrders = new ArrayList<>();
		int k = 1; // last assigned cluster index - initially all together

		while (k < totalK) {
			int[] clustersIds = new int[k];
			for(int i = 1; i <= k; i++)
				clustersIds[i-1] = i;
			IMaximumFlow.Solution[] candidateSolutions = new IMaximumFlow.Solution[k+1];

			// Each cluster elects its optimal partition.
			Arrays.stream(clustersIds).parallel().forEach(clusterId -> {
				candidateSolutions[clusterId] = new IMaximumFlow.Solution();

				for (int source : g.partitions.get(clusterId)) {
					IMaximumFlow.Solution candidateOptimalSolution = null; // optimal solution for given source

					// Execute the flow algorithm for each pair of vertices (s,t) for s,t in `clusterIdx`
					for (int target : g.partitions.get(clusterId)) {
						if (source == target)
							continue;

						candidateOptimalSolution = flowSolver.solve(g, source, target);

						// If the solution found now is better, replace it.
						if (candidateOptimalSolution.maximumFlow < candidateSolutions[clusterId].maximumFlow)
							candidateSolutions[clusterId] = candidateOptimalSolution;
					}
				}
			});

			// Find the first cluster for which a partition was possible.
			int init_part = 1;
			for (int i = 1; i <= k; i++) {
				if (candidateSolutions[i].partition != null) {
					init_part = i;
					break;
				}
			}

			// Select the optimal partition. Assume the flow by partitioning cluster `1` is the minimum.
			Integer optimalPartitionIdx = init_part;
			IMaximumFlow.Solution optimalPartition = candidateSolutions[init_part];

			PartitionOrder order = new PartitionOrder(init_part, k+1,
				optimalPartition.partition.get(init_part).size(),
				optimalPartition.partition.get(init_part + 1).size(),
				optimalPartition.maximumFlow);

			// No cluster before init_part had a non-null partition.
			for (int i = init_part + 1; i <= k; i++) {
				if (candidateSolutions[i].maximumFlow < optimalPartition.maximumFlow) {
					optimalPartition = candidateSolutions[i];
					optimalPartitionIdx = i;

					order.original = i;
					order.originalSize = optimalPartition.partition.get(i).size();
					order.created = k+1;
					order.createdSize = optimalPartition.partition.get(i + 1).size();
					order.cutValue = optimalPartition.maximumFlow;
				}
			}
			partitioningOrders.add(order);

			// Repartition the graph.
			for (Entry<Integer, Set<Integer>> partition : optimalPartition.partition.entrySet()) {
				/* In splitting the vertices, keep a partition with the original index
				 * and the new one with the index of `k+1`.
				 */
				int partitionIndex = partition.getKey();
				if (partitionIndex != optimalPartitionIdx)
					partitionIndex = k+1;

				g.partitions.put(partitionIndex, partition.getValue());

				// Adjust the vertex to partition mappings.
				for (int v : partition.getValue())
					g.vertex_partition[v] = partitionIndex;
			};

			k++;
		}

		return partitioningOrders;
	}
}
