package nConstants;

public class Constants {	
	
	// ------------------------------ DATABASE CONSTANT DATA ------------------------------ //
	// JDBC driver name and database URL
	public final static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public final static String DB_URL = "jdbc:mysql://localhost:3306/freshdrivedb";
	
	//Database Connection
	public final static String USER = "root";
	public final static String PASS = "";
	
	//File DataBaseRows
	public final static String DELETE_FILEIDS = "DELETE FROM files WHERE file_ID IN ( SELECT * FROM ( SELECT file_ID FROM files WHERE file_expireOn < current_date() ) AS f );";

}
