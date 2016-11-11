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
		Logger.getInstance().PrintInfo("User Response POST === " + request.getParameter("users")+ ", " + request.getParameter("fileID"));
		
		int fileID = Integer.parseInt(request.getParameter("fileID"));
		
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        System.out.println("doPost(): " + request.getParameter("users"));
        
        String userString = request.getParameter("users").substring(1, request.getParameter("users").length()-1);
        String[] userArray = userString.split(", ");
        List<String> errorUserList = new ArrayList<String>();
        List<Integer> userIDs = new ArrayList<Integer>();
        
        if (fileID != 0){
	        if (validateFile(fileID)){
	        	
	        	for (int i = 0; i < userArray.length; i++){
	            	int userValidity = validateUser(userArray[i]);
	            	
	                if (userValidity != 0) {
	                	System.out.println(userArray[i] + " is validated.");
	                	userIDs.add(userValidity);
	                }
	                else
	                {
	                	System.out.println(userArray[i] + " is not validated.");
	                	errorUserList.add(userArray[i]);
	                }
	            }
	        	shareFile(userIDs, fileID);
	        	if (errorUserList.size() != 0){
	            	out.print(errorUserList);
	        	}
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
					//System.out.println("shareFile(): " + fileID + " shared to " + users[i]);
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
	
	public static int validateUser(String user){
		int userID = 0;
		
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
				userID = rs.getInt("user_ID");
			}
			else
			{
				System.out.println("validateUser(): " + user + " is not validated");
				Logger.getInstance().PrintInfo("validateUser(): " + user + " is not validated");
				userID = 0;
			}
			
			DBAccess.getInstance().closeDB();
		} catch (Exception e) {
			System.out.println("validateUser(): " + e.toString());
			Logger.getInstance().PrintError("validateUser() ", e.toString());
		}
		return userID;
	}
	
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
	
	public static int validateUserPermission(int userID, int fileID){
		try {
			connection = DBAccess.getInstance().openDB();
			preparedStatement = connection.prepareStatement("SELECT * FROM "
					+ "permissions WHERE permission_fileID=? AND permission_sharedToUserID=?");
			
			preparedStatement.setInt(1, fileID);
			preparedStatement.setInt(2, userID);
			ResultSet rs = preparedStatement.executeQuery();
			
			if(rs.next()){
				//System.out.println("validateUserPermission(): " + fileID + " is shared to " + userID);
				Logger.getInstance().PrintInfo("validateUserPermission()", fileID + " is shared to " + userID);
				return 1;
			}
			else {
				//System.out.println("validateUserPermission(): " + fileID + " is not shared to " + userID);
				Logger.getInstance().PrintInfo("validateUserPermission()", fileID + " is not shared to " + userID);
			}
			
			DBAccess.getInstance().closeDB();
		} catch (Exception e) {
			Logger.getInstance().PrintError("validateUserPermission() ", e.toString());
		}
		return 0;
	}

}
