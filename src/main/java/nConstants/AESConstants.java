package nConstants;

/**
 * This class reads From a file , and returns it CryptoKeys Related values.
 * @author WenJieChew
 *
 */
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class AESConstants implements Cloneable {
	private static AESConstants instance;
	private static JSONParser parser = new JSONParser();
	
	private static String AESPass = null;
	
	/**
	 * Get instance of the AESConstants
	 * @return the instance of the object created
	 */
	public static AESConstants getInstance(){
		if(instance == null){
			instance = new AESConstants();
			readJSONFile();
		}
		return instance;
	}

	/**
	 * Retrieves the AES password String
	 * @return AES Password
	 */
	public String getAESPass() {
		return new String(AESPass);
	}

	/**
	 * Sets local variable to the value parsed in
	 * @param aESPass
	 */
	private static void setAESPass(String aESPass) {
		AESPass = aESPass;
	}
	
	/**
	 * Reads an JSON file from a stated location, then collects and sets its value appropriately.
	 */
	private static void readJSONFile(){

        try { 
            Object obj = parser.parse(new FileReader(
            		Constants.getConfigPath() ));
 
            JSONObject jsonObject = (JSONObject) obj;

            setAESPass(  ( (JSONArray) jsonObject.get("AESPass") ).toString() );          
           
 
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	

}
