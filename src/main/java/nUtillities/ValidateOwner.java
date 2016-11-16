package nUtillities;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nDatabase.DBAccess;

/**
 * Servlet implementation class ValidateOwner
 */
@WebServlet("/ValidateOwner")
public class ValidateOwner extends HttpServlet {
	private static Connection connection;
	private static PreparedStatement preparedStatement;
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ValidateOwner() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		int fileID = Integer.parseInt(request.getParameter("fileID"));
		int userID = Integer.parseInt(request.getParameter("userID"));
		
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        System.out.println("doPost(): " + request.getParameter("userID") + ", " + request.getParameter("fileID"));
        
        out.print(validateOwnership(userID, fileID));
	}
	
	/**
	 * Validates if user is the owner of the specific file as only owners have rights to share files
	 * @param userID
	 * @param fileID
	 * @return
	 */
	public boolean validateOwnership(int userID, int fileID){
		int ownerID = 0;

		try {
			connection = DBAccess.getInstance().openDB();
			preparedStatement = connection.prepareStatement("SELECT file_ownerID FROM "
					+ "files WHERE file_ID=?");
				
			preparedStatement.setInt(1, fileID);
			ResultSet rs = preparedStatement.executeQuery();
			
			if(rs.next()){
				ownerID = rs.getInt("file_ownerID");
				System.out.print(ownerID);
				if (ownerID == userID){
					System.out.println("validateOwnership(): Owner validated.");
					return true;
				}
				else
				{
					System.out.println("validateOwnership(): User is not the file owner.");
					return false;
				}
			}
			rs.close();
			preparedStatement.close();
			connection.close();
		} catch (Exception e) {
			System.out.println("validateOwnership(): " + e.toString());
		}
		return false;
	}

}
