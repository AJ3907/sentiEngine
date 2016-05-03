package sentiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ParseTestData {
	
	/*********
	 * STEP 1:
	 * 
	 * This file is to put the raw review text from the files generated 
	 * by GetYelpReviews.java into our "sentiment" database in
	 * "electronics_review" table.
	 * 
	 * Here a product is a business,movie or anything which has a review.
	 * 
	 * For yelpdatabase: "business_id" is equivalent to "prodId".
	 * 
	 * NOTE: This was a pipeline developed by us for the mining 
	 * of electronics reviews so bear with the naming of database columns.
	 * 
	 */
	
	// Database name
	static final String DB_NAME = "sentiment";
	// Database ip
	static final String IP_ADDR = "127.0.0.1:3306";
	
	// Database credentials
	static final String USER = "root";
	static final String PASS = "1234";
	
	public static void main(String[] args) throws IOException, SQLException {
		// TODO Auto-generated method stub
		
		Connection conn = null;
		java.sql.PreparedStatement ps;
		
		Config config = new Config();
		
		FileInputStream fstream;
		BufferedReader br;
		
		String line;
		
		int count=0;
		
		//get the reviewset file for that business_id
		fstream = new FileInputStream(config.getminiDataPath()+args[0]+".txt");
		br = new BufferedReader(new InputStreamReader(fstream)); 
	       
	    String summary="";
	    String text="";
	    
	    String productId=args[0];
	    conn = config.getConnection(DB_NAME,IP_ADDR,USER,PASS);
	    String sql = "INSERT INTO electronics_review VALUES(?,?,?,?,?,?,?,?,?,?,null)";
	    ps = conn.prepareStatement(sql);
	    
	    while ((line = br.readLine()) != null) {
	
	    	//see if the line contains "[t]" as it marks start of a review.
	    	if(line.contains("[t]")){
	    		count++;
	    		
	    		if(count>=2){
	    			//add review to database.
	    				
	    			text=text.replace("[t]", "");
	    			ps.setString(1, null);
	    			ps.setString(2, productId);
	    			ps.setString(3, null);
	    			ps.setInt(4, 0);
	    			ps.setInt(5, 0);
	    			ps.setString(6, text);
	    			ps.setDouble(7, 0);
	    			ps.setString(8, summary);
	    			ps.setInt(9, 0);
	    			ps.setString(10, null);
	    			
	   // 			ps.executeUpdate();
	    			
	    			text="";
	    			
	    		}
	    		
	    		//for the next review.
	    		
	    	}
	    	text=text+line;
		}
	   
	    //last review
	    text=text.replace("[t]", "");
	    ps.setString(1, null);
		ps.setString(2, productId);
		ps.setString(3, null);
		ps.setInt(4, 0);
		ps.setInt(5, 0);
		ps.setString(6, text);
		ps.setDouble(7, 0);
		ps.setString(8, summary);
		ps.setInt(9, 0);
		ps.setString(10, null);
	//	ps.executeUpdate();
	    
	    fstream.close();
		conn.close();

	}
	
	

}
