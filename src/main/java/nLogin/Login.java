package nLogin;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nConstants.EmailSettings;

import java.security.KeyManagementException;
import java.security.SecureRandom;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Properties;

import nDatabase.DBAccess;
import nObjectModel.Account;
import nUtillities.Log;

/**
 * This servlet services login requests, checking if the requesting user has provided a valid set of credentials
 * when logging into the system.
 * 
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private EmailSettings emailSettings = EmailSettings.getInstance();
	
	private static final long serialVersionUID = 1L;
	private static Connection connection;
	private static PreparedStatement preparedStatement;
	private static Properties mailServerProperties;
	private static Session getMailSession;
	private static MimeMessage generateMailMessage;
	private SecureRandom sr = new SecureRandom();
	private ScheduledExecutorService executorService1;
	private static Log Log = new Log();

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		Account account = new Account();
		account.setUsername(request.getParameter("username"));
		account.setPassword(request.getParameter("password"));

		response.setContentType("text/html");
		
		/*
		 * Validates the provided username and password against the database records
		 * Then validates if that user is already logged into the system (exisiting access token)
		 * If all clear, then clears user to go ahead for 2FA authenticaton after generating an OTP emailed to them 
		 */
		if (Validate.checkUser(account)) {
			if (Validate.isLoggedIn(account) == 0) {
				// Active token existing; someone is currently logged in with
				// the account
				out.println("active-token");
				Log.log("Login Process| "+ account.getUsername() + " is already logged in");
			} else {
				Log.log("Login Process| "+ account.getUsername() + " password matched");
				try {
					account.setEmail(getEmail(account.getUsername()));
					sendEmail(account);
					out.println("to-2FA");
				} catch (Exception ex) {
					out.println("1");
				}
			}
		} else {
			out.println("1");
		}
	}
	/**
	 * Get the email address of the account that is being logged into from database server
	 * 
	 * @param userName
	 * @return email for the given username
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws MessagingException
	 */
	public String getEmail(String userName) {
		String email = "";
		try {
			connection = DBAccess.getInstance().openDB();
			// Get password for selected user account based on given username
			preparedStatement = connection.prepareStatement("SELECT user_email FROM users WHERE username=?");

			preparedStatement.setString(1, userName);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				email = rs.getString("user_email");
			}
			
			rs.close();
			preparedStatement.close();
			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return email;
	}

	/**
	 * Send email containing the OTP password to the account being logged in
	 * @param account	object containing username's field 
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws MessagingException
	 */
	public void sendEmail(Account account) {
		try{
			// Step1 setup Mail Server Properties
			mailServerProperties = System.getProperties();
			mailServerProperties.put("mail.smtp.port", "587");
			mailServerProperties.put("mail.smtp.auth", "true");
			mailServerProperties.put("mail.smtp.starttls.enable", "true");
			
			//Mail Server Properties have been setup successfully
	
			// Step2 Generate OTP for user
			int OTP = randomSixDigitCode(account.getUsername());
			if (OTP == 0) {
				// OTP didn't get generated or stored properly
				return;
			}
			// Creating the email session		
			getMailSession = Session.getDefaultInstance(mailServerProperties, null);
			generateMailMessage = new MimeMessage(getMailSession);
			generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(account.getEmail()));
			generateMailMessage.setSubject("Freshdrive Log in validation");
			String emailBody = "Dear " + account.getUsername()
					+ ",<br><br>Please enter the following code for validating your login: <b>" + OTP
					+ "</b><br><br> Regards, <br>Freshdrive Admin<br><br> <br><br> <i>Please reply to this email if this log in was not authorised by you</i>";
			generateMailMessage.setContent(emailBody, "text/html");
			//Mail session has been created successfully
	
			// Step3 Get Session and Send mail
			Transport transport = getMailSession.getTransport("smtp");
	
			transport.connect("smtp.gmail.com", emailSettings.getEmailAddress() , emailSettings.getEmailPass());
			transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
			transport.close();
			
			Log.log("Login Process| "+account.getUsername()+ " has requested for an OTP to be emailed.");
			
			// Make OTP expire after a specific time limit
			executorService1 = Executors.newScheduledThreadPool(1);
			executorService1.schedule(() -> {
				expireOTP(account.getUsername());
				this.executorService1.shutdownNow();
			}, 180L, TimeUnit.SECONDS);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Schedule an operation to make a generated OTP expire in 3 minutes
	 * @param username to identify user who has just received an OTP
	 */
	private void expireOTP(String username) {
		try {
			// Update database with generated OTP
			connection = DBAccess.getInstance().openDB();
			preparedStatement = connection.prepareStatement("UPDATE users SET user_OTP=null " + "WHERE username=?");
			preparedStatement.setString(1, username);
			preparedStatement.executeUpdate();

			preparedStatement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Generates a random six digit number that will be used as the OTP 
	 * and stores OTP inside database for future comparison
	 * 
	 * @param	username	for whom the OTP is for
	 * @return	six digit integer
	 */
	public int randomSixDigitCode(String username) {
		// Randomization with SecureRandom for less chance of collision i.e.
		// more securely & uniquely random OTP
		int otp = sr.nextInt(900000) + 100000;
		try {
			// Update database with generated OTP
			connection = DBAccess.getInstance().openDB();
			preparedStatement = connection.prepareStatement("UPDATE users SET user_OTP=? " + "WHERE username=?");
			preparedStatement.setString(1, String.valueOf(otp));
			preparedStatement.setString(2, username);
			preparedStatement.executeUpdate();

			preparedStatement.close();
			connection.close();;
		} catch (SQLException e) {
			otp = 0;
		} catch (Exception e) {
			otp = 0;
		}
		return otp;
	}
	
	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
    	System.out.println("**********************************                                             ***********************************************************");
        //Destroy all created threads, if any
    	if(executorService1!=null)
    		executorService1.shutdownNow();
    }
}
