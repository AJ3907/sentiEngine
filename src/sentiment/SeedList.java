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
	 * STEP 10-ii:
	 * 
	 * Till now we have mined opinions.
	 * 
	 * Now we have to proceed for finding sentiments of the opinion.
	 * 
	 * 
	 * 
	 * 
	 */

	public static void main(String[] args) throws IOException, SQLException {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		java.sql.PreparedStatement ps = null;

		Config config = new Config();
		conn = config.getConnection();
		
		
		FileInputStream fstream;
		BufferedReader br;
		String line,sql;
		fstream = new FileInputStream(config.getminiFilePath()+"negative-words.txt");
		
		br = new BufferedReader(new InputStreamReader(fstream)); 
		
	    while ((line = br.readLine()) != null) {
			   // process the line.
	    	sql = "INSERT INTO polarity VALUES(?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, line);
			ps.setString(2, "-1");
		//	ps.executeUpdate();
				
		}
		
	    fstream = new FileInputStream(config.getminiFilePath()+"positive-words.txt");
		
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
