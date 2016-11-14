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
	private static DBSettings instance;
	private static JSONParser parser = new JSONParser();
	
	private static String DB_URL = null;
	private static String DB_USER = null;
	private static String DB_PASS = null;	
	
	/**
	 * 
	 */
	public static DBSettings getInstance() {
		if( instance == null ) { 
			instance = new DBSettings(); 
			readJSONFile();
		}	
		return instance;		
	}		

	public String getDB_URL() {
		return DB_URL;
	}

	private static void setDB_URL(String dB_URL) {
		DB_URL = dB_URL;
	}
	
	public String getDB_USER() {
		return DB_USER;
	}

	private static void setDB_USER(String dB_USER) {
		DB_USER = dB_USER;
	}

	public String getDB_PASS() {
		return DB_PASS;
	}

	private static void setDB_PASS(String dB_PASS) {
		DB_PASS = dB_PASS;
	}	
	
	private static void readJSONFile(){

        try { 
            Object obj = parser.parse(new FileReader(
                   Constants.getCurrentPath() ));
 
            JSONObject jsonObject = (JSONObject) obj;
 
            setDB_URL(  (String) jsonObject.get("DB_URL"));
            setDB_USER( (String) jsonObject.get("DB_USER"));
            setDB_PASS( (String) jsonObject.get("DB_PASS") );
             
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	

	
	
	
}
