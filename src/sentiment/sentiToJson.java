package sentiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;

public class sentiToJson {

	// Database name
	static final String DB_NAME = "sentiment";
	// Database ip
	static final String IP_ADDR = "127.0.0.1:3306";
	
	// Database credentials
	static final String USER = "root";
	static final String PASS = "1234";
	
	public static void main(String[] args) throws SQLException, IOException {
		// TODO Auto-generated method stub
			
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		java.sql.PreparedStatement ps = null;

		Config config = new Config();
		conn = config.getConnection(DB_NAME,IP_ADDR,USER,PASS);
		
		stmt = (Statement) conn.createStatement();
		
		BufferedWriter writer=null;
		writer = new BufferedWriter(new FileWriter(config.getminiJSONPath()+ args[0] + ".json"));

		String sql = "select *,count(*) c ,sum(f.polarity) s from (select t2.word,t1.polarity from sentiments t1"
					+" inner join"
					+"(select word,sentence,prodId,reviewId from electronics_review e"
					+" inner join dummy d on e.id = d.reviewId and prodId="+'"'+args[0]+'"'+") t2"
					+" on t2.sentence = t1.sentence) f group by f.word "
					+ "order by c desc limit 50";
		
		rs = stmt.executeQuery(sql);
		int count=0;
		
		

		JSONArray ja = new JSONArray();
		

		JSONObject mainObj = new JSONObject();
		JSONObject sj = new JSONObject();
		JSONObject cj = new JSONObject();
		//writer.write("[");		
		//writer.newLine();		
		while(rs.next()){
			String word = rs.getString("word");
			int frequency = Integer.parseInt(rs.getString("c"));
			int polarity = Integer.parseInt(rs.getString("s"));
			String colorcode="";
			if(polarity<0)
				colorcode="#d01919";
			else if(polarity>0)
				colorcode="#3db11a";
			else
				colorcode="#8d8989";
			
			
			
			sj.put("style", "color:"+colorcode);
			frequency =frequency *2;
			if(frequency >50)
				frequency =50;
			JSONObject jo = new JSONObject();
			jo.put("text", word);
			jo.put("weight", frequency);
			jo.put("polarity", polarity);
			ja.put(jo);
			
//			
//			if(count==0){
//				writer.write("{"+"text:"+'"'+word+'"'+","+"weight:"+frequency+","+"html:{style:"+'"'+"color:"+colorcode+'"'+"}}");
//				writer.newLine();
//			}
//				
//			else{
//				writer.write(",{"+"text:"+'"'+word+'"'+","+"weight:"+frequency+","+"html:{style:"+'"'+colorcode+'"'+"}}");
//				writer.newLine();
//			}
				
			count++;
		}
		mainObj.put("wordCloud", ja);
		
		writer.write(mainObj.toString());
		writer.close();
		stmt.close();
		conn.close();
	}

}
