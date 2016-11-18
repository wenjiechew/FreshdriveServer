package nLogin;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nDatabase.DBAccess;
import nUtillities.Log;

/**
 * To service POST requests for a provided OTP code and username
 * Servlet implementation class VerifyOTP
 */
@WebServlet("/VerifyOTP")
public class VerifyOTP extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SecureRandom sr = new SecureRandom();
	private static Connection connection;
	private static PreparedStatement preparedStatement;
	private static Log Log = new Log();
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String username = request.getParameter("username");
        String otp = request.getParameter("otp");
        
        if(Validate.verifyOTP(username, otp)==1){
			// Generate access token
			String newToken = genToken();
			// Insert into database
			if (Validate.insertToken(username,newToken) == 0) {
				// If no records were updated (i.e. 0 rows updated)
				out.println("token-err");
			} else{ 
				// Fully authenticated, return all user informaton including token
				// in the format of id+username+email+token
				String userInfo = getUserInformation(username);
				out.println(userInfo);
				Log.log("Login Process| "+ username + " OTP accepted");
			}
        }
        else if(Validate.verifyOTP(username, otp)==-1){
        	out.println("expired");
        }
        else{
        	out.println("err");
        }
	}
	
	/**
	 * Generate a random access token string
	 * @return token string
	 */
	public String genToken() {
		return new BigInteger(130, sr).toString(32);
	}
	
	/**
	 * Called after user has been logged in successfully
	 * Returns all user information that the user will need to perform operations and verify his identity in the app 
	 * @param username
	 * @return	concat string holding user's ID, username, email and access token
	 */
	protected String getUserInformation(String username){
		String info = null;
		try{
			connection = DBAccess.getInstance().openDB();
			//Get password for selected user account based on given username
			preparedStatement = connection.prepareStatement("SELECT user_ID,username,user_email,user_token "
					+ "FROM freshdrivedb.users where username=?;");
			preparedStatement.setString(1, username);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next())
				info = rs.getString("user_ID")+"-"+rs.getString("username")+"-"+rs.getString("user_email")+"-"+rs.getString("user_token");
			
			rs.close();
			preparedStatement.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
}
