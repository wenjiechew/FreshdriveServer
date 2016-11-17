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
import nLogin.Validate;

/**
 * Servlet implementation class Login
 */
@WebServlet("/ShareFile")
public class ShareFile extends HttpServlet {
	private static Connection connection;
	private static PreparedStatement preparedStatement;
	private static final long serialVersionUID = 1L;
	private static Log Log = new Log();
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int fileID = Integer.parseInt(request.getParameter("fileID"));
		String action = request.getParameter("action");
		String token = request.getParameter("token");
		//User(s) that will be added/removed from sharing
		String users = request.getParameter("users");
		//User (file owner) that is performing the action
		String username = request.getParameter("username");
		
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        if(Validate.verifyToken(token, username)==1){
	        String[] userArray = users.split(";");
	        List<String> errorUserList = new ArrayList<String>();
	        List<Integer> userIDs = new ArrayList<Integer>();
	        List<String> userNames = new ArrayList<String>();
	        
	        //Sharing of file to users
	        if (action.equals("add")){
		        if (fileID != 0){
		        	//Validate if file exists in database
			        if (validateFile(fileID)){
			        	for (int i = 0; i < userArray.length; i++){
			        		//Validate if username or email is an registered user,
			        		//if user is registered, add to a List, userIDs, for sharing
			        		//else add to List, errorUserList, to be returned to user to notify that user does not exist.
			            	String[] userValidity = validateUser(userArray[i]);
			                if (userValidity != null) {
			                	int currentUserID = Integer.parseInt(userValidity[0]);
			                	if (validateUserPermission(currentUserID, fileID) == 0)
			                	{
				                	userIDs.add(currentUserID);
				                	userNames.add(userValidity[1]);
				                	Log.log("ShareFile Process| "+ username + " is now sharing fileID:" + fileID + " with " + userValidity[1]);
			                	}
			                }
			                else
			                {
			                	errorUserList.add(userArray[i]);
			                }
			            }
			        	//Share to all users in userID List
			        	shareFile(userIDs, fileID, userNames, errorUserList);
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
	        	int removedUserID = 0;
		        if (fileID != 0){
		        	//Validate if file exists in database
			        if (validateFile(fileID)){
			        	//Validate if user is registered
		            	String[] userValidity = validateUser(users);
			        	if (userValidity != null) {
		                	removedUserID = Integer.parseInt(userValidity[0]);
		                }
		                else
		                {
		                	out.print("User");
		                }
			        	Log.log("ShareFile Process| "+ username + " stopped sharing fileID:" + fileID + " with "+ userValidity[1]);
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
        else{
        	out.print("unverified-token");
        }
	}
	
	/**
	 * Updates database to grant permission to specified users for a file
	 * @param users	list of user(s) ID to whom the file will be shared 
	 * @param fileID	id of the file that will be shared
	 * @param userNames	list of username corresponding to users
	 * @param errorUserList	list of users that are not weren't managed to be shared with (i.e. errored)
	 * @return 1 if at least partially successful, else 0. 
	 */
	public static int shareFile(List<Integer> users, int fileID, List<String> userNames, List<String> errorUserList){
		try {
			connection = DBAccess.getInstance().openDB();
			for (int i = 0; i < users.size(); i++){
				preparedStatement = connection.prepareStatement("INSERT INTO permissions (permission_fileID, permission_sharedToUserID) "
						+ "VALUES (?,?)");
				
				preparedStatement.setInt(1, fileID);
				preparedStatement.setInt(2, users.get(i));
				int rs = preparedStatement.executeUpdate();
				
				if (rs == 1){
					//Working well; do nothing and continue with execution
				}
				else
				{
					errorUserList.add(userNames.get(i));
				}
			}		
			preparedStatement.close();
			connection.close();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
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
				userInfo[0] = rs.getString("user_ID");
				userInfo[1] = rs.getString("username");
			}
			else
			{
				userInfo = null;
			}
			
			rs.close();
			preparedStatement.close();
			connection.close();
			
		} catch (Exception e) {
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
				valid = true;
			}
			else
			{
				valid = false;
			}
			
			rs.close();
			preparedStatement.close();
			connection.close();
			
		} catch (Exception e) {
		}
		return valid;
	}
	
	/**
	 * Validates if specific user has access to the specific file already.
	 * @param userID
	 * @param fileID
	 * @return 1 (already has access) or 0 (no access)
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
				return 1;
			}
			
			rs.close();
			preparedStatement.close();
			connection.close();
			
		} catch (Exception e) {
			
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
				return 1;
			}
			else {
			}
			
			preparedStatement.close();
			connection.close();
		} catch (Exception e) {
		}
		return 0;
	}
}
