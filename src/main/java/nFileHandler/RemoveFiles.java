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
import nUtillities.Log;

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
public class RemoveFiles {		
	private static Connection connection;
	private static PreparedStatement findFileStatement;
	private static Log Log = new Log();
		
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
					
					Log.log("Expiry Process|"+ result.getString("file_name") + " is deleted");
				}while (result.next());
				
				//Remove Expired from List
				deleteFromFileTable();					

			}else {
				Log.log("Expiry Process| 0 files expired today");
			}

			result.close();
			findFileStatement.close();
			connection.close();			
		}catch(SQLException e){
			e.printStackTrace();
		}catch(Exception e){	
			e.printStackTrace();
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
		Log.log("Expiry Process| Total of " + rowsDel + " permission related to fileID of " + fileID + " is being deleted" );
	}
	
	/**
	 * Deletes all the expired files as of today from the File table in the Database
	 * @throws SQLException
	 */
	private static void deleteFromFileTable() throws SQLException{
		findFileStatement = connection.prepareStatement( Constants.DELETE_FileIDs );
		int rowsDel = findFileStatement.executeUpdate();
		
		Log.log("Expiry Process| Total of " + rowsDel + " files is being deleted at " + new java.util.Date() );
	}
	

	public static int deleteFileIDfromTable(int fileID) throws Exception {
		int rowDel = 0;
		connection = DBAccess.getInstance().openDB();		
		
		//Get Path of the fileID
		findFileStatement = connection.prepareStatement(Constants.SELECT_FileID);
		findFileStatement.setInt(1, fileID );
		ResultSet rs = findFileStatement.executeQuery();
		if (rs.next()){
			//Delete from Dropbox first
			client.delete( AESCipher.DecryptString( rs.getBytes("file_path") , rs.getBytes("file_iv") , rs.getBytes("file_salt") ));
			
			findFileStatement = connection.prepareStatement( Constants.DELECT_FileID );
			findFileStatement.setInt(1, fileID);
			rowDel = findFileStatement.executeUpdate();
		}
		rs.close();
		findFileStatement.close();
		connection.close();
		return rowDel;		
	}
	
	/**
	 * This Function check if stated 'fileID' owned by the owner_id is already in the database
	 * @param fileName
	 * @param owner_id
	 * @return Boolean True / False (Count >= 1 is False || Count == 0 is True )
	 * @throws SQLException
	 */
	public static boolean validateFilePermission (int userID, int fileID) throws Exception{
		int count = 0;
		connection = DBAccess.getInstance().openDB();	
		findFileStatement = connection.prepareStatement(Constants.SELECT_CheckValid);
		findFileStatement.setInt(1, userID);
		findFileStatement.setInt(2, fileID);
		ResultSet rs = findFileStatement.executeQuery();
		if(rs.next()){
			count = rs.getInt(1);
		}
		rs.close();
		return (count == 1);
		
	}
	
	
	
	
	
	

}
