package sentiment;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;
import rita.support.PorterStemmer;




public class SentiSentence {
	/**************************************************
	 * 
	 * STEP 10-I: USING STANFORD SENTIMENT GENERATOR 
	 * 
	 * generates sentiment for all the opinion sentences.
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
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		java.sql.PreparedStatement ps = null;

		Config config = new Config();
		conn = config.getConnection(DB_NAME,IP_ADDR,USER,PASS);
		stmt = (Statement) conn.createStatement();
		
		String sql = "SELECT p.feature,p.opinion,coalesce(n.isNegateNear,0) as isNegateNear,p.sentenceId,p.minDistId from potentialfeature p "
				+ "left outer join "
				+ "negateproximity n on p.sentenceId=n.sentenceId and p.minDistId = n.relPos;";
		rs = stmt.executeQuery(sql);
		Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		while(rs.next()){
			String text = "";
			int negated = rs.getInt("isNegateNear");
			text=rs.getString("opinion")+" "+rs.getString("feature");
//			if(negated==-1)
//				text="not "+text;
			Long sentenceId=rs.getLong("sentenceId");
			Long relPos = rs.getLong("minDistId");
	        Annotation annotation = pipeline.process(text);
	        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
	        for (CoreMap sentence : sentences) {
	            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
	            int polarity=0;
	            if(sentiment.equals("Very positive"))
	            	polarity=2;
	            else if(sentiment.equals("Positive"))
	            	polarity=1;
	            else if(sentiment.equals("Neutral"))
	            	polarity=0;
	            else if(sentiment.equals("Negative"))
	            	polarity=-1;
	            else if(sentiment.equals("Very negative"))
	            	polarity=-2;
	            
	            if(negated==-1)
	            	polarity*=-1;
	            
	            System.out.println(polarity+":"+sentiment + "\t" + sentence);
	            sql = "INSERT INTO sentisentence VALUES(null,?,?,?,?,?)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, rs.getString("opinion"));
				ps.setString(2,rs.getString("feature"));
				ps.setLong(3, sentenceId);
				ps.setLong(4,relPos);
				ps.setInt(5,polarity);
				//ps.executeUpdate();
				break;
	        }

			
			
		}
		
 
	}

}
