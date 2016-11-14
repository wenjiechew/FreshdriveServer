/**
 * This 
 */
package nConstants;

import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author WenJieChew
 *
 */
public class ScanSettings {
	private static ScanSettings instance;
	private static JSONParser parser = new JSONParser();
	
	private static String ScanKey = null;

	/**
	 * 
	 */
	public static ScanSettings getInstance() {
		if( instance == null){
			instance =  new ScanSettings();
			readJSONFile();
		}
		return instance;
	}

	public String getScanKey() {
		return ScanKey;
	}

	private static void setScanKey(String scanKey) {
		ScanKey = scanKey;
	}
	
	private static void readJSONFile(){

        try { 
            Object obj = parser.parse(new FileReader(
                   Constants.getCurrentPath() ));
 
            JSONObject jsonObject = (JSONObject) obj;
 
            setScanKey(  (String) jsonObject.get("ScanKey"));
             
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
