package nUtillities;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nDatabase.DBAccess;

/**
 * Servlet implementation class SharingList
 */
@WebServlet("/SharingList")
public class SharingList extends HttpServlet {
	private static Connection connection;
	private static PreparedStatement preparedStatement;
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SharingList() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Logger.getInstance().PrintInfo("User Response POST === " + request.getParameter("fileID"));
		
		int fileID = Integer.parseInt(request.getParameter("fileID"));
		
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        System.out.println("doPost(): " + request.getParameter("fileID"));
        
        if (fileID != 0){
    		List<Integer> sharedUsers = new ArrayList<Integer>();
    		sharedUsers = getSharingUsers(fileID);
    		List<String> sharedUsernames = new ArrayList<String>();

	       	if (sharedUsers.size() != 0){
				for (int i = 0; i < sharedUsers.size(); i++){
					sharedUsernames.add(getSharedUsernames(sharedUsers.get(i)));
				}
				out.print(sharedUsernames);
	       	}
	       	else {
		       	out.print("Unshared");
	       	}
 	    }
 	    else
        {
	       	out.print("File");
	    }
    }
	
	/**
	 * Retrieve the username of a user
	 * @param userID
	 * @return username
	 */
	public static String getSharedUsernames(int userID){
		String username = null;
		try {
			connection = DBAccess.getInstance().openDB();
			preparedStatement = connection.prepareStatement("SELECT username FROM "
					+ "users WHERE user_ID=?");
				
			preparedStatement.setInt(1, userID);
			ResultSet rs = preparedStatement.executeQuery();
			
			if(rs.next()){
				username = rs.getString("username");
				System.out.println("getSharedUsernames(): Added " + username);
				Logger.getInstance().PrintInfo("getSharedUsernames(): Added " + username);
			}
			
			rs.close();
			preparedStatement.close();
			connection.close();
		} catch (Exception e) {
			System.out.println("getSharedUsernames(): " + e.toString());
			Logger.getInstance().PrintError("getSharedUsernames() ", e.toString());
		}
		
		return username;
	}

	/**
	 * Retrieve a list of users who has access to specific file
	 * @param fileID
	 * @return list of userIDs with access to file or null (unshared file)
	 */
	public static List<Integer> getSharingUsers(int fileID){
		List<Integer> sharedUsers = new ArrayList<Integer>();
		try {
			connection = DBAccess.getInstance().openDB();
			preparedStatement = connection.prepareStatement("SELECT p.permission_sharedToUserID FROM "
					+ "permissions p JOIN files f ON p.permission_fileID = f.file_ID WHERE p.permission_fileID=? "
					+ "AND p.permission_sharedToUserID != f.file_ownerID");
			
			preparedStatement.setInt(1, fileID);
			ResultSet rs = preparedStatement.executeQuery();
			
			if(rs.next()){
				do {
					sharedUsers.add(rs.getInt("permission_sharedToUserID"));
				} while (rs.next());
				System.out.println("getSharingUsers(): Shared users = " + sharedUsers.toString());
				Logger.getInstance().PrintInfo("getSharingUsers(): Shared users = " + sharedUsers.toString());
			}
			else
			{
				System.out.println("getSharingUsers(): Unshared.");
				Logger.getInstance().PrintInfo("getSharingUsers(): Unshared.");
				sharedUsers = null;
			}
			
			rs.close();
			preparedStatement.close();
			connection.close();
		} catch (Exception e) {
			System.out.println("getSharingUsers(): " + e.toString());
			Logger.getInstance().PrintError("getSharingUsers() ", e.toString());
		}
		return sharedUsers;
	}
}
