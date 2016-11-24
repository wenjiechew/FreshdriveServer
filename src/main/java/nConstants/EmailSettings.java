package nConstants;

import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * This class reads From a file , and returns it Email Settings Related values.
 * 
 * @author WenJieChew
 */
public class EmailSettings {
	private static EmailSettings instance;
	private static JSONParser parser = new JSONParser();

	private static String EmailAddress = null;
	private static String EmailPass = null;
	
	/**
	 * 
	 * @return the instance of the object created
	 */
	public static EmailSettings getInstance(){
		if( instance == null ){
			instance = new EmailSettings();
			readJSONFile();
		}
		return instance;
	}
	
	/**
	 * 
	 * @return the Application's Email Configuration Password
	 */
	public String getEmailPass() {
		return new String(EmailPass);
	}
	
	/**
	 * sets the local variable Email password
	 * @param emailPass
	 */
	private static void setEmailPass(String emailPass) {
		EmailPass = emailPass;
	}
	
	/**
	 * 
	 * @return the Application's Email Configuration email address
	 */
	public String getEmailAddress() {
		return new String(EmailAddress);
	}
	
	/**
	 * Sets the local variable email address
	 * @param emailAddress
	 */
	private static void setEmailAddress(String emailAddress) {
		EmailAddress = emailAddress;
	}
	
	/**
	 * Reads an JSON file from a stated location, and collects and sets the it's value appropriately.
	 */
	private static void readJSONFile(){

        try { 
            Object obj = parser.parse(new FileReader(
                   Constants.getConfigPath() ));
 
            JSONObject jsonObject = (JSONObject) obj;
 
            setEmailAddress(  (String) jsonObject.get("Email"));
            setEmailPass( (String) jsonObject.get("EmailPass"));                   
           
 
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
