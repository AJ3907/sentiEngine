package sentiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class OpinionsForPhrases {
	
	
	/***********************************************
	 * 
	 * STEP 9:
	 * 
	 * In the last step opinions were found only for single word features.
	 * In this step, we find nearby opinions for feature phrases.
	 * 
	 * The opinion phrase and opinion are inserted in "potentialfeature" 
	 * table.
	 * 
	 * 
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
		Statement stmt = null, stmt2 = null, stmt3 = null;
		ResultSet rs = null, rs2 = null, rs3 = null;
		java.sql.PreparedStatement ps = null;

		Config config = new Config();
		conn = config.getConnection(DB_NAME,IP_ADDR,USER,PASS);
		stmt = (Statement) conn.createStatement();
		stmt2 = (Statement) conn.createStatement();
		stmt3 = (Statement) conn.createStatement();
		String sql = "SELECT distinct prodId FROM electronics_review";
		rs = stmt.executeQuery(sql);

		String productId = args[0];

		String input = config.getminiFilePath()+"freqfeat"
				+ productId + ".txt";
		
		String line;
		
	
		BufferedReader reader;
		
		input = config.getminiFilePath()+"freqfeat"
				+ productId + ".txt";
		reader = new BufferedReader(new FileReader(input));
		while (((line = reader.readLine()) != null)) {
			/*
			 * if line does not contain space, its a single word and we
			 * have already found opinions for single words, so skip
			 * them.
			 */
			if(!line.contains(" ") )
				continue;
			String[] lineSplited = line.split(":");

			Long sentenceId;
			Long tagId;
			
				
			sentenceId = Long.parseLong(line.split("~")[1]);
			
			Long left = Long.parseLong((((line.split("~")[0]).split(":")[1]).split(",")[0]));
			Long right = Long.parseLong((((line.split("~")[0]).split(":")[1]).split(",")[1]));
			
			
			sql = "Select word,id from tagwords where sentenceId=" + "'" + sentenceId + "'" + "and ( posTag="
					+ "'" + "JJ" + "'" + "or posTag=" + "'" + "JJR" + "'" + "or posTag=" + "'" + "JJS" + "'"
					+ ")";
			rs3 = stmt3.executeQuery(sql);

			Long opinionTagId;
			String word = "";
			Long minDistanceId = Long.MAX_VALUE;
			String nearestOpinion = "";
			while (rs3.next()) {
				word = rs3.getString("word");
				opinionTagId = rs3.getLong("id");

				if ((opinionTagId - left) < 6l) {
					if (opinionTagId < minDistanceId) {
						nearestOpinion = word;
						minDistanceId = opinionTagId;
					}
				}
				if ((right - opinionTagId) < 6l) {
					if (opinionTagId < minDistanceId) {
						nearestOpinion = word;
						minDistanceId = opinionTagId;
					}

				}
			}
			if (minDistanceId != Long.MAX_VALUE) {

				sql = "INSERT INTO potentialfeature VALUES(NULL,?,?,?,?,?)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, lineSplited[0]);
				ps.setString(2, nearestOpinion);
				ps.setString(3, productId);
				ps.setLong(4, sentenceId);
				ps.setLong(5, minDistanceId);
				//ps.executeUpdate();

			}
			
			}
			
			stmt.close();
			conn.close();
			}
		
		
}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

	


