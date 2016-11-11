package nFileHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.acl.Owner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import nDatabase.DBAccess;
import nUtillities.Logger;

/**
 * Servlet implementation class Retrieve
 */
@WebServlet("/Retrieve")
public class Retrieve extends HttpServlet {
	private static Connection connection;
	private static PreparedStatement preparedStatement;
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Retrieve() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// Gson gson = new Gson();
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		String userID = request.getParameter("userID");
		System.out.println("Retrieve Servlet");
		response.setContentType("text/html");
		JsonArray array = new JsonArray();
		JsonObject obj = new JsonObject();
		try {
			ResultSet rset;
			
			List<String> fileArray = new ArrayList<String>();
			connection = DBAccess.getInstance().openDB();
			// Get password for selected user account based on given username
			preparedStatement = connection.prepareStatement("SELECT file_name, file_ID from files WHERE file_ID IN (SELECT permission_fileID from permissions where permission_sharedToUserID = '"+userID+"')");
//			preparedStatement.setString(1, userID); 
			rset = preparedStatement.executeQuery();
			while (rset.next()) {
				String  fileName_json=rset.getString("file_name");
			    String FileId_json=rset.getString("file_ID");
			    JsonObject jObj = new JsonObject();
			    jObj.addProperty("fileId", FileId_json);
			    jObj.addProperty("fileName", fileName_json);
			    array.add(jObj);
			}
			
			obj.add("fileNames", array);
//			System.out.println("json array pos 2"+array.get(2).toString());
//			System.out.println("GSON to JSON: "+ gson.toJson(array));
//			System.out.println("GSON to JSON: "+ gson.toJson(obj));
			
		} catch (SQLException e) {
			Logger.getInstance().PrintError("openDB() ", e.toString());
		} catch (Exception e) {
			Logger.getInstance().PrintError("openDB() ", e.toString());
		}
		out.print(gson.toJson(obj));

	}

}
