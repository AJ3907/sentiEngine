package sentiment;

import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;



public class CompactPruning {
	
	
	
	/************************************************
	 * 
	 * STEP 6:
	 * 
	 * This is used to do compactness pruning on the frequent features found.
	 * 
	 * Compactness Pruning is to be applied on feature phrases.
	 * 
	 * A phrase is compact iff all adjacent words are at most 3 word distance apart.
	 * 
	 * We store the compact phrases and their frequency in '"cp"+ prodId.txt' files.
	 * 
	 * We read the raw output of association miner and read our "temp" file we generated in Step 2
	 * for our help in this compactness pruning.
	 * 
	 * Each "temp" file contains a phrase seperated by "." for every sentence. And every word is accompanied by 
	 * its relative position in the sentence
	 * 
	 * In addition to compact pruning, we are also generating one more file "freqFeat"[product].txt for 
	 * each product that contains the start and end positions of a feature phrase. This will be useful to 
	 * find adjacent opinions for feature phrases later on.
	 */
	

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		Config config = new Config();
		
		FileInputStream fstream;
		BufferedReader br;
		String productId=args[0];
		
		HashMap<String,Integer> hm=new HashMap();
		
		HashMap<String,List> hs = new HashMap();
		
		BufferedWriter writer=null;
		writer = new BufferedWriter(new FileWriter(config.getminiFilePath()+"cp"+ productId + ".txt"));
		
		BufferedWriter writer2=null;
		writer2 = new BufferedWriter(new FileWriter(config.getminiFilePath()+"freqfeat"+ productId + ".txt"));
		
		fstream = new FileInputStream(config.getminiFilePath()+"output"+productId+".txt");
		br = new BufferedReader(new InputStreamReader(fstream)); 
	    String line;
	    int count=0;
	    while ((line = br.readLine()) != null) {
			   // process the line.
	    	
		    	/*
		    	 * means it has more than two words.
		    	 */
				if(line.contains(" ")){
					
					line = line.split(":")[0];
					hm.put(line,0);
				}
				
				/*
				 * we write the single features in the "cp" file because compact pruning won't affect them.
				 */
				else{
					writer.write(line);
					writer.newLine();
				}
		}
	    fstream.close();
	    fstream = new FileInputStream(config.getminiFilePath()+"temp"+productId+".txt");
		
		br = new BufferedReader(new InputStreamReader(fstream)); 
	    while ((line = br.readLine()) != null) {
			   // process the line.
	    	
	    	
	    	Set set = hm.entrySet();
		      // Get an iterator
		    Iterator i = set.iterator();
		      // Iterate over elements
		    while(i.hasNext()) {
		         Map.Entry<String,Integer> me = (Map.Entry)i.next();
		         /*
		          * if processLine yields -1, then phrase isn't compact or it contains more than 3 words
		          * So remove it from hashmap.
		          */
		         if(processLine(hm, line, me.getKey(),writer2)==-1){
		        	 i.remove();
		         } 	
		     }
		    
		    
				
		}
	    
	    /*
	     * now hm contains all compact frequent feature phrases.
	     * write them to our output "cp" file.
	     */
		
	    Set set = hm.entrySet();
	      // Get an iterator
	    Iterator i = set.iterator();
	      // Iterate over elements
	    while(i.hasNext()) {
	         Map.Entry<String,Integer> me = (Map.Entry)i.next();
	         if(me.getValue()>0){
	        	 writer.write(me.getKey()+":"+me.getValue());
		         writer.newLine();
	         }
	     }
  
	   writer.close();
	   writer2.close();	
	}
	
	/*
	 * processLine function 
	 * 
	 * Takes in a line and a  frequent feature phrase.
	 * 
	 * for each word in the phrase(from assoc miner output), we loop on the a phrase from the line we got from "temp" file
	 * and see that the difference in relative position of all adjacent words is less than equal to 3.
	 * 
	 * we count such occurences of the feature phrase and put it in hashmap.
	 * 
	 */
	private static int processLine(HashMap<String,Integer> hm, String line,String phrase,BufferedWriter writer) throws IOException{
		String pattern="";
		
		
		
		
		String temp[] = line.split("~");
		
		line = temp[0];
		
		Long sentenceId = Long.parseLong(temp[1]);
		//split phrase into words.
		String words[]=(phrase.split(":")[0]).split(" ");
		String blocks[] = line.split("\\.");
		
		
		int m = words.length;
		
		
		/*
		 * we have limited feature phrases to length of 3, so any phrase greater than that we are removing.
		 */
		if(m>3)
			return -1;
		int n = blocks.length;
		
		Long[] wids = new Long[m];
		int count=0;
		for(int i=0;i<n;i++){
			count=0;
			String ids = "";
			if(blocks[i].contains(" ")){
				
				String s[] = blocks[i].split(" ");
				
				int l = s.length;
				for(int j=0;j<l;j++){
					
					//here we get "word,wordId"
					String w = s[j].split(",")[0];
					String id = s[j].split(",")[1];
					int flag=0 ;

					for(int k=0;k<m;k++){
						if(w.equals(words[k])){
							
							if(wids[k]==null)
								count++;
							wids[k]=Long.parseLong(id);
							
							if(count==m){
								
								for(int q=0;q<m-1;q++){
									if(wids[q+1]-wids[q]>3 || wids[q]-wids[q+1]>3)
										flag=1;	
								}
								if(flag==0){
									i=n;
									break;
								}
							}
							
						}
							
					}
					
				}
				if(count==m){
					
					for(int q=0;q<m-1;q++){
						if(wids[q+1]-wids[q]>3 || wids[q]-wids[q+1]>3)
							return -1;	
					}
					
					long min = wids[0];
					long max = wids[0];
					for(int q=1;q<m;q++){
						if(wids[q]>max)
							max=wids[q];
						if(wids[q]<min)
							min=wids[q];
					}
					
					int val = hm.get(phrase);
					hm.put(phrase, val+1);
					
					
					/*
					 * write start and end positions of feature phrase.
					 */
					writer.write(phrase.split(":")[0]+":"+min+","+max+"~"+sentenceId);
					writer.newLine();
					
					return 0;
					
				}
				
				
				
			}
			
		}
		return 1;	
		
			}

}
