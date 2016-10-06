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
		Logger.getInstance().PrintInfo("User Respose POST === " + request.getParameter("username") + " AND " + request.getParameter("password"));
		
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        //TO-DO Parameters Checking
        
        Account account = new Account();
        account.setUsername(request.getParameter("username"));
        account.setPassword(request.getParameter("password"));

        if(Validate.checkUser(account)){
            Logger.getInstance().PrintInfo("Account : SUCCESSFULLY Validate");
            
            response.setContentType("text/html");
            
            out.println("U're Validated");
            
            //TO-DO SESSION ??  TOKEN ??       
            
        }
        else {
            Logger.getInstance().PrintInfo("Account : is NOT Validate");
			
			response.setContentType("text/html" );
			
			out.println("U're NOT Validated");
        }
	}

}
