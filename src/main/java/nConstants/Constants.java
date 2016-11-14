package nConstants;

/**
 * This File is a Collects all Constants Variables that is used throughout the application, to prevent REPEATATIVE CODES
 * @author WenJieChew
 *
 */
public class Constants {
	static String currentPath = null;
	static String FileLocation = "/META-INF/Configuration.json";
	
	// JDBC driver name and database URL
	public static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		
	//File Table
	public static String DELETE_FileIDs = "DELETE FROM files WHERE file_ID IN ( SELECT * FROM ( SELECT file_ID FROM files WHERE file_expireOn < current_date() ) AS f );";
	public static String DELETE_FilePermission = "DELETE FROM permissions WHERE permission_fileID = ?";
	public static String SELECT_FileFields = "SELECT file_ID, file_name, file_path, file_iv, file_salt FROM files WHERE file_expireOn < current_date()";
	

	
	public static void setCurrentPath(String currentPath) {
		Constants.currentPath = currentPath;
	}
	
	public static String getCurrentPath(){
		return currentPath + FileLocation;
	}

	






}
