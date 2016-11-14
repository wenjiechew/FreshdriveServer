package nFileHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JFileChooser;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxRequestConfig;

import nConstants.DropboxSettings;
import nDatabase.DBAccess;
import nUtillities.AESCipher;
import nUtillities.Logger;

/**
 * Servlet implementation class Download
 */
@WebServlet("/Download")
public class Download extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection connection;
	private static PreparedStatement preparedStatement;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Download() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		System.out.println("download servlet called");
		response.setContentType("text/html");
				
		int fileID = Integer.parseInt(request.getParameter("fileID"));
		String fileName = "";
		
	     
		try{
			connection = DBAccess.getInstance().openDB();
			// Get password for selected user account based on given username
			preparedStatement = connection.prepareStatement("SELECT file_path, file_salt, file_iv, file_name FROM files WHERE file_ID = ?");
			preparedStatement.setInt(1, fileID);
			ResultSet rset = preparedStatement.executeQuery();
			byte[] encryptedPath = null;
			byte[] fileIv = null;
			byte[] fileSalt = null;
			while(rset.next()){
				fileName = rset.getString("file_name");
				encryptedPath = rset.getBytes("file_path");
				fileIv = rset.getBytes("file_iv");
				fileSalt = rset.getBytes("file_salt");
				System.out.println("Encrypted path: "+encryptedPath);
				System.out.println("Encrypted IV: "+fileIv);
				System.out.println("Encrypted Salt: "+fileSalt);
			}
			
			String decryptedString = AESCipher.DecryptString(encryptedPath, fileIv, fileSalt);
			System.out.println("decrypted string: "+ decryptedString);
			
			DbxRequestConfig config = new DbxRequestConfig("FreshDrive", Locale.getDefault().toString());
			//
			// // access token for the dropbox account. may need to encrypt this
			String accessToken = "-TcOHePlr9AAAAAAAAAACMWGsYvDXPTDcThy6nM8r0hwG-Mz5cEqtDxcDygkg9i3";
			
			//
			System.out.println("Connect to dropbox");
			DbxClient client;
			client = new DbxClient(config, accessToken);
//			client.delete("/test/clone.txt");
			String home = System.getProperty("user.home");
//			File file = new File(home+"/Downloads/" + fileName);
			FileOutputStream outputStream = new FileOutputStream(home+"/Downloads/" + fileName);
	        try {
	            DbxEntry.File downloadedFile = client.getFile(decryptedString, null,
	                outputStream);
	            System.out.println("Metadata: " + downloadedFile.toString());
	        } finally {
	            outputStream.close();
	        }
			
			out.println("File has been downloaded");
		}catch (SQLException e) {
			Logger.getInstance().PrintError("download sql() ", e.toString());
			out.println("Download Fail");
		} catch (Exception e) {
			Logger.getInstance().PrintError("download exception() ", e.toString());
			out.println("Download Fail");
		}
		
		
	}

}
