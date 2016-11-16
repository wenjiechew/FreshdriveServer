package nLogin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import nDatabase.DBAccess;
import nObjectModel.Account;

public class Validate {
	private static Connection connection;
	private static PreparedStatement preparedStatement;

	public static boolean checkUser(Account account) {
		String password = account.getPassword();
		boolean valid = false;
		try {
			connection = DBAccess.getInstance().openDB();
			// Get password for selected user account based on given username
			preparedStatement = connection.prepareStatement("SELECT user_password FROM users WHERE username=?");

			preparedStatement.setString(1, account.getUsername());
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next()) {
				// Retrieve hashed password in database
				String hashed = rs.getString("user_password");
				// Compare and validate given password input
				if (BCrypt.checkpw(password, hashed))
					return valid = true;
			}
			DBAccess.getInstance().closeDB();
			
		} catch (SQLException e) {
		} catch (Exception e) {
		}

		return valid;
	}

	public static int isLoggedIn(Account account) {
		int rs = 0;
		try {
			connection = DBAccess.getInstance().openDB();
			preparedStatement = connection
					.prepareStatement("SELECT COUNT(*) FROM users WHERE username=? and user_token is null");
			preparedStatement.setString(1, account.getUsername());
			ResultSet results = preparedStatement.executeQuery();
			if (results.next())
				rs = results.getInt("COUNT(*)");


			results.close();
			preparedStatement.close();
			connection.close();
		} catch (SQLException e) {
		} catch (Exception e) {
		}
		return rs;
	}

	public static int insertToken(String username, String token) {
		int rs = 0;
		try {
			connection = DBAccess.getInstance().openDB();
			// Update database with generated token value
			preparedStatement = connection
					.prepareStatement("UPDATE users SET user_token=? WHERE user_token is null AND username=?");
			preparedStatement.setString(1, token);
			preparedStatement.setString(2, username);
			rs = preparedStatement.executeUpdate();

			preparedStatement.close();
			connection.close();
		} catch (SQLException e) {
		} catch (Exception e) {
		}
		return rs;
	}

	public static int clearTokenOnLogout(String username) {
		int done = 0;
		try {
			connection = DBAccess.getInstance().openDB();
			// Update database with generated token value
			preparedStatement = connection
					.prepareStatement("UPDATE users SET user_token=NULL,user_OTP=NULL WHERE username=?");
			preparedStatement.setString(1, username);
			done = preparedStatement.executeUpdate();

			preparedStatement.close();
			connection.close();
		} catch (SQLException e) {
		} catch (Exception e) {
		}
		return done;
	}

	// Validate 2FA OTP
	public static int verifyOTP(String un, String code) {
		String dbOTP = null;
		try {
			connection = DBAccess.getInstance().openDB();
			// Get password for selected user account based on given username
			preparedStatement = connection.prepareStatement("SELECT user_OTP FROM users WHERE username=?");
			preparedStatement.setString(1, un);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next())
				dbOTP = rs.getString("user_OTP");
			if (dbOTP == null)
				return -1;
			if (dbOTP.equals(code))
				return 1;


			rs.close();
			preparedStatement.close();
			connection.close();
		} catch (SQLException e) {
		} catch (Exception e) {
		}
		return 0;
	}
	
	//Verify a provided access token against the database records
	public static int verifyToken(String token, String username){
		int rsCount = 0;
		try{
			connection = DBAccess.getInstance().openDB();
			//Get password for selected user account based on given username
			preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM "
					+ "users WHERE username=? AND user_token=?");
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, token);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next())
				rsCount = rs.getInt("COUNT(*)");

			rs.close();
			preparedStatement.close();
			connection.close();
		} catch (SQLException e) {
		} catch (Exception e) {
		}
		return rsCount;
	}
}
