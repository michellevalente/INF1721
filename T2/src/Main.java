import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class Main {

	public static void main(String[] args) {
		Path fullPathFilesName[] = new Path[args.length - 1];

		for (int i=1; i < args.length; i++)
			fullPathFilesName[i-1] = Paths.get(args[0],args[i]);

		for (Path fpFileName : fullPathFilesName) {
			// System.out.println(fpFileName);
			Graph currentGraph = (new Loader(fpFileName.toString())).graphParse();

			final String[] outputs = new String[3];
			Stream.iterate(3, i -> i + 1).limit(3).parallel().forEach(k ->  {
	  			// System.out.println(currentGraph);

				List<Clusterizer.PartitionOrder> partitionOrders = new ArrayList<>();
				double duration = 0, nRuns = 0; // in ms; number of executions
				while (duration < 5000000000.0) { // 5000ms
					Graph gCopy = Graph.deepCopy(currentGraph);

					long startTime = System.nanoTime();
					partitionOrders = new Clusterizer().findClusters(gCopy, k, new PreflowPush());
					long endTime = System.nanoTime();

					duration += (endTime - startTime);
					nRuns += 1;
				}

				// System.out.println(currentGraph);

				String output = String.format("%s // k: %d // avg runtime %fms [# runs: %d]\n",
						fpFileName.getFileName(), k, (duration/1000000.0)/nRuns, (int)nRuns);
				for(Clusterizer.PartitionOrder partitionOrder : partitionOrders)
					output += partitionOrder + "\n";
				outputs[k - 3] = output;
			});

			// Print all the results found for each file x K combination.
			for(String kOutput : outputs)
				System.out.println(kOutput);
		}
	}

}
