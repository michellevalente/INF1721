import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
	
	public Graph graphParse(){
		
		BufferedReader reader = null;

		Map<Integer, List<Graph.Edge>> adjacencies = new HashMap<>();
		Map<Integer, Set<Integer>> partitions = new HashMap<>();
		
		
		List<Graph.Edge> edges = new ArrayList<Graph.Edge>();
		//edges.add(new Graph.Edge(source, target, capacity, 0));
		
		try {
		    reader = new BufferedReader(new FileReader(file));
		    String text = null;
		    Pattern p = Pattern.compile("-?\\d+");
		    
		    text = reader.readLine();
		    Matcher m = p.matcher(text);	    
		    m.find();
		    int n = Integer.parseInt(m.group());
		    
		    
		    while ((text = reader.readLine()) != null) {
		    	m = p.matcher(text);
		    	
		    	int readNum[] = new int[4];
		    	int i = 0 ; //i=0; necessary?
		    	while(m.find()){
		    		
		    		readNum[i++] = Integer.parseInt(m.group());
		    		
		    	}
		    	
		    	edges.add(new Graph.Edge(
		    							readNum[1],
		    							readNum[2],
		    							readNum[3],
		    							0.0));
		    }
		    
		    adjacencies.put(1, edges);
		    
		    Set <Integer> vertices = new HashSet<Integer>();
		    for(int j=1; j<=n; j++){
		    	vertices.add(j);
		    }
		    partitions.put(1, vertices );
		    
		    
		    int[] vertex_partition = null;
		    Arrays.fill(vertex_partition, 1);

		    return new Graph(adjacencies,partitions,vertex_partition );
		    
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		    } catch (IOException e) {
		    }
		}
		return null;
		
		
	}

}
