//step 5
//extract the opinion, those features which got opinions insert into potential feature table
package sentiment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OpinionExtraction {

	
	/**STEP 8:
	 * 
	 * This step finds adjacent opinions to corresponding frequent feature.
	 * 
	 * Adjacent opinions means around 5 word distance near the noun feature.
	 * 
	 * A nearest adjacent opinion is found for a frequent feature (not always)
	 * and inserted in "potentialfeature" table.
	 *  
	 */
	
	
	// Database name
	static final String DB_NAME = "sentiment";
	// Database ip
	static final String IP_ADDR = "127.0.0.1:3306";
	
	// Database credentials
	static final String USER = "root";
	static final String PASS = "1234";
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

		Connection conn = null;
		Statement stmt = null, stmt2 = null, stmt3 = null;
		ResultSet rs = null, rs2 = null, rs3 = null;
		java.sql.PreparedStatement ps = null;

		Config config = new Config();
		conn = config.getConnection(DB_NAME,IP_ADDR,USER,PASS);
		stmt = (Statement) conn.createStatement();
		stmt2 = (Statement) conn.createStatement();
		stmt3 = (Statement) conn.createStatement();
		
		String sql;
		

		String productId = args[0];
		/*
		 * We open the pruned feature file for each product.
		 */
		String input = config.getminiFilePath()+"rp"+ productId + ".txt";
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line;
	
		
		while (((line = reader.readLine()) != null)) {
			
			/*
			 * This code handles only opinions for single (1-gram) features. 
			 * For feature phrases, OpinionsForPhrases.java is there.
			 * So if line contains space, it means it has more than 1 word, means it is a phrase.
			 */
			if(line.contains(" "))
				continue;
			String[] lineSplited = line.split(":");
			
			/*
			 * query yields all sentenceIds and relative positions of the frequent feature in the dataset.
			 */
			sql = "Select t1.relPos,t1.sentenceId from ((SELECT t.relPos,t.word,t.sentenceId,t.posTag,r.reviewId FROM tagwords t inner join reviewsentence r on t.sentenceId = r.id) t1 inner join electronics_review e on e.id = t1.reviewId) where e.prodId="
					+ "'" + productId + "'" + "and t1.word=" + "'" + lineSplited[0] + "'";

			rs2 = stmt2.executeQuery(sql);
			
			Long sentenceId;
			Long tagId;
			
			
			while (rs2.next()) {
				sentenceId = rs2.getLong("sentenceId");
				tagId = rs2.getLong("relPos");
				/*
				 * query yields only the adjectives in the sentences we got from the previous query.
				 */

				sql = "Select word,relPos from tagwords where sentenceId=" + "'" + sentenceId + "'" + "and ( posTag="
						+ "'" + "JJ" + "'" + "or posTag=" + "'" + "JJR" + "'" + "or posTag=" + "'" + "JJS" + "'"
						+ ")";
				rs3 = stmt3.executeQuery(sql);

				Long opinionTagId;
				String word = "";
				Long minDistanceId = Long.MAX_VALUE;
				String nearestOpinion = "";
				while (rs3.next()) {
					word = rs3.getString("word");
					opinionTagId = rs3.getLong("relPos");

					
					/*
					 * for opinion and word to be adjacent they should be at most 5 distance apart.
					 * and we check for the minimum relative position of all the adjacent opinions,
					 *  because we need the nearest opinion.
					 */
					if (Math.abs(opinionTagId - tagId) < 6l) {
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
				//	ps.executeUpdate();

				}
			}
		}
		reader.close();
		stmt.close();
		conn.close();
	}

}
