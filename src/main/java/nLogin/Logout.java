package nLogin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nUtillities.Log;

/**
 * Services log out request, clearing the user's session credentials (i.e. token and OTP)
 * Servlet implementation class Logout
 */
@WebServlet("/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log Log = new Log();
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Logger.getInstance().PrintInfo("User Response POST === " + request.getParameter("username"));
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
		response.setContentType("text/html" );
        if(Validate.clearTokenOnLogout(request.getParameter("username"))>0){
            Log.log("Logout Process| "+ request.getParameter("username") + " logged out");
            out.println("logged-out");
        }
        else {
          out.println("1");
        }
	}
}
