package nLogin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import nDatabase.DBAccess;
import nObjectModel.Account;

/**
 * This class contains all validation methods, mainly to compare and verify given inputs against database records 
 * 
 * @author ottoma
 */
public class Validate {
	private static Connection connection;
	private static PreparedStatement preparedStatement;

	/**
	 * Verify a user provided credentials against database records
	 * @param account	contains user account information like username and password
	 * @return	true if valid credentials, else false
	 */
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
					valid= true;
			}
			DBAccess.getInstance().closeDB();
			
		} catch (SQLException e) {
		} catch (Exception e) {
		}

		return valid;
	}

	/**
	 * Check if the account being logged in is ALREADY logged by checking for existing user token on the database
	 * 
	 * NOTE: A inactive / logged out user will not have an active user token in the database, where the field will
	 * be null. 
	 * 
	 * @param account	contains user information such as username and token
	 * @return 1 if there is an existing token, else returns 0
	 */
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * Inserts token into the database 
	 * @param username	for whom the OTP is generated for
	 * @param token	six-digit number that was generated 
	 * @return 1 if updated, else return 0 if no database rows were affected
	 */
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

	/**
	 * Removes token and OTP from user's database record when logging out
	 * @param username	username for whom to clear credentials for. 
	 * @return	1 if updated, 0 if no database row were updated.
	 */
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return done;
	}

	/**
	 * Verifies a given OTP code for a given username against existing database records
	 * @param un username to verify
	 * @param code	provided OTP code to verify with
	 * @return 1 if OTP verified, -1 if OTP doesn't exists, else return 0 
	 */
	public static int verifyOTP(String un, String code) {
		String dbOTP = null;
		try {
			connection = DBAccess.getInstance().openDB();
			// Get OTP for selected user account based on given username
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * Verify a provided access token against the database records
	 * @param token	provided access token string to verify with
	 * @param username	username to verify for	
	 * @return 0 if no records for given pair of username and token is found, else 1 if input is verified successfully
	 */
	public static int verifyToken(String token, String username){
		int rsCount = 0;
		try{
			connection = DBAccess.getInstance().openDB();
			//Get number of records from database with provided username and token
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rsCount;
	}
}
