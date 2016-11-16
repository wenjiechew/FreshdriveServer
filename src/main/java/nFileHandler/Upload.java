package nFileHandler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dropbox.core.*;

import nConstants.Constants;
import nConstants.DropboxSettings;
import nDatabase.DBAccess;
import nLogin.Validate;
import nObjectModel.FileModel;
import nUtillities.AESCipher;
import nUtillities.Log;


/**
 * This Serlvet only allow POST, from the client, Which first validates the right user before executing the main task. @return unverified-token 
 * Upon token is validated. file will be checked if it's existed in the applicaiton @return file Exists
 * else file will be uploaded into the dropbox then updates the file table in the database, following by updating the
 * permission table.
 * Servlet implementation class Upload
 */
@WebServlet("/Upload")
public class Upload extends HttpServlet {
	private static Connection connection;
	private static PreparedStatement preparedStatement;
	private static ResultSet rs;
	private static Log Log;
	
	private static final long serialVersionUID = 1L;	
	
	private static DbxRequestConfig config = new DbxRequestConfig("FreshDrive", Locale.getDefault().toString());
	private static DbxClient client = new DbxClient(config, DropboxSettings.getInstance().getAccessToken() );

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;");
		PrintWriter out = response.getWriter();
		
		//New File()
		File inputFile = new File( request.getHeader("filePath") );
		
		//New FileModel and Store Request Data
		FileModel fileModel = new FileModel();
		
		//Encrpting the Path to get the IV, Salt and the encrypted Path
		byte[][] encryptedFilePath = AESCipher.EncryptString( request.getHeader("filePath") );
		
		fileModel.setUserName( request.getHeader("username") );
		fileModel.setFileName( request.getHeader("fileName") );
		fileModel.setPathByte( encryptedFilePath[0] );
		fileModel.setIvByte( encryptedFilePath[1] );
		fileModel.setSaltByte( encryptedFilePath[2] );
		fileModel.setCreatedOn( request.getHeader("createdOn") );
		fileModel.setOwnderID( request.getHeader("ownerID") );
		fileModel.setFileLength( request.getHeader("fileLength"));
		if (request.getHeader("expiryDate") != "" ){
			fileModel.setExpiredDate( Date.valueOf( request.getHeader("expiryDate") ) );
		}
		
		//user-token AND username Validation
		if (Validate.verifyToken( request.getHeader("usertoken"), fileModel.getUserName() ) == 1){
			//Check File Exist in the Database/FileServer ( true = Exist // false = not exist )
			//if Not Exit Do Upload
			try{
				connection = DBAccess.getInstance().openDB();
				
				if(checkIfFileExisted(fileModel)){
					//Get the Remaing bytes of the uploading files
					ServletInputStream fileInputStream = request.getInputStream();
					
					//Do upload to Dropbox
					DbxEntry.File uploadedFile = client.uploadFile("/" + fileModel.getUserName() + "/" + inputFile.getName(),
													DbxWriteMode.add(), Long.parseLong( fileModel.getFileLength() ), fileInputStream);
					//Update File Table in Database
					//Check if update done isDone = true
					if(addFileToDatabase(fileModel)){
						
						//Update Permission Table
						insertPermissiontoDatabase(fileModel);
						
					}else{
						client.delete( AESCipher.DecryptString(fileModel.getPathByte(), fileModel.getIvByte(), fileModel.getSaltByte()));
					}
					
					out.println("File Uploaded");
					Log.log("Upload Process| " + request.getHeader("username") + "uploaded "+ request.getHeader("filename"));
				}else{
					out.println("File already exist");
				}
				
				rs.close();
				preparedStatement.close();
				connection.close();	
			}catch (SQLException e) {
				} catch (Exception e) {
				}			
		}else{
			//TODO Change Return ERROR
			out.println("unverified-token");
		}			

	}
	
	/**
	 * This Function check if stated 'fileName' owned by the owner_id is already in the database
	 * @param fileName
	 * @param owner_id
	 * @return Boolean True / False (Count >= 1 is False || Count == 0 is True )
	 * @throws SQLException
	 */
	private boolean checkIfFileExisted(FileModel fileModel) throws SQLException {
		int count = 0;
		preparedStatement = connection.prepareStatement( Constants.SELECT_FileOwnerID );
		preparedStatement.setString(1, fileModel.getFileName());
		preparedStatement.setString(2, fileModel.getOwnderID());
		rs = preparedStatement.executeQuery();
		if(rs.next()){
			count = rs.getInt(1);
		}
		rs.close();
		return (count == 0);		
	}
	
	/**
	 * This function will update the 'file' Table in the database with all the required column values.
	 * @param fileModel
	 * @return boolean (true= the function has success, false= not done)
	 * @throws SQLException
	 */
	public boolean addFileToDatabase (FileModel fileModel) throws SQLException {
		int isDone = 0;
		preparedStatement = connection.prepareStatement( Constants.INSERT_FileTable );
		preparedStatement.setString(1, fileModel.getFileName());
		preparedStatement.setBytes(2, fileModel.getPathByte());
		preparedStatement.setString(3, fileModel.getFileLength());
		preparedStatement.setString(4, fileModel.getCreatedOn());
		preparedStatement.setString(5, fileModel.getOwnderID());
		preparedStatement.setDate(6, fileModel.getExpiredDate());
		preparedStatement.setBytes(7, fileModel.getSaltByte());
		preparedStatement.setBytes(8, fileModel.getIvByte());
		isDone = preparedStatement.executeUpdate();
		if (isDone > 0){
			return true;
		}
		else{
			return false;
		}
				
	}
	
	/**
	 * This function insert into the 'Permission' Table in the Database
	 * making sure that the file_ID and file_name is the current User 
	 * @param fileModel
	 * @throws SQLException
	 */
	public void insertPermissiontoDatabase (FileModel fileModel) throws SQLException {
		
		preparedStatement = connection.prepareStatement( Constants.INSERT_FilePermission );
		preparedStatement.setString(1, fileModel.getFileName());
		preparedStatement.setString(2, fileModel.getOwnderID());
		preparedStatement.setString(3, fileModel.getOwnderID());
		preparedStatement.executeUpdate();
	}
}