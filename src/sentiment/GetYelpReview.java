package sentiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GetYelpReview {
	
	/*******************************************************
	 * STEP 0:
	 * 
	 * Gets the yelp reviews for a business_id from the yelpdb 
	 * and stores the reviewtext in a file with each review 
	 * starting with a "[t]".
	 * 
	 * NOTE: This step is unnecessary if one can directly put the 
	 * "yelpdb" records in our "sentiment" database. This step is done 
	 * just to get yelp reviews in our "sentiment" database as a pipeline 
	 * is already been put in place to handle sentiment analysis of the "sentiment" database.
	 */
	
	
	// Database name
	static final String DB_NAME = "yelpdb";
	// Database ip
	static final String IP_ADDR = "127.0.0.1:3306";
	
	// Database credentials
	static final String USER = "root";
	static final String PASS = "1234";
	

	public static void main(String[] args) throws SQLException, IOException {
		// TODO Auto-generated method stub
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		BufferedWriter writer = null;
		
		Config config = new Config();
		
		conn = config.getConnection(DB_NAME,IP_ADDR,USER,PASS);
		stmt = (Statement) conn.createStatement();
		
		String businessId=args[0];
		
		// get all business_ids from table
		String sql = "SELECT text FROM mini_review where business_id="+'"'+businessId+'"';
		rs=stmt.executeQuery(sql);
		
		// store the reviewset in this file
		writer = new BufferedWriter(new FileWriter(config.getminiDataPath()+ businessId + ".txt"));
		while(rs.next()){
			// writing each review seperated by [t].
			writer.write("[t]"+rs.getString("text"));
			writer.newLine();	
		}
		writer.close();
		stmt.close();
		conn.close();
		
	}

}
