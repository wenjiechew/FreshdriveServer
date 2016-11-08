package nFileHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Locale;
import java.nio.file.*;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.tomcat.util.http.fileupload.IOUtils;

import com.dropbox.core.*;

@WebServlet("/Upload")
public class Upload extends HttpServlet{
	private static final long serialVersionUID = 1L;
	static final int BUFFER_SIZE = 524288000;
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("Upload Servlet Begin");
		response.setContentType("text/html;");
		PrintWriter out = response.getWriter();
//		
		//APP KEY AND SECRET FROM DROPBOX
		String APP_KEY = "hlxjjkypee9pfx6";
		String APP_SECRET = "a9akptnjcley8jk";

		DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
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
		
		
		File inputFile = new File(filePath);
		System.out.println("File Path: " + filePath);
		
		//get the input from the client's output stream
        ServletInputStream fileInputStream = request.getInputStream();
		InputStream inputStream = request.getInputStream();
		
		
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
        
        System.out.println("Receiving data...");
        
        FileInputStream inputS = new FileInputStream(inputFile);
        System.out.println("inputS available: " + inputS.available());
		//Try uploading to dropbox
		System.out.println("Try uploading");
//		System.out.println(inputStream.available());
		try {
			//writing the file into the dropbox
			DbxEntry.File uploadedFile = client.uploadFile("/" + inputFile.getName(), DbxWriteMode.add(),
					fileLength, fileInputStream);
			System.out.println("Uploaded: " + uploadedFile.toString());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
}
