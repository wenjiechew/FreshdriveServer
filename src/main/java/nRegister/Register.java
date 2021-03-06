package nRegister;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;

import nDatabase.DBAccess;
import nObjectModel.Account;
import nUtillities.Log;

/**
 * Services all requests for a new user trying to register a new account
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static Connection connection;
	private static PreparedStatement preparedStatement;
	private static final long serialVersionUID = 1L;
	private static Log Log = new Log();
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        //Retrieve all parameters that were passed in the request
        Account account = new Account();
        account.setUsername(request.getParameter("username"));
        account.setPassword(BCrypt.hashpw(request.getParameter("password"),BCrypt.gensalt()));
        account.setEmail(request.getParameter("email"));
        
        /*
         * Try to create the user with given parameters 
         * and let the user know if the registration was successful
         */
        if(createUser(account)){
            response.setContentType("text/html");            
            Log.log("Register Process| New account: '"+ account.getUsername() + " ' created");
            out.println("Registered");
        }
        //If registration failed, return error code which will be handled and properly displayed to the user at client
        else {
			response.setContentType("text/html" );
			out.println("1");
        }
	}
	
	/**
	 * Try to create a new user account in the database with the provided account information 
	 * @param account	object holding all account information (username, email, password)
	 * @return true if account created, else false
	 */
	public boolean createUser(Account account) {
		try {
			connection = DBAccess.getInstance().openDB();
			//Get password for selected user account based on given username
			preparedStatement = connection.prepareStatement("INSERT INTO users (username, user_email, user_password) "
					+ "VALUES (?,?,?)");
			preparedStatement.setString(1, account.getUsername());
			preparedStatement.setString(2, account.getEmail());
			preparedStatement.setString(3, account.getPassword());
			preparedStatement.executeUpdate();


			preparedStatement.close();
			connection.close();
			return true;
			
		} catch (SQLException e) {
			} catch (Exception e) {
			}		
		return false;
	}

}
