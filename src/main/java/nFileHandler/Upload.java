package nFileHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dropbox.core.*;

/**
 * Servlet implementation class Upload
 */
@WebServlet("/Upload")
public class Upload extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static final int BUFFER_SIZE = 524288000;


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Upload Servlet Begin");
		response.setContentType("text/html;");
		PrintWriter out = response.getWriter();
//		

		DbxRequestConfig config = new DbxRequestConfig("FreshDrive", Locale.getDefault().toString());
//
//		// access token for the dropbox account. may need to encrypt this
		String accessToken = "-TcOHePlr9AAAAAAAAAACMWGsYvDXPTDcThy6nM8r0hwG-Mz5cEqtDxcDygkg9i3";
//		
		System.out.println("Connect to dropbox");
		DbxClient client;
		client = new DbxClient(config, accessToken);
		
//
//		get the file path from the property sent from the client side
		String filePath = request.getHeader("filePath");
		//get the filelength from the client side also
		Long fileLength = Long.parseLong(request.getHeader("fileLength"));
		String username = request.getHeader("username");
		
		File inputFile = new File(filePath);
		System.out.println("File Path: " + filePath);
		
		//get the input from the client's output stream
        ServletInputStream fileInputStream = request.getInputStream();
//	
//        
        System.out.println("Receiving data...");
        
		//Try uploading to dropbox
		System.out.println("Try uploading");
//		System.out.println(inputStream.available());
		try {
			//writing the file into the dropbox
			DbxEntry.File uploadedFile = client.uploadFile("/" + username + "/" + inputFile.getName(), DbxWriteMode.add(),
					fileLength, fileInputStream);
			System.out.println("Uploaded: " + uploadedFile.toString());
			response.setContentType("text/html");
			out.println("File Uploaded");
//			client.delete("/test (2).txt");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			out.println("File Failed");
			e.printStackTrace();
		} 
	
	}

}
