package nConstants;

import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class EmailSettings {
	private static EmailSettings instance;
	private static JSONParser parser = new JSONParser();

	private static String EmailAddress = null;
	private static String EmailPass = null;
	
	public static EmailSettings getInstance(){
		if( instance == null ){
			instance = new EmailSettings();
			readJSONFile();
		}
		return instance;
	}
	
	public String getEmailPass() {
		return EmailPass;
	}
	private static void setEmailPass(String emailPass) {
		EmailPass = emailPass;
	}
	public String getEmailAddress() {
		return EmailAddress;
	}
	private static void setEmailAddress(String emailAddress) {
		EmailAddress = emailAddress;
	}
	
	private static void readJSONFile(){

        try { 
            Object obj = parser.parse(new FileReader(
                   Constants.getCurrentPath() ));
 
            JSONObject jsonObject = (JSONObject) obj;
 
            setEmailAddress(  (String) jsonObject.get("Email"));
            setEmailPass( (String) jsonObject.get("EmailPass"));                   
           
 
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
