package sentiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RedundancyPruning {

	/********************************************************
	 * 
	 * STEP 7:
	 * 
	 * This step is to do redundancy pruning on the associate miner result 
	 * which was compact pruned in last step.
	 * 
	 * If there are two words "food" and "mexican food", we end up counting "food" twice.
	 * So, we need to remove that extra count.
	 * 
	 * This step removes redundant features as phrases are more meaningful than single words.
	 * 
	 * The redundant pruning is done using regex matching of two features. 
	 * If one is a subset of other, we just decrement the frequency count of the smaller feature. 
	 * 
	 *The output is stored in '"rp"[product].txt' file. 
	 *
	 */

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		Config config = new Config();
		
		FileInputStream fstream;
		BufferedReader br;
		
		String productId=args[0];	

		ArrayList<String> featurelist = new ArrayList<String>();
		HashMap<String, Integer> hm = new HashMap<>();
	
		BufferedWriter writer=null;
		writer = new BufferedWriter(new FileWriter(config.getminiFilePath()+"rp"+ productId + ".txt"));
		fstream = new FileInputStream(config.getminiFilePath()+"cp"+productId+".txt");
		br = new BufferedReader(new InputStreamReader(fstream)); 
	    String line;
	   
	    while ((line = br.readLine()) != null) {
			   // process the line.
	    	featurelist.add(line.split(":")[0]);
	    	hm.put(line.split(":")[0], Integer.parseInt(line.split(":")[1]));
		}
	    
	    int n = featurelist.size();
	    
	    for(int i=0;i<n;i++){
	    
	    	String[] s=featurelist.get(i).split(" ");
	    	int l = s.length;
	    	String regex="";
	    	/* 
	    	 * form the regex for a feature
	    	 * 
	    	 * this regex is then applied to all the features to see which features are supersets of these feature.
	    	 */
	    	for(int k=0;k<l;k++){
	    		regex+="(?=.*("+s[k]+"( |$)))";
	    	}
	    	regex+="[^\\n]+";
	    	Pattern r = Pattern.compile(regex);
	    	
	    	for(int j=0;j<n;j++){
	    		if(j!=i){
	    		//	
	    			Matcher m = r.matcher(featurelist.get(j));
	    			
	    			if(m.find()){	
	    				int p = hm.get(featurelist.get(i));
	    				int q = hm.get(featurelist.get(j));
	    				
	    				/*
	    				 * decrement the smaller feature frequency as we found a subset.
	    				 */
	    				hm.put(featurelist.get(i),p-q);
	    			}
	    		}
	    	
	    	}
	    }
	    
	    Set set = hm.entrySet();
	      // Get an iterator
	    Iterator i = set.iterator();
	  
	    while(i.hasNext()) {
	         Map.Entry<String,Integer> me = (Map.Entry)i.next();
	         if(me.getValue()>=0){
	         		/*
	         		 * output only those which have more than 3 frequency.
	         		 */
	        	    if(me.getValue()>=3){
	        	    	writer.write(me.getKey()+":"+me.getValue());
		        	 	writer.newLine();		         			
	        	    }
	        	 	
	         }

	     }

	    writer.close();
	    fstream.close();
		
		
		
		
	}

}
