package nFileHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import javax.swing.JOptionPane;

import com.dropbox.core.*;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import nDatabase.DBAccess;
import nUtillities.AESCipher;
import nUtillities.Logger;

/**
 * Servlet implementation class Upload
 */
@WebServlet("/Upload")
public class Upload extends HttpServlet {
	private static Connection connection;
	private static PreparedStatement preparedStatement;
	private static final long serialVersionUID = 1L;
	static final int BUFFER_SIZE = 524288000;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Upload Servlet Begin");
		response.setContentType("text/html;");
		PrintWriter out = response.getWriter();
		//

		DbxRequestConfig config = new DbxRequestConfig("FreshDrive", Locale.getDefault().toString());
		//
		// // access token for the dropbox account. may need to encrypt this
		String accessToken = "-TcOHePlr9AAAAAAAAAACMWGsYvDXPTDcThy6nM8r0hwG-Mz5cEqtDxcDygkg9i3";
		//
		System.out.println("Connect to dropbox");
		DbxClient client;
		client = new DbxClient(config, accessToken);

		//
		// get the file path from the property sent from the client side
		String filePath = request.getHeader("filePath");
		// get the filelength from the client side also
		Long fileLength = Long.parseLong(request.getHeader("fileLength"));
		String username = request.getHeader("username");
		// String ownerId = request.getHeader("ownerID");
		String createdOn = request.getHeader("createdOn");
		String expireDate = request.getHeader("expiryDate");
		String fileName = request.getHeader("fileName");
		File inputFile = new File(filePath);
		System.out.println("File Path: " + filePath);
		boolean fileExist;
		try {
			fileExist = checkIfFileExist(fileName,"1"); //INSERT OWNER ID
			if (fileExist) {
				// get the input from the client's output stream
				ServletInputStream fileInputStream = request.getInputStream();
				//
				//
				System.out.println("Receiving data...");

				// Try uploading to dropbox
				System.out.println("Try uploading");
				// System.out.println(inputStream.available());
				try {
					boolean dbSuccess = addFileToDb(fileName, filePath, fileLength, createdOn, "1", expireDate);	//INSERT OWNER ID
					//if successfully added to database then upload file to dropbox
					if(dbSuccess){
					// writing the file into the dropbox
					DbxEntry.File uploadedFile = client.uploadFile("/" + username + "/" + inputFile.getName(),
							DbxWriteMode.add(), fileLength, fileInputStream);
					System.out.println("Uploaded: " + uploadedFile.toString());
					response.setContentType("text/html");
					out.println("File Uploaded");
					// client.delete("/test (2).txt");

					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					out.println("File Failed");
					e.printStackTrace();
				}
			}else{
				response.setContentType("text/html");
				out.println("File already exist");
			}
				
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	private boolean checkIfFileExist(String fileName, String owner_id) throws Exception {
		// TODO Auto-generated method stub
		// true: yes, false: no
		int count = 0;
		ResultSet rset = null;
		try {

			connection = DBAccess.getInstance().openDB();
			// Get password for selected user account based on given username
			preparedStatement = connection.prepareStatement("SELECT Count(file_id) from files WHERE file_name=? AND file_ownerID =?");
			preparedStatement.setString(1, fileName);
			preparedStatement.setString(2, owner_id);
			rset = preparedStatement.executeQuery();
			if (rset.next()) {
				count = rset.getInt(1);
				System.out.println("COUNTER: "+count);
			}
		} finally {
			if (rset != null) {
				try {
					rset.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
		System.out.println("COUNTER: "+count);
		return (count == 0);
	}

	public boolean addFileToDb(String fileName, String filePath, long fileLength, String createdOn, String owner_id,
			String expireOn) {
		try {
			Date expireDate;
			if (expireOn.equals("")) {
				expireDate = null;
			} else {
				expireDate = Date.valueOf(expireOn);
			}
			String[] encryptedFilePath = AESCipher.EncryptString(filePath);
			connection = DBAccess.getInstance().openDB();
			// Get password for selected user account based on given username
			preparedStatement = connection.prepareStatement(
					"INSERT INTO files (file_name, file_path, file_size, file_createdOn, file_ownerID, file_expireOn, file_salt, file_iv) "
							+ "VALUES (?,?,?,?,?,?,?,?)");
			preparedStatement.setString(1, fileName);
			preparedStatement.setString(2, encryptedFilePath[0]);
			preparedStatement.setString(3, Long.toString(fileLength));
			preparedStatement.setString(4, createdOn);
			preparedStatement.setString(5, owner_id);
			preparedStatement.setDate(6, expireDate);
			preparedStatement.setString(7, encryptedFilePath[1]);
			preparedStatement.setString(8, encryptedFilePath[2]);
			preparedStatement.executeUpdate();
			DBAccess.getInstance().closeDB();
			return true;
		} catch (SQLException e) {
			Logger.getInstance().PrintError("openDB() ", e.toString());
		} catch (Exception e) {
			Logger.getInstance().PrintError("openDB() ", e.toString());
		}
		return false;
	}

}
