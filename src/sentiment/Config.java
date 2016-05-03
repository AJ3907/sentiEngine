package sentiment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Config {
	
	
	/**********************************************************
	 * This is the Configuration file.
	 * Here one can edit the database urls and credentials.
	 * 
	 * The method getConnection(args) is used to get connection of the specified database url.
	 * 
	 * The method getFeatureSetPath() returns directory path where mining results are stored.
	 * 
	 * The method getReviewSetPath() returns directory path where reviewset is stored.
	 * 
	 * 
	 */

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	
	// Database name
	
	
	private String minitestdatapath ="./generatedFiles/reviewset/";
	private String minifilepath ="./generatedFiles/features/";
	
	public Connection getConnection(String DB_NAME,String IP_ADDR,String USER, String PASS){
		String DB_URL = "jdbc:mysql://"+IP_ADDR+"/"+DB_NAME+"?autoReconnect=true&failOverReadOnly=false&maxReconnects=20";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn;
			try {
				conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
				return conn;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	public String getminiFilePath(){
		return minifilepath;
	}
	
	public String getminiDataPath(){
		return minitestdatapath;
	}
	
	
	
}
