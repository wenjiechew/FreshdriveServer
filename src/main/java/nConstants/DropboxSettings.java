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
	private JSONParser parser = new JSONParser();
	private String AccessToken = null;
	
	public DropboxSettings(){
		readJSONFile();
	}
	
	public String getAccessToken() {
		return AccessToken;
	}

	public void setAccessToken(String accessToken) {
		AccessToken = accessToken;
	}

	
	
	private void readJSONFile(){

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
