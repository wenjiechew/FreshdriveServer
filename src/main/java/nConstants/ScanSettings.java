package nConstants;

import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * This class reads From a file , and returns it ScanFiles Settings Related values.
 * 
 * @author WenJieChew
 */
public class ScanSettings {
	private static ScanSettings instance;
	private static JSONParser parser = new JSONParser();
	
	private static String ScanKey = null;

	/**
	 * @return the instance of the object created
	 */
	public static ScanSettings getInstance() {
		if( instance == null){
			instance =  new ScanSettings();
			readJSONFile();
		}
		return instance;
	}

	/**
	 * 
	 * @return the VirusScan Key
	 */
	public String getScanKey() {
		return ScanKey;
	}

	/**
	 * Sets the local variable for the scan key
	 * @param scanKey
	 */
	private static void setScanKey(String scanKey) {
		ScanKey = scanKey;
	}
	
	/**
	 * Reads an JSON file from a stated location, and collects and sets the it's value appropriately.
	 */
	private static void readJSONFile(){

        try { 
            Object obj = parser.parse(new FileReader(
                   Constants.getConfigPath() ));
 
            JSONObject jsonObject = (JSONObject) obj;
 
            setScanKey(  (String) jsonObject.get("ScanKey"));
             
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
