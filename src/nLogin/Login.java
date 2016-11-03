package nLogin;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nObjectModel.Account;
import nUtillities.Logger;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
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
        account.setPassword(request.getParameter("password"));
        if(Validate.checkUser(account)){
            Logger.getInstance().PrintInfo("Account : SUCCESSFULLY Validate");
            //Generate token
            account.setToken(genToken());
            response.setContentType("text/html");   
            //Insert into database
            if(Validate.insertToken(account)==0){
            	//If no records were updated (i.e. 0 rows updated)
            	//Active token existing; someone is currently logged in with the account
                out.println("active-token");
            }
            else         
            	out.println(account.getToken()); //Return access token as results          
        }
        else {
            Logger.getInstance().PrintInfo("Account : is NOT Validate");
            //TODO Keep track of number of times user has failed login
			response.setContentType("text/html" );
			
			out.println("1");
        }
	}
	
	public String genToken(){
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}
	

}
