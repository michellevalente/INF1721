import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loader class for the file format 'mfp'
 *
 *
 * @author Carlos Mattoso, Gabriel Barros, Michelle Valente
 */
public class Loader {
	File file;

	public Loader(String filePath){
		file = new File(filePath);
	}

	public Graph graphParse() {
		BufferedReader reader = null;

		Map<Integer, List<Graph.Edge>> adjacencies = new HashMap<>();
		Map<Integer, Set<Integer>> partitions = new HashMap<>();

		//edges.add(new Graph.Edge(source, target, capacity, 0));

		try {
		    reader = new BufferedReader(new FileReader(file));
		    String text = null;
		    Pattern p = Pattern.compile("-?\\d+");

		    text = reader.readLine();
		    Matcher m = p.matcher(text);
		    m.find();
		    int n = Integer.parseInt(m.group());
		    //System.out.println("n: "+n);
		    int k = 0;
		    while(k<n){
		    	reader.readLine();
		    	k++;

		    	// Set up the mappings of vertices to their list of edges
		    	List<Graph.Edge> edges = new ArrayList<>();
		    	adjacencies.put(k, edges);
		    }
		    // Read the edges and create the vertex -> list<Edge> mappings
		    int readNum[] = new int[4];
		    while ((text = reader.readLine()) != null) {
		    	m = p.matcher(text);

		    	// reads: edgeId, source vertex id, target vertex id, edge capacity

		    	for (int i = 0; i < 4; i++){
		    		m.find();
		    		readNum[i] = Integer.parseInt(m.group());
		    		//m.find();
		    	}
		    	//System.out.println(readNum[0]+" "+readNum[1]+" "+readNum[2]+" "+readNum[3]);
		    	adjacencies.get(readNum[1]).add(new Graph.Edge(readNum[1], readNum[2], readNum[3]));
		    }

		    // Create the partition to vertex mappings.
		    Set<Integer> vertices = new HashSet<>();
		    for(int j = 1; j <= n; j++){
		    	vertices.add(j);
		    }
		    partitions.put(1, vertices);

		    // Create the vertex -> partiton mappings.
		    int[] vertex_partition = new int[n+1];
		    Arrays.fill(vertex_partition, 1);

		    return new Graph(adjacencies, partitions, vertex_partition);

		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		}
		return null;
	}

}
