//step 2
//now tagging the sentences using stanFord post tagger
package sentiment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.StringTokenizer;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class TagText {
	
	/**********************************************
	 * STEP 3:
	 * 
	 * This step is used to tag the words in a sentence we 
	 * stored in "reviewsentence" table.
	 * 
	 * The tagged nouns,adjectives and negate words are stored in 
	 * "tagwords" table.
	 */	
	
	
	
	// Database name
	static final String DB_NAME = "sentiment";
	// Database ip
	static final String IP_ADDR = "127.0.0.1:3306";
	
	// Database credentials
	static final String USER = "root";
	static final String PASS = "1234";

	static void findPOS(String s, Long sentenceId, int relPos) throws ClassNotFoundException, SQLException, IOException {

		/*
		 * String s is of type "WORD_POSTAG"
		 * So,  Stringparts[0] = WORD
		 * 		Stringparts[1]=POSTAG
		 */
		
		String[] Stringparts  = s.split("_");

		
		Connection conn = null;
		Statement stmt = null;
		java.sql.PreparedStatement ps = null;
		
		FileInputStream fstream;
		BufferedReader br;
		
		String line;
		
		Config config = new Config();
		
		conn = config.getConnection(DB_NAME,IP_ADDR,USER,PASS);
		stmt = (Statement) conn.createStatement();
		
		
		/* store the negation words from the file in a hashmap,
		so as to tag these in the sentences*/
		fstream = new FileInputStream("./negation.txt");
		br = new BufferedReader(new InputStreamReader(fstream)); 
	    HashMap negationwords = new HashMap<>();
	    while ((line = br.readLine()) != null) {
				negationwords.put(line, 0);				
		} 
	    fstream.close();
	    
	    if(negationwords.containsKey(Stringparts[0]))
	    	Stringparts[1]="NEGATE";
	    //we focuse only on nouns, negate words, adjectives for our mining
	    if(Stringparts[1].equals("NN") || Stringparts[1].equals("NNS") || Stringparts[1].equals("JJ") || Stringparts[1].equals("JJS")
	    		|| Stringparts[1].equals("JJR") || Stringparts[1].equals("NEGATE"))
	    {
	    	/*
	    	 * query to insert into tagwords table.
	    	 */
	    	String sql = "INSERT INTO tagwords VALUES(NULL,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, Stringparts[0]);
			ps.setString(2, Stringparts[1]);
			ps.setLong(3, sentenceId);
			ps.setInt(4, relPos);
			//ps.executeUpdate();
			stmt.close();
			
	    }
	    conn.close();
		
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {

		// Initialize the tagger
		MaxentTagger tagger = new MaxentTagger("models/english-bidirectional-distsim.tagger");

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		FileInputStream fstream;
		BufferedReader br;
		

		Config config = new Config();
		conn = config.getConnection(DB_NAME,IP_ADDR,USER,PASS);
		stmt = (Statement) conn.createStatement();
		
		// load stopwords from file into a hashmap
		
		fstream = new FileInputStream(config.getminiDataPath()+"stopwords.txt");
		br = new BufferedReader(new InputStreamReader(fstream)); 
	    String line;
	    HashMap stopwords = new HashMap<>();
	    int count=0;
	    while ((line = br.readLine()) != null) {
				stopwords.put(line, 0);			
		}
	    fstream.close();

	    
	    /*
	     * query to get sentences.
	     */
		String sql = "SELECT id,sentence from reviewsentence ";
//	    String sql="select * from reviewsentence r "
//	    		+ "where r.reviewid in (Select e.id from sentiment.electronics_review e "
//	    		+ "where e.prodId IN ( SELECT business_id FROM yelpdb.mini_business where city="+'"'+"Glendale"+'"'+"))";
		rs = stmt.executeQuery(sql);

		String reviewsentence;
		String taggedReview;
		Long id;
        
		while (rs.next()) {
			id = rs.getLong("id");
			reviewsentence = rs.getString("sentence");
			//tag the string using postagger.
			taggedReview = tagger.tagString(reviewsentence);
			
			StringTokenizer st = new StringTokenizer(taggedReview);
			
			/*
			 * relPos is the relative position of each word. 
			 * Helps in calculating distance between any two words.
			 */
			int relPos=0;
			while (st.hasMoreTokens()) {
				String term = st.nextToken();
				
				relPos++;
				//remove stopwords here
				if(!stopwords.containsKey(term.split("_")[0])){
					findPOS(term, id,relPos);
				}
					
			}
		}
		stmt.close();
		conn.close();

	}
}
