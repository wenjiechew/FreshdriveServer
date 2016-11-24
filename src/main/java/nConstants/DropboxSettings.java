package nConstants;

/**
 * This class reads From a file , and returns it Dropbox Settings Related values.
 * 
 * @author WenJieChew
 */
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author WenJieChew
 *
 */
public class DropboxSettings {
	private static DropboxSettings instance;

	private static JSONParser parser = new JSONParser();
	private static String AccessToken = null;

	/**
	 * 
	 * @return the instance of the object created
	 */
	public static DropboxSettings getInstance() {
		if (instance == null) {

			instance = new DropboxSettings();
			readJSONFile();
		}
		return instance;
	}

	/**
	 * @return the AccessToken of the Database
	 */

	public String getAccessToken() {
		return new String(AccessToken);
	}

	/**
	 * Clears the instance when the access token has be retrieved,to clear the
	 * access token away from the memory
	 */
	public static void clearInstance() {

		if (instance != null) {

			instance = null;

		}

	}

	/**
	 * Sets the local variable AccessToken
	 * 
	 * @param accessToken
	 */
	private static void setAccessToken(String accessToken) {
		AccessToken = accessToken;
	}

	/**
	 * Reads an JSON file from a stated location, and collects and sets the it's
	 * value appropriately.
	 */
	private static void readJSONFile() {

		try {
			Object obj = parser.parse(new FileReader(Constants.getConfigPath()));

			JSONObject jsonObject = (JSONObject) obj;

			setAccessToken((String) jsonObject.get("DropBoxAccessToken"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
