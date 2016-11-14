package nFileHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxRequestConfig;

import nConstants.Constants;
import nConstants.DropboxSettings;
import nDatabase.DBAccess;
import nUtillities.AESCipher;
import nUtillities.Logger;

/**
 * This Class gathers the files from the "File" Table in the Database that are expired in the current date. Each iterations (depending the number of files expires)
 * will Remove it's file permission list ( Records of sharing to users related to the file) from the "Permission" Table in the Database
 * and Removes from the file server (Dropbox).
 * 
 * Finally Removing this list from the "File" Table in the Database.
 *  
 * @author WenJieChew
 *
 */
public class ExpiryFileRemove {		
	private static Connection connection;
	private static PreparedStatement findFileStatement;
		
	private static DbxRequestConfig config = new DbxRequestConfig("FreshDrive", Locale.getDefault().toString());
	private static DbxClient client = new DbxClient(config, DropboxSettings.getInstance().getAccessToken() );
		
	/**
	 * Gather Expired Files (ID, Name, Path, IV, Salt) and decrypt Path to delete from Dropbox.
	 * And Specific function mentioned below
	 */
	public static void deleteFileExpired(){
		try{
			connection = DBAccess.getInstance().openDB();
			
			//Find Files that are expired, that is before current date.
			findFileStatement = connection.prepareStatement( Constants.SELECT_FileFields );		
			ResultSet result = findFileStatement.executeQuery();
			
			
			if(result.next()){				
				
				do{					
					//Remove Related File From Sharing Permission List
					deletePermissionOfFiles( result.getString( "file_ID") );
					
					//Delete this files from DropBox
					client.delete( AESCipher.DecryptString( result.getBytes("file_path") , result.getBytes("file_iv") , result.getBytes("file_salt") ));
					Logger.getInstance().PrintInfo( result.getString("file_name") + " is deleted" );
				}while (result.next());
				
				//Remove Expired from List
				deleteFromFileTable();					

			}else {
				Logger.getInstance().PrintInfo("There are no files Expire today");
			}

			connection.close();
			
			
		}catch(SQLException e){
			e.printStackTrace();
			Logger.getInstance().PrintError("openDB() ", e.toString());
		}catch(Exception e){
			Logger.getInstance().PrintError("openDB() ", e.toString());		
		}

	}
	
	/**
	 * Gets the specific file ID and deletes from the Permission table in the Database
	 * @param fileID
	 * @throws SQLException
	 */	
	private static void deletePermissionOfFiles(String fileID) throws SQLException {
		findFileStatement = connection.prepareStatement( Constants.DELETE_FilePermission );
		findFileStatement.setString(1, fileID);	
		
		int rowsDel = findFileStatement.executeUpdate();	
		
		Logger.getInstance().PrintInfo("Total of " + rowsDel + " permission related to fileID of " + fileID + " is being deleted" );
	}
	
	/**
	 * Deletes all the expired files as of today from the File table in the Database
	 * @throws SQLException
	 */
	private static void deleteFromFileTable() throws SQLException{
		findFileStatement = connection.prepareStatement( Constants.DELETE_FileIDs );
		int rowsDel = findFileStatement.executeUpdate();
		
		Logger.getInstance().PrintInfo("Total of " + rowsDel + " files is being deleted at " + new java.util.Date() );
	}
	

	
	
	
	
	

}
