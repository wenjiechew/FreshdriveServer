package nFileHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxRequestConfig;

import nConstants.Constants;
import nConstants.DropboxSettings;
import nDatabase.DBAccess;
import nLogin.Validate;
import nObjectModel.FileModel;
import nUtillities.AESCipher;
import nUtillities.Log;


/**
 * This class file, checks the user validity, if good it will download the path file the related fileID POST from the 'file' Table in the Database
 * and decrpyt the path stored in Database, to download form dropbox
 * Servlet implementation class Download
 */
@WebServlet("/Download")
public class Download extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection connection;
	private static PreparedStatement preparedStatement;
	private static ResultSet rs;
	private static PrintWriter out;
	private static File file;
	private static Log Log = new Log();

	private static DbxRequestConfig config = new DbxRequestConfig("FreshDrive", Locale.getDefault().toString());
	private static DbxClient client = new DbxClient(config, DropboxSettings.getInstance().getAccessToken() );
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		out = response.getWriter();		

		//Check User Validity
		if(Validate.verifyToken(request.getParameter("usertoken")  , request.getParameter("username")) == 1 ){
			
			FileModel fileModel = new FileModel();			
			fileModel.setFileID( Integer.parseInt( request.getParameter("fileID") ) );
			try{
				
				connection = DBAccess.getInstance().openDB();
				//Get File Path to download from database to download from Dropbox
				if( getFilePath(fileModel) ){
					
					//Decrypting of Path
					fileModel.setFilePath( AESCipher.DecryptString( fileModel.getPathByte() , fileModel.getIvByte(), fileModel.getSaltByte() ));
					
					//Set FileOutputStream to setup temporary location to store the file 
		 			FileOutputStream outputStream = new FileOutputStream( Constants.getFilePathLocation() + fileModel.getFileName() );
		 			
		 			//Download physical File from File Server (Dropbox)
		 			DbxEntry.File downloadedFile = client.getFile(fileModel.getFilePath(), null, outputStream);
		 			outputStream.close();
		 			
		 			//Get File from the temporary location
		 			file = new File(Constants.getFilePathLocation() + fileModel.getFileName() );
		 			
		 			//parse file into bytes 
		            byte[] data = Files.readAllBytes( Paths.get(file.getAbsolutePath()) );
		            //send the bytes to the client
		            out.println(Arrays.toString(data));
		            
		            Log.log("Download Process| "+ request.getParameter("username") + " downloaded " + downloadedFile.toString());
				}
				
				rs.close();
				preparedStatement.close();
				connection.close();
				
			}catch (SQLException e) {
				e.printStackTrace();
				out.println("Download Fail");
			}catch (Exception e) {
				e.printStackTrace();
				out.println("Download Fail");
			}finally {
				//This is to delete the physical file in the temp folder
				if(file.exists()){
	            	file.delete();
 	            }

			}
		}else{
			System.out.println("token fail");
			out.println("unverified-token");
		}
	}
	
	/**
	 * This Function is the to get file Details from the File Table in the Database
	 * @param fileModel
	 * @return boolean (true that the Select isDone, false that is not done)
	 * @throws SQLException
	 */
	private boolean getFilePath(FileModel fileModel) throws SQLException{
		int isDone = 0;
		preparedStatement = connection.prepareStatement( Constants.SELECT_FileID );
		preparedStatement.setInt(1, fileModel.getFileID());
		rs = preparedStatement.executeQuery();
		if(rs.next()){
			fileModel.setFileName( rs.getString("file_name") );
			fileModel.setPathByte( rs.getBytes("file_path") );
			fileModel.setIvByte( rs.getBytes("file_iv") );
			fileModel.setSaltByte( rs.getBytes("file_salt") );
			isDone = 1;
		}else{
			Logger.getInstance().PrintInfo("No Such File in Database");
			out.println("No Such File in Database");
			isDone = 1;
		}
		if(isDone == 1){
			return true;
		}else{
			return false;
		}
	}

}
