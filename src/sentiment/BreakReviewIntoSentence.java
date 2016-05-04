//step 1
//breaking review into sentences because post tagger takes input sentence wise
package sentiment;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.BreakIterator;
import java.util.Locale;

public class BreakReviewIntoSentence {
	
	/****************************************
	 * STEP 2:
	 * 
	 * This step breaks the review text for each review into sentences.
	 * These sentences are then put into our "sentiment" database in the 
	 * "reviewsentence" table.
	 * 
	 * Lemmatization of the sentence is done using stanford lemmatization tool.
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
		Statement stmt = null;
		ResultSet rs = null;
		java.sql.PreparedStatement ps = null;
		
		Long reviewId;
		String reviewText;

		Config config = new Config();
		
		// initialise the java class that does lemmatizing
		lemmatize slem = new lemmatize();
		
		
		conn = config.getConnection(DB_NAME,IP_ADDR,USER,PASS);
		stmt = (Statement) conn.createStatement();
		/*
		 * query to get the reviewtext for each prodId.
		 */
		String sql = "SELECT id,reviewText from electronics_review where prodId="+'"'+args[0]+'"';
		rs = stmt.executeQuery(sql);

		while (rs.next()) {
			reviewId = rs.getLong("id");
			reviewText = rs.getString("reviewText");
			
			BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
			iterator.setText(reviewText);
			int start = iterator.first();
			for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
				
				//process the sentence
				String str = reviewText.substring(start, end);
				str=str.toLowerCase();
				str=str.replaceAll("i\'am", " i am");
				str=str.replaceAll("i\'ve", " i have ");
				str=str.replaceAll("i\'ll", " i will ");
				str=str.replaceAll("i \'am", "i am");
				str=str.replaceAll("i \'ve", "i have");
				str=str.replaceAll("i \'ll", "i will");
				
				str=str.replaceAll("can\'t", "cannot");
				str=str.replaceAll("won\'t", "would not");
				
				//in general
				str=str.replaceAll("n\'t", " not");
				//keep only alphabets and space.
				str=str.replaceAll("[^a-zA-Z ]", " ");
				//lemmatize the string using stanford lemmatization tool.
				str=slem.lemmatize(str);
				
				//insert into database
				sql = "INSERT INTO reviewsentence VALUES(NULL,?,?)";
				ps = conn.prepareStatement(sql);
				ps.setLong(1, reviewId);
				ps.setString(2, str);
			//	ps.executeUpdate();
				

			}
			
		}
		stmt.close();
		conn.close();
	}
}
