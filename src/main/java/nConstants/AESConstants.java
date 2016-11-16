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

/**
 * This class reads From a file, and returns it appropriate values.
 * @author WenJieChew
 *
 */
public class AESConstants {
	private static AESConstants instance;
	private static JSONParser parser = new JSONParser();
	
	private static String AESPass = null;
	
	/**
	 * 
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
	 * 
	 * @return AES Password
	 */
	public String getAESPass() {
		return AESPass;
	}

	/**
	 * Sets local variable to the value parsed in
	 * @param aESPass
	 */
	private static void setAESPass(String aESPass) {
		AESPass = aESPass;
	}
	
	/**
	 * Reads an JSON file from a stated location, and collects and sets the it's value appropriately.
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
