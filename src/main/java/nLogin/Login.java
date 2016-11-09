package nLogin;

import java.io.IOException;
import java.io.PrintWriter;

import java.math.BigInteger;

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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.security.KeyManagementException;
import java.security.SecureRandom;

import java.util.Random;
import java.util.Properties;

import nDatabase.DBAccess;
import nObjectModel.Account;
import nUtillities.Logger;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection connection;
	private static PreparedStatement preparedStatement;
	private static Properties mailServerProperties;
	private static Session getMailSession;
	private static MimeMessage generateMailMessage;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Logger.getInstance().PrintInfo("User Response POST === " + request.getParameter("username") + " AND "
				+ request.getParameter("password"));

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		Account account = new Account();
		account.setUsername(request.getParameter("username"));
		account.setPassword(request.getParameter("password"));
		if (Validate.checkUser(account)) {
			Logger.getInstance().PrintInfo("Account : SUCCESSFULLY Validate");
			Logger.getInstance().PrintInfo("User name: " + account.getUsername());
			try {
				Logger.getInstance().PrintInfo(getEmail(account.getUsername()));
			} catch (Exception ex) {
				Logger.getInstance().PrintInfo(ex.toString());
			}

			// Generate token
			account.setToken(genToken());
			response.setContentType("text/html");
			// Insert into database
			if (Validate.insertToken(account) == 0) {
				// If no records were updated (i.e. 0 rows updated)
				// Active token existing; someone is currently logged in with
				// the account
				out.println("active-token");
			} else
				out.println(account.getToken()); // Return access token as
													// results
		} else {
			Logger.getInstance().PrintInfo("Account : is NOT Validate");
			// TODO Keep track of number of times user has failed login
			response.setContentType("text/html");

			out.println("1");
		}
	}

	public String genToken() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}

	public String getEmail(String userName) throws IOException, KeyManagementException, MessagingException {
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
			DBAccess.getInstance().closeDB();

		} catch (SQLException e) {
			Logger.getInstance().PrintError("openDB() ", e.toString());
		} catch (Exception e) {
			Logger.getInstance().PrintError("openDB() ", e.toString());
		}
		sendEmail(email);
		return email;
	}

	public void sendEmail(String email) throws IOException, KeyManagementException, MessagingException {
		// Step1
		System.out.println("\n 1st ===> setup Mail Server Properties..");
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		System.out.println("Mail Server Properties have been setup successfully..");

		// Step2
		System.out.println("\n\n 2nd ===> get Mail Session..");
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		generateMailMessage = new MimeMessage(getMailSession);
		generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
		generateMailMessage.setSubject("Freshdrive Log in validation");
		String emailBody = "Dear User, <br><br> Please enter the following code for validating your login: " + randomSixDigitCode()
				+ "<br><br> Regards, <br>Freshdrive Admin";
		generateMailMessage.setContent(emailBody, "text/html");
		System.out.println("Mail Session has been created successfully..");

		// Step3
		System.out.println("\n\n 3rd ===> Get Session and Send mail");
		Transport transport = getMailSession.getTransport("smtp");

		// for testing purposes you can enter your gmail account and pw.However
		// you have to go to this link
		// to turn on access for less secure apps
		// https://www.google.com/settings/security/lesssecureapps
		// ideally creating a gmail account specfic for freshdrive would be
		// better
		transport.connect("smtp.gmail.com", "freshdrive3103@gmail.com", "Qwerty1@3$");
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();
	}

	public int randomSixDigitCode() {
		Random r = new Random();
		int sixDigitCode = r.nextInt(1000000 - 100000) + 100000;
		return sixDigitCode;
	}
}
