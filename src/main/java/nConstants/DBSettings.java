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
public class DBSettings {	
	private JSONParser parser = new JSONParser();
	
	private String DB_URL = null;
	private String DB_USER = null;
	private String DB_PASS = null;	
	
	/**
	 * 
	 */
	public DBSettings() {
		readJSONFile();
	}		

	public String getDB_URL() {
		return DB_URL;
	}

	private void setDB_URL(String dB_URL) {
		DB_URL = dB_URL;
	}
	
	public String getDB_USER() {
		return DB_USER;
	}

	private void setDB_USER(String dB_USER) {
		DB_USER = dB_USER;
	}

	public String getDB_PASS() {
		return DB_PASS;
	}

	private void setDB_PASS(String dB_PASS) {
		DB_PASS = dB_PASS;
	}	
	
	private void readJSONFile(){

        try { 
            Object obj = parser.parse(new FileReader(
                   Constants.getCurrentPath() ));
 
            JSONObject jsonObject = (JSONObject) obj;
 
            setDB_URL(  (String) jsonObject.get("DB_URL"));
            setDB_USER( (String) jsonObject.get("DB_USER"));
            setDB_PASS( (String) jsonObject.get("DB_PASS") );
            
            System.out.println(Constants.getCurrentPath());

           
 
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	

	
	
	
}
