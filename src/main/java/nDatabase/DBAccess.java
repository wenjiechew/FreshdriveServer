package nDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import nConstants.Constants;
import nConstants.DBSettings;

/**
 * The Class is create a Connection with the mySQL database.
 * @author WenJieChew
 *
 */
public class DBAccess {
	private static DBAccess instance;
	private DBSettings setting = DBSettings.getInstance();

	
	public static DBAccess getInstance() { 
		if( instance == null ) { instance = new DBAccess(); }	
		return instance;
	}
	
	public static DBAccess instanceCheck(){
		return instance;
	}
	
	private Connection connection;
	
	/**
	 * Gets mySQL's JDBC Driver Class 
	 * @return returns an Open connection with the Database, with the Database ( URL, User name and database's password )
	 * @throws SQLException
	 * @throws Exception
	 */
	public Connection openDB() throws SQLException, Exception{		
		//Register JDBC
		Class.forName( Constants.JDBC_DRIVER );
		
		//Opening Connection		
		connection = DriverManager.getConnection( setting.getDB_URL() , setting.getDB_USER(), setting.getDB_PASS());
		return connection;			
	}
	
	public void closeDB() throws SQLException {
		connection.close();
	}
	
	
}