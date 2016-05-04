
package sentiment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NegateProximity {
	
	/***************************************************************************
	 * 
	 * STEP 10:
	 * 
	 * In this step we check in our opinion sentences
	 * ( which contain both opinion and feature) that if any opinions is preceded 
	 * by a negate word.
	 * 
	 * If yes then we insert it in "negateproximity" table.
	 * 
	 * This information is important as we need to know which opinions will 
	 * have opposite polarity because of negation word.
	 */

	// Database name
	static final String DB_NAME = "sentiment";
	// Database ip
	static final String IP_ADDR = "127.0.0.1:3306";
	
	// Database credentials
	static final String USER = "root";
	static final String PASS = "1234";

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub

		Connection conn=null;
		Statement stmt=null;
		ResultSet rs=null;
		Statement stmt2=null;
		ResultSet rs2=null;
		PreparedStatement ps = null;
		Config config = new Config();

		HashMap<String, Boolean> negatewords = new HashMap<>(); // just because
																// search is
																// o(1)

		List<Long> list = new ArrayList<>();
		List<Long> sentence_ids = new ArrayList<>();

		conn = config.getConnection(DB_NAME,IP_ADDR,USER,PASS);
		stmt = (Statement) conn.createStatement();
		stmt2 = (Statement) conn.createStatement();

		String sql = "";
		String sql2 = "";

		/*
		 * query selects the sentenceIds which are our opinion sentences.
		 */
		sql = "SELECT distinct sentenceId from potentialfeature";

		rs = stmt.executeQuery(sql);

		Long sentenceId;
		Long id;
		String word;
		String posTag;
		Long curr_neg;

		while (rs.next()) {
			sentenceId = rs.getLong("sentenceId");
			sql2 = "Select relPos,word,posTag,sentenceId from tagwords where sentenceId=" + sentenceId;
			rs2 = stmt2.executeQuery(sql2);

			curr_neg = -5L;

			while (rs2.next()) {
				id = rs2.getLong("relPos");
				word = rs2.getString("word");
				posTag = rs2.getString("posTag");
				sentenceId = rs2.getLong("sentenceId");
				if (posTag.equals("JJ") || posTag.equals("JJR") || posTag.equals("JJS")) {

					if ((id - curr_neg) <= 5l && id>curr_neg) {

						list.add(id);
						sentence_ids.add(sentenceId);
					}

				} else if (posTag.equals("NEGATE")) {
					curr_neg = id;
					
				}
			}
		}

		for (int i = 0; i < list.size(); i++) {
			sql = "INSERT INTO negateproximity VALUES(?,?,-1)";
			ps = conn.prepareStatement(sql);
			ps.setLong(1, sentence_ids.get(i));
			ps.setLong(2, list.get(i));
			//ps.executeUpdate();
		}

	}

}
