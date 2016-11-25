package nFileHandler;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nLogin.Validate;
import nUtillities.Log;
import nUtillities.ShareFile;

/**
 * Servlet implementation class Delete
 */
@WebServlet("/Delete")
public class Delete extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log Log = new Log();

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		//Check User Validity
		if(Validate.verifyToken(request.getParameter("usertoken"), request.getParameter("username")) == 1){
			int fileID = Integer.parseInt(request.getParameter("fileID"));
			int userID = Integer.parseInt(request.getParameter("userID"));
			try{
				//Check Validity of the File
				if(ShareFile.validateFile(fileID)){
					//Check for User validity to the Specified File
					if(RemoveFiles.validateFilePermission(userID, fileID)){
						
						//Delete the specific file from the database
						if(RemoveFiles.deleteFileIDfromTable(fileID) == 1){
							
							Log.log("Download Process| " + request.getParameter("username") + " has deleted a file" );
							out.print("Successful");
						} else {
							out.print("Invalid-FileDelete");
						}						
					} else {
						out.print("Invalid-NoPermission");
					}					
				} else {
					out.print("Invalid-NoSuchFile");
				}				
			} catch (Exception e) {
				e.printStackTrace();				
			}
		} else{
			out.print("unverified-token");
		}
	}

}
