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

public class ExpiryFileRemove {
	private static DropboxSettings dropboxSettings = new DropboxSettings();
	
	private static Connection connection;
	private static PreparedStatement preparedStatement;
	private static PreparedStatement findFileStatement;
	
	//Check File Dates to Delete
		public static void checkFileExpire(){
			String fileName = "";
			DbxRequestConfig config = new DbxRequestConfig("FreshDrive", Locale.getDefault().toString());
			System.out.println("Connect to dropbox");
			DbxClient client;
			client = new DbxClient(config, dropboxSettings.getAccessToken());
			try{
				connection = DBAccess.getInstance().openDB();
				
				//Get All file_id of the Files that is before current dates
				preparedStatement = connection.prepareStatement( Constants.getDELETE_FILEIDS() );
				String findFileQuery = "SELECT file_name, file_path, file_iv, file_salt FROM files WHERE file_expireOn < current_date()";
				findFileStatement = connection.prepareStatement(findFileQuery);
				ResultSet rset = findFileStatement.executeQuery();
				byte[] encryptedPath = null;
				byte[] fileIv = null;
				byte[] fileSalt = null;
//				String[] fileQueryArray = null;
				while(rset.next()){
					fileName = rset.getString("file_name");
					encryptedPath = rset.getBytes("file_path");
					fileIv = rset.getBytes("file_iv");
					fileSalt = rset.getBytes("file_salt");
					System.out.println("Encrypted path: "+encryptedPath);
					System.out.println("Encrypted IV: "+fileIv);
					System.out.println("Encrypted Salt: "+fileSalt);
//					System.out.println("Expire result set: " + rset.getString(1));
					String decryptedString = AESCipher.DecryptString(encryptedPath, fileIv, fileSalt);
					client.delete(decryptedString);
				}
				
//				System.out.println("decrypted string: "+ decryptedString);
				int rowsDel = preparedStatement.executeUpdate();
				
				System.out.println("No. of rows Deleted: " + rowsDel + " at " + new java.util.Date());	
				
				//
				// // access token for the dropbox account. may need to encrypt this
//				String accessToken = "-TcOHePlr9AAAAAAAAAACMWGsYvDXPTDcThy6nM8r0hwG-Mz5cEqtDxcDygkg9i3";
				//
				
				
				
				DBAccess.getInstance().closeDB();
				
				
				
				
				
			}catch(SQLException e){
				Logger.getInstance().PrintError("openDB() ", e.toString());
			}catch(Exception e){
				Logger.getInstance().PrintError("openDB() ", e.toString());
			}
		}

}
