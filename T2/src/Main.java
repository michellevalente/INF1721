import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Path fullPathFilesName[] = new Path[args.length-1];
		
		for(int i=1; i< args.length;i++ ){
			fullPathFilesName[i-1] = Paths.get(args[0],args[i]);
		}
		System.out.println("aaaa");
		System.out.println(fullPathFilesName[0]);
		
		for (Path fpFileName :fullPathFilesName){
			Graph currentGraph =(new Loader(fpFileName.toString())).graphParse();
			
			List<Clusterizer.PartitionOrder> print = 
					(new Clusterizer()).findClusters(currentGraph, 2, new EdmondsKarp());
			System.out.println(print.toString());
			
		}
	}

}
