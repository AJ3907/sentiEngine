package sentiment;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class wrapper {

	/*************************************************
	 * 
	 * This is a wrapper for various files in our sentiment engine.
	 * 
	 * 
	 * The while loop contains of a series of steps.
	 * Run 1 step at a time by commenting all other steps.
	 * 
	 * Our wrapper will run all the files of sentiment engine for each product.
	 * 
	 * Sit back and relax!!!
	 *
	 */
	
	// Database name
	static final String DB_NAME = "yelpdb";
	// Database ip
	static final String IP_ADDR = "127.0.0.1:3306";
	
	// Database credentials
	static final String USER = "root";
	static final String PASS = "1234";
	
	
	public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		
		
		Connection conn=null;
		Statement stmt=null;
		ResultSet rs=null;
		PreparedStatement ps = null;
		Config config = new Config();
		
		GetYelpReview getYelpReview = null;
		ParseTestData parseTestData = null;
		BreakReviewIntoSentence breakReviewIntoSentence = null;
		CreatingFeatureSetForAssociationMining creatingFeatureSetForAssociationMining = null;
		CompactPruning compactPruning = null;
		RedundancyPruning redundancyPruning = null;
		OpinionExtraction opinionExtraction = null;
		
		
//		OpinionsForPhrases opinionsForPhrases = null;
//		FreqFeaturePhrases ffp = null;
//		ffptodb obj = null;
//		sentiToJson stoj = null;
		
		
		
		
		conn=config.getConnection(DB_NAME,IP_ADDR,USER,PASS);
		stmt = (Statement) conn.createStatement();
		String sql = "SELECT * FROM mini_business where city="+'"'+"Glendale"+'"';
		rs=stmt.executeQuery(sql);
		int count=0;
		while(rs.next()){
			String businessId = rs.getString("business_id");
			String[] arg = {businessId};
			/*
			 * NOTE: uncomment 1 step at a time.
			 */
			
			
			//step0
			//getYelpReview.main(arg);
			
			//step1
			//parseTestData.main(arg);
			
			//step2
			//breakReviewIntoSentence.main(arg);
			
			/*
			 * step 3: Run TagText.java
			 */
			
			//step4
			//createFeatureSetForAssociationMining.main(arg);
			
			/*
			 * step 5: Run FeatureGeneration.java
			 */
			
			
			//step6
			//compactPruning.main(arg);
			
			//step7
			//redundancyPruning.main(arg);
			
			
			
			/***************************************************
			 * ASSOCIATION MINING HAS BEEN DONE
			 */
			
			/***************************************************]
			 * OPINION MINING STARTS FROM HERE
			 */
			//step8
			//opinionExtraction.main(arg);
			
			//step9
			//opinionsForPhrases.main(arg);
			
			
			//rp.main(arg);
			//ffp.main(arg);
			//obj.main(arg);
			//stoj.main(arg);
			
			
			/*
			 * just to know how many products/business we have finished.
			 */
			
			count++;
			System.out.println(count);
	}

		stmt.close();
		conn.close();
}
}
