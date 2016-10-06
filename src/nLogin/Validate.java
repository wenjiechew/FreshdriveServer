package nLogin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import nDatabase.DBAccess;
import nObjectModel.Account;
import nUtillities.Logger;

public class Validate {
	private static Connection connection;
	private static PreparedStatement preparedStatement;

	public static boolean checkUser(Account account) {
		Logger.getInstance().PrintInfo("CheckUser()", "Checking User " + account.getUsername() + " " + account.getPassword());
		
		boolean valid = false;
		try {
			connection = DBAccess.getInstance().openDB();
			preparedStatement = connection.prepareStatement("SELECT * FROM "
					+ "UserAccount WHERE username=? and password=?");
			
			preparedStatement.setString(1, account.getUsername());
			preparedStatement.setNString(2, account.getPassword());
			ResultSet rs = preparedStatement.executeQuery();
			
			if(rs.next()){
				
				return valid = true;
			}
			DBAccess.getInstance().closeDB();
			
			
		} catch (SQLException e) {
			Logger.getInstance().PrintError("openDB() ", e.toString());
		} catch (Exception e) {
			Logger.getInstance().PrintError("openDB() ", e.toString());
		}		
		
		return valid;
	}

}
