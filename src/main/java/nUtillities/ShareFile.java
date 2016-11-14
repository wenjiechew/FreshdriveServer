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
 * Servlet implementation class Login
 */
@WebServlet("/ShareFile")
public class ShareFile extends HttpServlet {
	private static Connection connection;
	private static PreparedStatement preparedStatement;
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logger.getInstance().PrintInfo("User Response POST === " + request.getParameter("users")+ ", " + request.getParameter("fileID")+ ", " + request.getParameter("action"));
		
		int fileID = Integer.parseInt(request.getParameter("fileID"));
		String action = request.getParameter("action");
		String users = request.getParameter("users");
		
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        System.out.println("doPost(): " + request.getParameter("users")+ ", " + request.getParameter("fileID")+ ", " + request.getParameter("action"));
        
        String[] userArray = users.split(";");
        List<String> errorUserList = new ArrayList<String>();
        List<Integer> userIDs = new ArrayList<Integer>();
        List<String> userNames = new ArrayList<String>();
        
        
        //Sharing of file to users
        if (action.equals("add")){
        	//TODO: When will fileID be 0?
	        if (fileID != 0){
	        	//Validate if file exists in database
		        if (validateFile(fileID)){
		        	for (int i = 0; i < userArray.length; i++){
		        		//Validate if username or email is an registered user,
		        		//if user is registered, add to a List, userIDs, for sharing
		        		//else add to List, errorUserList, to be returned to user to notify that user does not exist.
		            	String[] userValidity = validateUser(userArray[i]);
		                if (userValidity != null) {
		                	System.out.println(userArray[i] + " is validated.");
		                	int currentUserID = Integer.parseInt(userValidity[0]);
		                	if (validateUserPermission(currentUserID, fileID) == 0)
		                	{
			                	userIDs.add(currentUserID);
			                	userNames.add(userValidity[1]);
		                	}
		                }
		                else
		                {
		                	System.out.println(userArray[i] + " is not validated.");
		                	errorUserList.add(userArray[i]);
		                }
		            }
		        	//Share to all users in userID List
		        	shareFile(userIDs, fileID);
		        	System.out.println(errorUserList + ",accepted="+userNames);
		        	out.print(errorUserList + ",accepted="+userNames);
		        }
		        else
		        {
		        	//For error message printing
		        	out.print("File");
		        }
	        }
	        else
	        {
	        	//For error message printing
	        	out.print("File");
	        }
        } 
        //Remove shared user's access to files
        else if (action.equals("remove"))
        {
        	String removedUser = null;
        	int removedUserID = 0;
        	
        	//TODO: When will fileID be 0?
	        if (fileID != 0){
	        	//Validate if file exists in database
		        if (validateFile(fileID)){
		        	//Validate if user is registered
	            	String[] userValidity = validateUser(users);
		        	if (userValidity != null) {
	                	System.out.println(users + " is validated.");
	                	removedUserID = Integer.parseInt(userValidity[0]);
	                	removedUser = userValidity[1];
	                }
	                else
	                {
	                	System.out.println(users + " does not exist.");
	                	out.print("User");
	                }
		        	out.print(removeUserPermission(removedUserID, fileID));
		        }
		        else
		        {
		        	out.print("File");
		        }
	        }
	        else
	        {
	        	out.print("File");
	        }
        }
	}
	
	/**
	 * Updates database to grant permission to specified users for a file
	 * @param users
	 * @param fileID
	 * @return
	 */
	public static int shareFile(List<Integer> users, int fileID){
		try {
			connection = DBAccess.getInstance().openDB();
			
			for (int i = 0; i < users.size(); i++){
				preparedStatement = connection.prepareStatement("INSERT INTO permissions (permission_fileID, permission_sharedToUserID) "
						+ "VALUES (?,?)");
				
				preparedStatement.setInt(1, fileID);
				preparedStatement.setInt(2, users.get(i));
				int rs = preparedStatement.executeUpdate();
				
				if (rs == 1){
					Logger.getInstance().PrintInfo("shareFile()", fileID + " shared to " + users.get(i));
				}
				else
				{
					//TODO: Return update error to user
				}
			}
			
			DBAccess.getInstance().closeDB();
			return 1;
		} catch (Exception e) {
			Logger.getInstance().PrintError("shareFile() ", e.toString());
		}
		return 0;
	}
	
	/**
	 * Validates if specified user is an registered user
	 * @param user
	 * @return an array [userID, username] if validated else null
	 */
	public static String[] validateUser(String user){
		String[] userInfo = new String[2];
		
		try {
			connection = DBAccess.getInstance().openDB();
			preparedStatement = connection.prepareStatement("SELECT * FROM "
					+ "users WHERE user_email=? OR username=?");
			
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, user);
			ResultSet rs = preparedStatement.executeQuery();
			
			if(rs.next()){
				System.out.println("validateUser(): " + user + " is validated");
				Logger.getInstance().PrintInfo("validateUser(): " + user + " is validated");
				userInfo[0] = rs.getString("user_ID");
				userInfo[1] = rs.getString("username");
			}
			else
			{
				System.out.println("validateUser(): " + user + " is not validated");
				Logger.getInstance().PrintInfo("validateUser(): " + user + " is not validated");
				userInfo = null;
			}
			
			DBAccess.getInstance().closeDB();
		} catch (Exception e) {
			System.out.println("validateUser(): " + e.toString());
			Logger.getInstance().PrintError("validateUser() ", e.toString());
		}
		return userInfo;
	}
	
	/**
	 * Validates if selected file exists in database
	 * @param fileID
	 * @return true (exists) or false (doesn't exist)
	 */
	public static boolean validateFile(int fileID){
		boolean valid = false;
		
		try {
			connection = DBAccess.getInstance().openDB();
			preparedStatement = connection.prepareStatement("SELECT * FROM "
					+ "files WHERE file_ID=?");
			
			preparedStatement.setInt(1, fileID);
			ResultSet rs = preparedStatement.executeQuery();
			
			if(rs.next()){
				System.out.println("validateFile(): File is validated");
				Logger.getInstance().PrintInfo("validateFile(): File is validated");
				valid = true;
			}
			else
			{
				System.out.println("validateFile(): File is not validated");
				Logger.getInstance().PrintInfo("validateFile(): File is not validated");
				valid = false;
			}
			
			DBAccess.getInstance().closeDB();
		} catch (Exception e) {
			System.out.println("validateFile(): " + e.toString());
			Logger.getInstance().PrintError("validateFile() ", e.toString());
		}
		return valid;
	}
	
	/**
	 * Validates if specific user has access to the specific file
	 * @param userID
	 * @param fileID
	 * @return 1 (has access) or 0 (no access)
	 */
	public static int validateUserPermission(int userID, int fileID){
		try {
			connection = DBAccess.getInstance().openDB();
			preparedStatement = connection.prepareStatement("SELECT * FROM "
					+ "permissions WHERE permission_fileID=? AND permission_sharedToUserID=?");
			
			preparedStatement.setInt(1, fileID);
			preparedStatement.setInt(2, userID);
			ResultSet rs = preparedStatement.executeQuery();
			
			if(rs.next()){
				Logger.getInstance().PrintInfo("validateUserPermission()", fileID + " is shared to " + userID);
				return 1;
			}
			else {
				Logger.getInstance().PrintInfo("validateUserPermission()", fileID + " is not shared to " + userID);
			}
			
			DBAccess.getInstance().closeDB();
		} catch (Exception e) {
			Logger.getInstance().PrintError("validateUserPermission() ", e.toString());
		}
		return 0;
	}

	/**
	 * Update database to remove the access of a file to a specific user
	 * @param userID
	 * @param fileID
	 * @return 1 (removed) or 0 (permission not found)
	 */
	public static int removeUserPermission(int userID, int fileID){
		try {
			connection = DBAccess.getInstance().openDB();
			preparedStatement = connection.prepareStatement("DELETE FROM "
					+ "permissions WHERE permission_fileID=? AND permission_sharedToUserID=?");
			
			preparedStatement.setInt(1, fileID);
			preparedStatement.setInt(2, userID);
			int rs = preparedStatement.executeUpdate();
			
			if(rs == 1){
				Logger.getInstance().PrintInfo("removeUserPermission()", fileID + " is no longer shared to " + userID);
				return 1;
			}
			else {
				Logger.getInstance().PrintInfo("removeUserPermission()", fileID + "'s access to " + userID + " is not found.");
			}
			
			DBAccess.getInstance().closeDB();
		} catch (Exception e) {
			Logger.getInstance().PrintError("removeUserPermission() ", e.toString());
		}
		return 0;
	}
}
