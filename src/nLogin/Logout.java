package nLogin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nUtillities.Logger;

/**
 * Servlet implementation class Logout
 */
@WebServlet("/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Logger.getInstance().PrintInfo("User Response POST === " + request.getParameter("username"));
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
		response.setContentType("text/html" );
        if(Validate.clearTokenOnLogout(request.getParameter("username"))>0){
            Logger.getInstance().PrintInfo("Account : SUCCESSFULLY Logout");
            out.println("logged-out");
        }
        else {
            Logger.getInstance().PrintInfo("Account : is NOT Logout");
			out.println("1");
        }
	}
}
