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
	private JSONParser parser = new JSONParser();
	
	private String ScanKey = null;

	/**
	 * 
	 */
	public ScanSettings() {
		readJSONFile();
	}

	public String getScanKey() {
		return ScanKey;
	}

	private void setScanKey(String scanKey) {
		ScanKey = scanKey;
	}
	
	private void readJSONFile(){

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
