package sentiment;

import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SeedList {
	/***************************************************************
	 * 
	 * STEP 10-ii-a:
	 * 
	 * Till now we have mined opinions.
	 * 
	 * Now we have to proceed for finding sentiments of the opinion. 
	 * So, according to Bing Liu paper, we generate a seed list by 
	 * putting negative words and positive words. 
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
		Statement stmt = null;
		ResultSet rs = null;
		java.sql.PreparedStatement ps = null;

		Config config = new Config();
		conn = config.getConnection(DB_NAME,IP_ADDR,USER,PASS);
		
		
		FileInputStream fstream;
		BufferedReader br;
		String line,sql;
		fstream = new FileInputStream("./negative-words.txt");
		
		br = new BufferedReader(new InputStreamReader(fstream)); 
		
	    while ((line = br.readLine()) != null) {
			   // process the line.
	    	sql = "INSERT INTO polarity VALUES(?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, line);
			ps.setString(2, "-1");
		//	ps.executeUpdate();
				
		}
		
	    fstream = new FileInputStream("./positive-words.txt");
		
		br = new BufferedReader(new InputStreamReader(fstream)); 
	    while ((line = br.readLine()) != null) {
			   // process the line.
	    	sql = "INSERT INTO polarity VALUES(?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, line);
			ps.setString(2, "1");
	//		ps.executeUpdate();
				
		}
		
	}

}
