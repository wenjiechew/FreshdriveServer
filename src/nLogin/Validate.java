package nLogin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import nDatabase.DBAccess;
import nObjectModel.Account;
import nUtillities.Logger;

public class Validate {
	private static Connection connection;
	private static PreparedStatement preparedStatement;

	public static boolean checkUser(Account account) {
        String password = account.getPassword();
		Logger.getInstance().PrintInfo("CheckUser()", "Checking User " + account.getUsername() + " PW: " + password);
		boolean valid = false;
		try {
			connection = DBAccess.getInstance().openDB();
			//Get password for selected user account based on given username
			preparedStatement = connection.prepareStatement("SELECT user_password FROM "
					+ "users WHERE username=?");
			
			preparedStatement.setString(1, account.getUsername());
			ResultSet rs = preparedStatement.executeQuery();
			
			if(rs.next()){
				//Retrieve hashed password in database
				String hashed = rs.getString("user_password");
				//Compare and validate given password input
				if (BCrypt.checkpw(password, hashed))
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
	
	public static int insertToken(Account account){
		int rs = 0;
		try {
			connection = DBAccess.getInstance().openDB();
			//Update database with generated token value
			preparedStatement = connection.prepareStatement("UPDATE users SET user_token=? "
					+ "WHERE user_token is null AND username=?");
			preparedStatement.setString(1, account.getToken());
			preparedStatement.setString(2, account.getUsername());
			rs = preparedStatement.executeUpdate();
			DBAccess.getInstance().closeDB();
		} catch (SQLException e) {
			Logger.getInstance().PrintError("openDB() ", e.toString());
		} catch (Exception e) {
			Logger.getInstance().PrintError("openDB() ", e.toString());
		}		
		return rs;
	}
	
	public static int clearTokenOnLogout(String username){
		int done = 0;
		try{
			connection = DBAccess.getInstance().openDB();
			//Update database with generated token value
			preparedStatement = connection.prepareStatement("UPDATE users SET user_token=NULL "
					+ "WHERE username=?");
			preparedStatement.setString(1, username);
			done = preparedStatement.executeUpdate();
			DBAccess.getInstance().closeDB();
		} catch (SQLException e) {
			Logger.getInstance().PrintError("openDB() ", e.toString());
		} catch (Exception e) {
			Logger.getInstance().PrintError("openDB() ", e.toString());
		}
		return done;
	}
}
