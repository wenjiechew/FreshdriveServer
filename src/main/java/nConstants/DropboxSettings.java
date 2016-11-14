/**
 * 
 */
package nConstants;

import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author WenJieChew
 *
 */
public class DropboxSettings {
	private static DropboxSettings instance;
	
	private static JSONParser parser = new JSONParser();
	private static String AccessToken = null;
	
	public static DropboxSettings getInstance(){
		if( instance == null ) {
			
			instance = new DropboxSettings();
			readJSONFile();			
		}	
		return instance;
	}
	
	public String getAccessToken() {
		return AccessToken;
	}

	public static void setAccessToken(String accessToken) {
		AccessToken = accessToken;
	}	
	
	private static void readJSONFile(){

        try { 
            Object obj = parser.parse(new FileReader(
                   Constants.getCurrentPath() ));
 
            JSONObject jsonObject = (JSONObject) obj;
 
            setAccessToken(  (String) jsonObject.get("DropBoxAccessToken") );

             
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
