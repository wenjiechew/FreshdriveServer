package nUtillities;

import org.apache.log4j.Logger;

/**
 * This logger file is implemented to log key events in the server
 */
public class Log {

	final static Logger logger = Logger.getLogger(Log.class);

	/**
	 * The log function to write the key events message into the log
	 * 
	 * @param msg
	 *            the message passed in to write into the log
	 */
	public void log(String msg) {
		logger.info(msg);
	}

	/**
	 * The log function to write the key events message into the log as a
	 * warning message
	 * 
	 * @param msg
	 *            the message passed in to write into the log
	 */

	public void warn(String msg) {
		logger.warn(msg);
	}

}
