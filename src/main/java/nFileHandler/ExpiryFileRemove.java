package nFileHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import nConstants.Constants;
import nDatabase.DBAccess;
import nUtillities.Logger;

public class ExpiryFileRemove {
	private static Connection connection;
	private static PreparedStatement preparedStatement;
	
	//Check File Dates to Delete
		public static void checkFileExpire(){
			try{
				connection = DBAccess.getInstance().openDB();
				
				//Get All file_id of the Files that is before current dates
				preparedStatement = connection.prepareStatement(Constants.getDELETE_FILEIDS());
				
				int rowsDel = preparedStatement.executeUpdate();
				
				System.out.println("No. of rows Deleted: " + rowsDel + " at " + new java.util.Date());	
				
				
				
				
				DBAccess.getInstance().closeDB();
				
				
				
			}catch(SQLException e){
				Logger.getInstance().PrintError("openDB() ", e.toString());
			}catch(Exception e){
				Logger.getInstance().PrintError("openDB() ", e.toString());
			}
		}

}
