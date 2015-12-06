import java.util.List;

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
	 *
	 * @param flowSolver
	 * 		Implementation of maximum flow algorithm.
	 * @return
	 * 		List of partitions with the values of their cuts.
	 */
	public List<PartitionPair> findClusters(IMaximumFlow flowSolver) {
		/*
		- Input:  G = {V, E}, K (número de partições a serem feitas)
		- Output: as K partições e os máximos cortes mínimos

		K_atual = K_ini = 1

		Enquanto houver menos de `K` grupos:
		  map: (K_ini .. K_atual) -> i {
		    map: (stream de pares (u,v), u != v, (u,v) pertence ao grupo `i`) -> par (u,v) {
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

		return null;
	}
}
