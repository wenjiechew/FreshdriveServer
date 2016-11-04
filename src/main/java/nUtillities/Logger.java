package nUtillities;

/**
 * This Class file is to facilitate Logging
 * 
 * @author WenJieChew
 *
 */
public class Logger {
	private static Logger instance;

	public static Logger getInstance() {
		if ( instance == null ) { instance = new Logger(); }
		return instance;
	}
	
	public void PrintError (String functionName, String msg){
		System.err.println("[Error] in [" + functionName + "] " + msg);		
	}
	
	public void PrintInfo (String msg){
		System.out.println("[Information] " + msg);
	}
	
	public void PrintInfo (String functionName, String msg){
		System.out.println("[Information] in [" + functionName + "] " + msg);
	}
	
	
	public void PrintWarning (String functionName, String msg){
		System.out.println("[Warning] in [" + functionName + "] " + msg);
	}
	

}
