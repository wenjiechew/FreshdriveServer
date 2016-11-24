package nConstants;

import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * This class reads From a file , and returns it Database Settings Related values.
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
	 * @return the instance of the object created
	 */
	public static DBSettings getInstance() {
		if( instance == null ) { 
			instance = new DBSettings(); 
			readJSONFile();
		}	
		return instance;		
	}		

	/**
	 * 
	 * @return Datebase URL
	 */
	public String getDB_URL() {
		return new String(DB_URL);
	}

	/**
	 * Sets the private local variable DB_URL
	 * @param dB_URL
	 */
	private static void setDB_URL(String dB_URL) {
		DB_URL = dB_URL;
	}
	
	/**
	 * @return the User name
	 */
	public String getDB_USER() {
		return new String(DB_USER);
	}

	/**
	 * Sets the private local variable DB_USER
	 * @param dB_URL
	 */
	private static void setDB_USER(String dB_USER) {
		DB_USER = dB_USER;
	}

	/**
	 * @return the password
	 */
	public String getDB_PASS() {
		return new String(DB_PASS);
	}

	/**
	 * Sets the private local variable DB_PASS
	 * @param dB_URL
	 */
	private static void setDB_PASS(String dB_PASS) {
		DB_PASS = dB_PASS;
	}	
	
	/**
	 * Reads an JSON file from a stated location, and collects and sets the it's value appropriately.
	 */
	private static void readJSONFile(){

        try { 
            Object obj = parser.parse(new FileReader(
                   Constants.getConfigPath() ));
 
            JSONObject jsonObject = (JSONObject) obj;
 
            setDB_URL(  (String) jsonObject.get("DB_URL"));
            setDB_USER( (String) jsonObject.get("DB_USER"));
            setDB_PASS( (String) jsonObject.get("DB_PASS") );
             
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	

	
	
	
}
