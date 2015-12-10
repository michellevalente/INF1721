import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class Main {

	public static void main(String[] args) {
		Path fullPathFilesName[] = new Path[args.length - 1];

		for (int i=1; i < args.length; i++) {
			fullPathFilesName[i-1] = Paths.get(args[0],args[i]);
		}
		System.out.println("aaaa");
		System.out.println(fullPathFilesName[0]);

		for (Path fpFileName : fullPathFilesName){
			System.out.println(fpFileName);
			Graph currentGraph =(new Loader(fpFileName.toString())).graphParse();

			System.out.println(currentGraph);

			List<Clusterizer.PartitionOrder> partitionOrders =
					(new Clusterizer()).findClusters(currentGraph, 2, new EdmondsKarp());

			System.out.println(currentGraph);

			for(Clusterizer.PartitionOrder partitionOrder : partitionOrders)
				System.out.println(partitionOrder);

		}
	}

}
