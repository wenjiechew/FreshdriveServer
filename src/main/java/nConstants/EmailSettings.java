package nConstants;

import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class EmailSettings {
	private JSONParser parser = new JSONParser();

	private String EmailAddress = null;
	private String EmailPass = null;
	
	public EmailSettings(){
		readJSONFile();
	}
	
	public String getEmailPass() {
		return EmailPass;
	}
	private void setEmailPass(String emailPass) {
		EmailPass = emailPass;
	}
	public String getEmailAddress() {
		return EmailAddress;
	}
	private void setEmailAddress(String emailAddress) {
		EmailAddress = emailAddress;
	}
	
	private void readJSONFile(){

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
