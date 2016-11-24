package nConstants;

/**
 * This File is a Collects all Constants Variables that is used throughout the application, to prevent repititive codes
 * @author WenJieChew
 *
 */
public class Constants {
	static String currentPath = null;
	static String configFileLocation = "/META-INF/Configuration.json";
	static String downloadFileLocation = "/Temp-Files/";
	
	// JDBC driver name and database URL
	public static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		
	/*********************** File Table ***********************/
	public static String DELETE_FileIDs = "DELETE FROM files WHERE file_ID IN ( SELECT * FROM ( SELECT file_ID FROM files WHERE file_expireOn < current_date() ) AS f );";
	public static String DELETE_FilePermission = "DELETE FROM permissions WHERE permission_fileID = ?";
	public static String SELECT_FileFields = "SELECT file_ID, file_name, file_path, file_iv, file_salt FROM files WHERE file_expireOn < current_date()";
	
	//File Upload
	public static String SELECT_FileOwnerID = "SELECT Count(file_id) from files WHERE file_name=? AND file_ownerID =?";
	public static String INSERT_FileTable = "INSERT INTO files (file_name, file_path, file_size, file_createdOn, file_ownerID, file_expireOn, file_salt, file_iv) "
			+ "VALUES (?,?,?,?,?,?,?,?)" ;
	public static String INSERT_FilePermission = "INSERT INTO permissions (permission_fileID, permission_sharedToUserID)"
			+ "VALUES((SELECT file_ID FROM files WHERE file_name = ? AND file_ownerID = ?), ? )";
	
	//File Retrieve for Permission Related
	public static String SELECT_FileRelatedtoID = "SELECT file_name, file_ID from files WHERE file_ID IN "
			+ "(SELECT permission_fileID from permissions where permission_sharedToUserID = ?)";
	
	//File Download get fileID
	public static String SELECT_FileID = "SELECT file_path, file_salt, file_iv, file_name, file_size FROM files WHERE file_ID = ?" ;
	
	//Sets the current path to specify where the server is located at
	public static void setCurrentPath(String currentPath) {
		Constants.currentPath = currentPath;
	}
	
	//Gets the config path with reference to the current path
	public static String getConfigPath(){
		return new String(currentPath + configFileLocation);
	}
	
	//Gets file path with reference to the current path
	public static String getFilePathLocation(){
		return new String(currentPath + downloadFileLocation);
	}

	






}
