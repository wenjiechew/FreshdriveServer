package nFileHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxRequestConfig;

import nConstants.Constants;
import nDatabase.DBAccess;
import nUtillities.Logger;

public class ExpiryFileRemove {
	private static Connection connection;
	private static PreparedStatement preparedStatement;
	private static PreparedStatement findFileStatement;
	
	//Check File Dates to Delete
		public static void checkFileExpire(){
			try{
				connection = DBAccess.getInstance().openDB();
				
				//Get All file_id of the Files that is before current dates
				preparedStatement = connection.prepareStatement(Constants.DELETE_FILEIDS);
				String findFileQuery = "SELECT file_name FROM files WHERE file_expireOn < current_date()";
				findFileStatement = connection.prepareStatement(findFileQuery);
				ResultSet rset = findFileStatement.executeQuery();
				String[] fileQueryArray = null;
				while(rset.next()){
					System.out.println("Expire result set: " + rset.getString(1));
				}
				int rowsDel = preparedStatement.executeUpdate();
				
				System.out.println("No. of rows Deleted: " + rowsDel + " at " + new java.util.Date());	
				DbxRequestConfig config = new DbxRequestConfig("FreshDrive", Locale.getDefault().toString());
				//
				// // access token for the dropbox account. may need to encrypt this
				String accessToken = "-TcOHePlr9AAAAAAAAAACMWGsYvDXPTDcThy6nM8r0hwG-Mz5cEqtDxcDygkg9i3";
				//
				System.out.println("Connect to dropbox");
				DbxClient client;
				
				
				client = new DbxClient(config, accessToken);			
				
				DBAccess.getInstance().closeDB();
				
				
				
			}catch(SQLException e){
				Logger.getInstance().PrintError("openDB() ", e.toString());
			}catch(Exception e){
				Logger.getInstance().PrintError("openDB() ", e.toString());
			}
		}

}
