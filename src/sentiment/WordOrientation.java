package sentiment;

import java.awt.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import rita.RiWordNet;

public class WordOrientation {
	/**********************************************
	 * STEP 10-ii-b:
	 * 
	 * Using seedlist generated in step 10-ii-a, 
	 * we generate polarity for all our opinions using wordnet.
	 * AS described by bing liu opinion mining paper.
	 * 
	 */

	// Database name
	static final String DB_NAME = "sentiment";
	// Database ip
	static final String IP_ADDR = "127.0.0.1:3306";
	
	// Database credentials
	static final String USER = "root";
	static final String PASS = "1234";
	
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		Statement stmt2 = null;
		ResultSet rs2 = null;
		java.sql.PreparedStatement ps = null;

		Config config = new Config();
		conn = config.getConnection(DB_NAME,IP_ADDR,USER,PASS);
		stmt = (Statement) conn.createStatement();
		stmt2 = (Statement) conn.createStatement();
		
		HashMap seedList = new HashMap<>();
		ArrayList activeList=new ArrayList();
		
		String sql = "SELECT * from polarity";
		rs = stmt.executeQuery(sql);
		
		while (rs.next()) {
			seedList.put(rs.getString("word"),rs.getInt("Polarity"));
		}
		
		sql="Select distinct opinion from potentialfeature";
		rs2=stmt2.executeQuery(sql);
		
		while (rs2.next()) {
			activeList.add(rs2.getString("opinion"));
		}
		
		
		int flag=0;
		
		while(true){
			
			flag=0;
			int activeListSize = activeList.size();
			System.out.println(activeListSize);
			for(int i=0;i<activeList.size();i++){
				String word = (String) activeList.get(i);
				
				if(seedList.containsKey(word)){
					
					activeList.remove(i);
					flag=1;
					sql = "INSERT INTO opinion_polarity VALUES(?,?)";
					ps = conn.prepareStatement(sql);
					ps.setString(1, word);
					ps.setInt(2, (int) seedList.get(word));
					//ps.executeUpdate();
					continue;
				}
				
				
				RiWordNet wordnet = new RiWordNet("C:/Program Files (x86)/WordNet/2.1");
				String[] antonyms = wordnet.getAllAntonyms(word, "a");
				String[] synonyms = wordnet.getAllSynonyms(word, "a");
				// inserting antonyms with opposite polarity
				for (int j = 0; j < antonyms.length; j++) {
					if(seedList.containsKey(antonyms[j])){
						
						if(word.equals("quick"))
							System.out.println(antonyms[j]);
						int polarity  = (int) seedList.get(antonyms[j]);
						seedList.put(word, -polarity);
						activeList.remove(i);
						flag=1;
						sql = "INSERT INTO opinion_polarity VALUES(?,?)";
						ps = conn.prepareStatement(sql);
						ps.setString(1, word);
						ps.setInt(2, polarity*-1);
						//ps.executeUpdate();
						break;
					}
				}
				if(flag==0){
					// inserting synonyms with same polarity
					for (int j = 0; j < synonyms.length; j++) {
						if(seedList.containsKey(synonyms[j])){
							if(word.equals("quick"))
								System.out.println(synonyms[j]);
							int polarity  = (int) seedList.get(synonyms[j]);
							seedList.put(word, polarity);
							activeList.remove(i);
							flag=1;
							sql = "INSERT INTO opinion_polarity VALUES(?,?)";
							ps = conn.prepareStatement(sql);
							ps.setString(1, word);
							ps.setInt(2, polarity);
						//	ps.executeUpdate();
							break;
						}
				}
				}


		}
			if(flag==0)
				break;

		}

	}

}
