package nFileHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import nConstants.Constants;
import nDatabase.DBAccess;
import nLogin.Validate;

/**
 * This servlet allows POST, 
 * to retrieve all the FileIDs and FileName that are permitted (uploaded or shared with) to the user ID 
 * 
 * Servlet implementation class Retrieve
 */
@WebServlet("/Retrieve")
public class Retrieve extends HttpServlet {
	private static Connection connection;
	private static PreparedStatement preparedStatement;
	private static ResultSet rset;
	private static final long serialVersionUID = 1L;
	
	private Gson gson = new Gson();


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		if(Validate.verifyToken(request.getParameter("usertoken"), request.getParameter("username")) == 1){
		
			JsonArray array = new JsonArray();
			JsonObject obj = new JsonObject();
			try {			
				connection = DBAccess.getInstance().openDB();
				
				// Get all the file_IDs and file Names permitted to a given user ID
				preparedStatement = connection.prepareStatement(Constants.SELECT_FileRelatedtoID);
				preparedStatement.setString(1, request.getParameter("userID")); 
				rset = preparedStatement.executeQuery();
				
				if(rset.next()){
					do{
						JsonObject jObj = new JsonObject();
					    jObj.addProperty("fileId", rset.getString("file_ID") );
					    jObj.addProperty("fileName", rset.getString("file_name") );
					    array.add(jObj);
					} while (rset.next());
				}
				
				obj.add("fileNames", array);
				
				rset.close();
				preparedStatement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			out.print(gson.toJson(obj));
		}else{
			out.println("unverified-token");
		}
	}

}
