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
	
	private Connection connection;
	
	public Connection openDB() throws SQLException, Exception{
		Logger.getInstance().PrintInfo("OpenDB ()", "Connecting to database..." );

		//Register JDBC
		Class.forName(nConstants.Constants.JDBC_DRIVER);
		
		//Opening Connection		
		connection = DriverManager.getConnection(nConstants.Constants.DB_URL, nConstants.Constants.USER, nConstants.Constants.PASS);
		Logger.getInstance().PrintInfo("Database Successful Connection");
		return connection;			
	}
	
	public void closeDB() throws SQLException {
		Logger.getInstance().PrintInfo("CloseDB()", "Closing Database...");

		connection.close();
		
		Logger.getInstance().PrintInfo("Database is Closed");
	}

}
