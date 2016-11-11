package nConstants;

public class Constants {
	static String currentPath = null;
	static String FileLocation = "/META-INF/Configuration.json";
	
	// JDBC driver name and database URL
	private static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		
	//File DataBaseRows
	private static String DELETE_FILEIDS = "DELETE FROM files WHERE file_ID IN ( SELECT * FROM ( SELECT file_ID FROM files WHERE file_expireOn < current_date() ) AS f );";

	
	public static void setCurrentPath(String currentPath) {
		Constants.currentPath = currentPath;
	}
	
	public static String getCurrentPath(){
		return currentPath + FileLocation;
	}

	public static String getJDBC_DRIVER() {
		return JDBC_DRIVER;
	}

	public static String getDELETE_FILEIDS() {
		return DELETE_FILEIDS;
	}






}
