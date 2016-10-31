package nLogin;

import java.io.IOException;
import java.io.PrintWriter;

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
            
            //Check for PROPER RESULTS .. tO EDITED!!
            //TODO Return session/login token
            
            response.setContentType("text/html");            
            out.println("Validated");          
            
        }
        else {
            Logger.getInstance().PrintInfo("Account : is NOT Validate");
            //TODO Keep track of number of times user has failed login
			response.setContentType("text/html" );
			
			out.println("1");
        }
	}

}
