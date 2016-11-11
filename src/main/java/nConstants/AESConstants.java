package nConstants;

import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class AESConstants {
	private JSONParser parser = new JSONParser();
	
	private static String AESPass = null;
	
	public AESConstants(){
		readJSONFile();
	}

	public String getAESPass() {
		return AESPass;
	}

	private void setAESPass(String aESPass) {
		AESPass = aESPass;
	}
	
	private void readJSONFile(){

        try { 
            Object obj = parser.parse(new FileReader(
            		Constants.getCurrentPath() ));
 
            JSONObject jsonObject = (JSONObject) obj;

            setAESPass(  ( (JSONArray) jsonObject.get("AESPass") ).toString() );          
           
 
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	

}
