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
import nUtillities.Logger;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static Connection connection;
	private static PreparedStatement preparedStatement;
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logger.getInstance().PrintInfo("User Response POST === " + request.getParameter("username") + " AND " + request.getParameter("password"));
		
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        Account account = new Account();
        account.setUsername(request.getParameter("username"));
        account.setPassword(BCrypt.hashpw(request.getParameter("password"),BCrypt.gensalt()));
        account.setEmail(request.getParameter("email"));
        
        if(createUser(account)){
            Logger.getInstance().PrintInfo("Account : SUCCESSFULLY Registered");            
            response.setContentType("text/html");            
            out.println("Registered");          
            
        }
        else {
            Logger.getInstance().PrintInfo("Account : is NOT Validate");
            //TODO Keep track of number of times user has failed login
			response.setContentType("text/html" );
			
			out.println("1");
        }
	}
	
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
			DBAccess.getInstance().closeDB();
			return true;
			
		} catch (SQLException e) {
			Logger.getInstance().PrintError("openDB() ", e.toString());
		} catch (Exception e) {
			Logger.getInstance().PrintError("openDB() ", e.toString());
		}		
		return false;
	}

}
