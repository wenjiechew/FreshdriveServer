package nFileScan;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nConstants.ScanSettings;
import nLogin.Validate;
import nUtillities.Log;

/**
 * Servlet implementation class ScanFile
 * 
 * This Servlet returns upon a POST request and checks if the requesting user is valid (username and token)
 * before returning the Virus Scanner's API Key
 */
@WebServlet("/ScanFile")
public class ScanFile extends HttpServlet {
	private static ScanSettings scanSettings = ScanSettings.getInstance();
	private static final long serialVersionUID = 1L;
	private static Log Log = new Log();
       
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		if(Validate.verifyToken(
				request.getParameter("user_token"), request.getParameter("username")) == 1){			
			out.println( scanSettings.getScanKey() );
			Log.log("Scan File Process| "+ request.getParameter("username") + " submitted a file to scan");
		}else {
			out.println("unverified-token");
		}
		
		
	}

}
