//step 4
//doing association mining using FPgrowth algorithm, results are saved in output file under package featureSetProductWise
package sentiment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth_with_strings.AlgoFPGrowth_Strings;
import ca.pfv.spmf.test.MainTestFPGrowth_strings_saveToFile;

//feature generation using association mining using FPGrowth algorithm for strings
public class FeatureGeneration {

	/*******************************************
	 * STEP 5:
	 * 
	 * This step uses FPGrowth algorithm from spmf jar and 
	 * outputs the association mining results for each prodId/business_id.
	 * 
	 */
	
	
	/*
	 * NOTE: if use query => select distinct prodId from electronics_review
	 * 
	 * DB_NAME => sentiment
	 */
	
	// Database name
	static final String DB_NAME = "yelpdb";
	// Database ip
	static final String IP_ADDR = "127.0.0.1:3306";
	
	// Database credentials
	static final String USER = "root";
	static final String PASS = "1234";
		
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		Config config = new Config();
		conn = config.getConnection(DB_NAME,IP_ADDR,USER,PASS);
		
		stmt = (Statement) conn.createStatement();

		/*
		 * query to get all the product IDs
		 * can query the electronics_review table for distinct prodId.
		 */
		
		
		String sql = "SELECT * FROM mini_business where city="+'"'+"Glendale"+'"';
		rs = stmt.executeQuery(sql);

		String productId = "";
		while (rs.next()) {
			productId = rs.getString("business_id");
			System.out.println("product id = " + productId);
	
			/*
			 *  the path for saving the frequent itemsets found
			 */
			String output = config.getminiFilePath()+"output"+ productId + ".txt"; 
			String input = config.getminiFilePath()+"input"+ productId + ".txt";

			double minsup = 0.01; // MINIMUM SUPPORT which the paper has used
									// i.e. 1%
			// Applying the FPGROWTH algorithmMainTestFPGrowth.java
			AlgoFPGrowth_Strings algo = new AlgoFPGrowth_Strings();
			algo.runAlgorithm(input, output, minsup);
			algo.printStats();

		}
		stmt.close();
		conn.close();
	}

	public static String fileToPath(String filename) throws UnsupportedEncodingException {
		URL url = MainTestFPGrowth_strings_saveToFile.class.getResource(filename);
		return java.net.URLDecoder.decode(url.getPath(), "UTF-8");
	}
}
