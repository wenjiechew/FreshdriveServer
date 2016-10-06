package nDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import nUtillities.Logger;

/**
 * @author WenJieChew
 *
 */
public class DBAccess {
	private static DBAccess instance;
	
	public static DBAccess getInstance() { 
		if( instance == null ) { instance = new DBAccess(); }
		return instance;
	}
	
	// JDBC driver name and database URL
	final static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	final static String DB_URL = "jdbc:mysql://localhost:3306/freshdrive";
	
	//Database Connection
	final static String USER = "root";
	final static String PASS = "";
	
	private Connection connection;
	
	public Connection openDB() throws SQLException, Exception{
		Logger.getInstance().PrintInfo("OpenDB ()", "Connecting to database..." );

		//Register JDBC
		Class.forName(JDBC_DRIVER);
		
		//Opening Connection		
		connection = DriverManager.getConnection(DB_URL, USER, PASS);
		Logger.getInstance().PrintInfo("Database Successful Connection");
		return connection;			
	}
	
	public void closeDB() throws SQLException {
		Logger.getInstance().PrintInfo("CloseDB()", "Closing Database...");

		connection.close();
		
		Logger.getInstance().PrintInfo("Database is Closed");
	}

}
