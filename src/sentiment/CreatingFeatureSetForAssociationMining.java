//step 3
//generating set of nouns for doing association mining
package sentiment;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CreatingFeatureSetForAssociationMining {
	
	/*******************
	 * STEP 4:
	 * 
	 * This step creates a transaction set of nouns appearing in a sentence and put them in file.
	 * This transaction set is to used in association mining in Step 5.
	 * Also, it creates a temp file for each productId, consisting information later to be used to
	 * do compact pruning and redundant pruning. Basically, its created to handle feature phrases.
	 * 
	 * "input*" files contain transaction sets.
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
	 
	 public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		  Connection conn = null;
		  Statement stmt = null, stmt2 = null;
		  ResultSet rs = null, rs2 = null;
		  
		  Config config = new Config();
		  conn = config.getConnection(DB_NAME,IP_ADDR,USER,PASS);
		  stmt = (Statement) conn.createStatement();
		  
		  String productId = args[0];
		  
		  BufferedWriter writer,writer2 = null;
		  
		  /*
		   * query to get sentenceId,word,relPos for nouns
		   */
		  String sql = "Select w.sentenceId,w.word,w.relPos from electronics_review e "
		  			+ "inner join (SELECT r.reviewId,t.sentenceId,t.word,t.relPos FROM tagwords t "
		  			+ "INNER JOIN reviewsentence r on t.sentenceId = r.id and (t.posTag='NN' OR t.posTag='NNS')) w on w.reviewId = e.id and e.prodId="
		 
		  			+ "'" + productId + "'"
		  			;
		   rs2 = stmt.executeQuery(sql);
		   
		   writer = new BufferedWriter(new FileWriter(config.getminiFilePath()+"input"+ productId + ".txt"));
		   writer2 = new BufferedWriter(new FileWriter(config.getminiFilePath()+"temp"+ productId + ".txt"));
		   
		   String word = "";
		   Long prevsentenceId = -99l, sentenceId;
		   StringBuilder buffer = new StringBuilder();
		   String out ="";
		   Long prevwordId = -99L,wordId;
		   while (rs2.next()) {
			    sentenceId = rs2.getLong("sentenceId");
			    word = rs2.getString("word");
			    wordId = rs2.getLong("relPos");
			    
			    if (sentenceId > prevsentenceId) {
			     if(prevsentenceId!=-99L){
			    	 
			    	 //new sentence will start, so flush the buffer of the previous sentence.
			    	 writer.write(buffer.toString().toLowerCase());
			    	 //append the sentenceId, so that we can differentiate each line by sentenceId.
				     out=out+"~"+prevsentenceId;
				     writer2.write(out.toLowerCase());
				     
			     }
			     if(prevsentenceId!=-99l){
			    	 //new sentence so new line.
			    	 writer.newLine(); 
				     writer2.newLine();
			     }
			     //empty the buffer initially for each sentence.
			     out="";
			     buffer = new StringBuilder();
			    }
			    
			    if((wordId-prevwordId)<=3l && (sentenceId-prevsentenceId)<1l){
			    	
			    	/*This is the out to writer2.
			    	 * What is being done?
			    	 * The consecutive features(nouns), each adjacent feature
			    	 * being seperated by atmost 3 words.
			    	 * 
			    	 * The word,wordId is stored.
			    	 * 
			    	 * If more than 3 distance, then put a "." at end of that 
			    	 * consecutive features sentence. So that consecutive feature 
			    	 * sentence can be our feature phrase candidate.
			    	 * 
			    	 */
			    	out = out + " "+word+","+wordId;
			    	
			    }
			    else
			    	out=out+"."+word+","+wordId;

			    if (!buffer.toString().contains(word)) {
			    	
			    	/*
			    	 * buffer is the transaction set for a sentence.
			    	 * it contains distinct nouns of a sentence seperated by " ".
			    	 */
			     buffer.append(word + " ");
			    }
			    prevwordId = wordId;
			    prevsentenceId = sentenceId;
			    
		   }
		   writer.write(buffer.toString());
		   out=out+"~"+prevsentenceId;
		   writer2.write(out);
		   writer2.close();
		   if (writer != null)
			   writer.close();
		   stmt.close();
		   conn.close();
	 }
}